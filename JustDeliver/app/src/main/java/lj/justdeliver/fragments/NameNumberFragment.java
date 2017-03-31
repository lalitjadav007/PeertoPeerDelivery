package lj.justdeliver.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;
import lj.justdeliver.R;
import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.User;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserRegister} interface
 * to handle interaction events.
 * Use the {@link NameNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameNumberFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMAIL = "email";
    private static final String ARG_UID = "uid";
    User user = new User();
    // TODO: Rename and change types of parameters
    private String email;
    private String uid;
    private OnUserRegister mListener;
    private EditText etFullName, etPhone;
    private CircleImageView profile_image;
    private Uri mCropImageUri;
    private boolean isImageUploading = false;
    private ProgressBar pbImageUpload;

    public NameNumberFragment() {
        // Required empty public constructor
    }

    public static NameNumberFragment newInstance(String param1, String param2) {
        NameNumberFragment fragment = new NameNumberFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, param1);
        args.putString(ARG_UID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                startActivityForResult(CropImage.getPickImageChooserIntent(getContext()), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
                break;

            case R.id.btnEmailNext:
                final String fullName, phone;

                fullName = etFullName.getText().toString().trim();
                phone = etPhone.getText().toString().trim();

                if (isImageUploading) {
                    displaySnackbar(getString(R.string.msg_please_wait_for_image_upload));
                    return;
                }

                if (TextUtils.isEmpty(fullName)) {
                    displaySnackbar(getString(R.string.error_name_field_empty));
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    displaySnackbar(getString(R.string.error_phone_field_empty));
                    return;
                }

                CommonConstants.showProgress(getContext(), getString(R.string.msg_please_wait));
                user.fullName = fullName;
                user.phone = phone;
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
                dbRef.child(uid).setValue(user).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CommonConstants.hideProgress();
                        onButtonPressed();
                    }
                });

                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etFullName, message, Snackbar.LENGTH_SHORT).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onRegisterd();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(getContext(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Glide.with(this).load(result.getUri()).into(profile_image);
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://just-deliver.appspot.com");
            isImageUploading = true;
            pbImageUpload.setVisibility(View.VISIBLE);
            UploadTask imageUpload = imageRef.child(uid).putFile(result.getUri());
            imageUpload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    isImageUploading = false;
                    pbImageUpload.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                        user.profilePic = task.getResult().getDownloadUrl().toString();
                    }
                }
            });
            imageUpload.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long byteTransfered = taskSnapshot.getBytesTransferred();
                    long byteToTransfered = taskSnapshot.getTotalByteCount();
                    pbImageUpload.setProgress((int) ((byteTransfered * 100) / byteToTransfered));
                }
            });
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(getContext(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserRegister) {
            mListener = (OnUserRegister) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserRegister");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            uid = getArguments().getString(ARG_UID);
            user.email = email;
            user.uid = uid;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name_number, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);
        etFullName = (EditText) view.findViewById(R.id.etFullName);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        view.findViewById(R.id.btnEmailNext).setOnClickListener(this);
        pbImageUpload = (ProgressBar) view.findViewById(R.id.pbImageUpload);
        pbImageUpload.setMax(100);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setActivityMenuIconColor(Color.parseColor("#3F51B5"))
                .setAspectRatio(200, 200)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getContext(), this);
    }

    public interface OnUserRegister {
        // TODO: Update argument type and name
        void onRegisterd();
    }
}
