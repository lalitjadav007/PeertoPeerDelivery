package lj.justdeliver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import lj.justdeliver.model.User;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etFullName, etEmailRegister, etPassword, etConfirmPassword, etPhone;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;
    private StorageReference imageRef;
    private Uri profilePic;
    private ImageView ivProfilePic;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://just-deliver.appspot.com");

        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmailRegister = (EditText) findViewById(R.id.etEmailRegister);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etPhone = (EditText) findViewById(R.id.etPhone);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        findViewById(R.id.btnAsDriver).setOnClickListener(this);
        findViewById(R.id.btnAsUser).setOnClickListener(this);
        findViewById(R.id.tvEditImage).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvEditImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
                break;
            case R.id.btnAsDriver: {
                final String fullName, email, password, confirmPassword, phone;
                fullName = etFullName.getText().toString().trim();
                email = etEmailRegister.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();
                phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    displaySnackbar(getString(R.string.error_name_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    displaySnackbar(getString(R.string.error_invalid_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    displaySnackbar(getString(R.string.error_password_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    displaySnackbar(getString(R.string.error_confirm_password_empty));
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    displaySnackbar(getString(R.string.error_password_not_match));
                    return;
                }


                if (TextUtils.isEmpty(phone)) {
                    displaySnackbar(getString(R.string.error_phone_field_empty));
                    return;
                }

                if (profilePic == null) {
                    displaySnackbar(getString(R.string.error_select_profile_pic));
                    return;
                }

                showProgress();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final AuthResult authresult = task.getResult();
                            final String uid = authresult.getUser().getUid();
                            UploadTask imageUpload = imageRef.child(uid).putFile(profilePic);
                            imageUpload.addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        UploadTask.TaskSnapshot result = task.getResult();
                                        String picUrl = result.getDownloadUrl() != null ? result.getDownloadUrl().toString() : "";
                                        final User user = new User(fullName, email, phone, "true", uid, picUrl, null);
                                        dbRef.child(user.uid).setValue(user).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                hideProgress();
                                                displaySnackbar("register");
                                                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("phone", user.phone);
                                                editor.putString("email", user.email);
                                                editor.putString("uid", user.uid);
                                                editor.putString("fullName", user.fullName);
                                                editor.putString("asDriver", user.asDriver);
                                                editor.putString("profilePic", user.profilePic);
                                                editor.apply();
                                                Intent intent1 = new Intent(RegisterActivity.this, DriverDetailsActivity.class);
                                                intent1.putExtra("user", user);
                                                startActivity(intent1);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            }
                                        });
                                    } else {
                                        displaySnackbar(getString(R.string.something_went_wrong));
                                    }
                                }
                            });
                        } else {
                            displaySnackbar(getString(R.string.something_went_wrong));
                        }
                    }
                });
            }
            break;

            case R.id.btnAsUser:
                final String fullName, email, password, confirmPassword, phone;
                fullName = etFullName.getText().toString().trim();
                email = etEmailRegister.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();
                phone = etPhone.getText().toString().trim();

                if (TextUtils.isEmpty(fullName)) {
                    displaySnackbar(getString(R.string.error_name_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    displaySnackbar(getString(R.string.error_invalid_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    displaySnackbar(getString(R.string.error_password_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    displaySnackbar(getString(R.string.error_confirm_password_empty));
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    displaySnackbar(getString(R.string.error_password_not_match));
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    displaySnackbar(getString(R.string.error_phone_field_empty));
                    return;
                }

                if (profilePic == null) {
                    displaySnackbar(getString(R.string.error_select_profile_pic));
                    return;
                }

                showProgress();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final AuthResult authresult = task.getResult();
                            final String uid = authresult.getUser().getUid();
                            UploadTask imageUpload = imageRef.child(uid).putFile(profilePic);
                            imageUpload.addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        UploadTask.TaskSnapshot result = task.getResult();
                                        String picUrl = result.getDownloadUrl() != null ? result.getDownloadUrl().toString() : "";
                                        final User user = new User(fullName, email, phone, "false", uid, picUrl, null);
                                        dbRef.child(user.uid).setValue(user).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                hideProgress();
                                                displaySnackbar("register");
                                                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("phone", user.phone);
                                                editor.putString("email", user.email);
                                                editor.putString("uid", user.uid);
                                                editor.putString("fullName", user.fullName);
                                                editor.putString("asDriver", user.asDriver);
                                                editor.putString("profilePic", user.profilePic);
                                                editor.apply();
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            }
                                        });
                                    } else {
                                        displaySnackbar(getString(R.string.something_went_wrong));
                                    }
                                }
                            });
                        } else {
                            displaySnackbar(getString(R.string.something_went_wrong));
                        }
                    }
                });
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etFullName, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.show();
    }

    private void hideProgress() {
        progressDialog.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            profilePic = data.getData();
            Glide.with(this).load(profilePic).centerCrop().into(ivProfilePic);
            return;
        }
    }

}
