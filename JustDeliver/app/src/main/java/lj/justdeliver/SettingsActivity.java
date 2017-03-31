package lj.justdeliver;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import lj.justdeliver.model.User;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etFullName, etPhone;
    private ImageView ivProfilePic;
    private Uri profilePic;
    private User user;
    private DatabaseReference dbRef;
    private StorageReference imageRef;
    private ProgressBar pbImageLoad, pbEditPtofile;
    private Button btnEditProfile;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(SettingsActivity.this, data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(SettingsActivity.this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                profilePic = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            profilePic = result.getUri();
            Glide.with(SettingsActivity.this).load(result.getUri()).into(ivProfilePic);
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://just-deliver.appspot.com");
            pbImageLoad.setVisibility(View.VISIBLE);
            pbImageLoad.setProgress(0);
            UploadTask imageUpload = imageRef.child(user.uid).putFile(result.getUri());
            imageUpload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                        user.profilePic = task.getResult().getDownloadUrl().toString();
                        dbRef.child(user.uid).child("profilePic").setValue(user.profilePic).addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (pbImageLoad != null)
                                    pbImageLoad.setVisibility(View.GONE);

                                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("phone", user.phone);
                                editor.putString("email", user.email);

                                editor.putString("uid", user.uid);
                                editor.putString("fullName", user.fullName);
                                editor.putString("asDriver", user.asDriver);
                                editor.putString("profilePic", user.profilePic);
                                editor.apply();
                            }
                        });
                        pbImageLoad.setVisibility(View.GONE);
                    }
                }
            });
            imageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long byteTransfered = taskSnapshot.getBytesTransferred();
                    long byteToTransfered = taskSnapshot.getTotalByteCount();
                    pbImageLoad.setProgress((int) ((byteTransfered * 100) / byteToTransfered));
                }
            });
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setActivityMenuIconColor(Color.parseColor("#3F51B5"))
                .setAspectRatio(200, 200)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        getSharedData();
        setData();
    }

    private void init() {
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://just-deliver.appspot.com");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.label_settings));
        toolbar.setNavigationContentDescription("Lalit");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        etFullName = (EditText) findViewById(R.id.etFullName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(this);

        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(this);

        pbImageLoad = (ProgressBar) findViewById(R.id.pbImageLoad);
        pbEditPtofile = (ProgressBar) findViewById(R.id.pbEditPtofile);
    }

    private void getSharedData() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String fullName = preferences.getString("fullName", "");
        String phone = preferences.getString("phone", "");
        String email = preferences.getString("email", "");
        String uid = preferences.getString("uid", "");
        String asDriver = preferences.getString("asDriver", "");
        String picUrl = preferences.getString("profilePic", "");

        user = new User(fullName, email, phone, asDriver, uid, picUrl, null);
    }

    private void setData() {
        etFullName.setText(user.fullName);
        etPhone.setText(user.phone);
        Glide.with(this).load(user.profilePic).centerCrop().into(ivProfilePic);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivProfilePic:
                startActivityForResult(CropImage.getPickImageChooserIntent(SettingsActivity.this), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
                break;


            case R.id.btnEditProfile:
                final String fullName, phone;
                fullName = etFullName.getText().toString().trim();
                phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    displaySnackbar(getString(R.string.error_name_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    displaySnackbar(getString(R.string.error_phone_field_empty));
                    return;
                }


                user.fullName = fullName;
                user.phone = phone;

                if (pbEditPtofile != null)
                    pbEditPtofile.setVisibility(View.VISIBLE);
                if (btnEditProfile != null)
                    btnEditProfile.setVisibility(View.INVISIBLE);

                dbRef.child(user.uid).setValue(user).addOnCompleteListener(SettingsActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (pbEditPtofile != null)
                            pbEditPtofile.setVisibility(View.GONE);
                        if (btnEditProfile != null)
                            btnEditProfile.setVisibility(View.VISIBLE);

                        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("phone", user.phone);
                        editor.putString("email", user.email);
                        editor.putString("uid", user.uid);
                        editor.putString("fullName", user.fullName);
                        editor.putString("asDriver", user.asDriver);
                        editor.putString("profilePic", user.profilePic);
                        editor.apply();

                        displaySnackbar(getString(R.string.message_profile_edited));
                    }
                });
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etFullName, message, Snackbar.LENGTH_SHORT).show();
    }
}
