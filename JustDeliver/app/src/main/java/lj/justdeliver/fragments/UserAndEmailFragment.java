package lj.justdeliver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import lj.justdeliver.R;
import lj.justdeliver.helper.CommonConstants;

public class UserAndEmailFragment extends Fragment implements View.OnClickListener {


    private UserAndEmailListener mListener;
    private EditText etEmail, etPassword, etConfirmPassword;

    public UserAndEmailFragment() {
        // Required empty public constructor
    }

    public static UserAndEmailFragment newInstance() {
        UserAndEmailFragment fragment = new UserAndEmailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserAndEmailListener) {
            mListener = (UserAndEmailListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserRegister");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_and_email, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) view.findViewById(R.id.etConfirmPassword);
        view.findViewById(R.id.btnEmailNext).setOnClickListener(this);
        view.findViewById(R.id.btnSkipRegister).setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEmailNext:
                final String email, password, confirmPassword;
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                confirmPassword = etConfirmPassword.getText().toString().trim();

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

                CommonConstants.showProgress(getContext(), getString(R.string.msg_please_wait));
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        CommonConstants.hideProgress();
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            onButtonPressed(email, uid);
                        } else {
                            displaySnackbar(task.getException().getMessage());
                        }
                    }
                });

                break;

            case R.id.btnSkipRegister:
                if (mListener != null) {
                    mListener.onAlresyUserExist();
                }
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etEmail, message, Snackbar.LENGTH_SHORT).show();
    }

    public void onButtonPressed(String email, String uid) {
        if (mListener != null) {
            mListener.onEmailCompleted(email, uid);
        }
    }

    public interface UserAndEmailListener {
        // TODO: Update argument type and name
        void onEmailCompleted(String email, String uid);

        void onAlresyUserExist();
    }

}
