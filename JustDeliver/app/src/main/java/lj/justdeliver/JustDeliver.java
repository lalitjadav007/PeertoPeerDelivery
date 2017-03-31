package lj.justdeliver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.User;

public class JustDeliver extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_just_deliver);
        init();
        checkLogin();
    }

    private void init() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        findViewById(R.id.btnSignIn).setOnClickListener(this);
        findViewById(R.id.tvRegisterMe).setOnClickListener(this);
    }

    private void checkLogin() {
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String asDriver = preferences.getString("asDriver", "");
        String licenceNumber = preferences.getString("licenceNumber", "");


        if (!asDriver.equals("true") && !email.equals("")) {
            CommonConstants.isDriver = false;
            startActivity(new Intent(JustDeliver.this, DriverHomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (asDriver.equals("true") && licenceNumber.equals("")) {
            User me = new User();
            me.phone = preferences.getString("phone", "");
            me.email = preferences.getString("email", "");
            me.uid = preferences.getString("uid", "");
            me.fullName = preferences.getString("fullName", "");
            me.asDriver = preferences.getString("asDriver", "");
            me.profilePic = preferences.getString("profilePic", "");

            Intent intent = new Intent(JustDeliver.this, DriverDetailsActivity.class);
            intent.putExtra("user", me);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (asDriver.equals("true") && !licenceNumber.equals("")) {
            CommonConstants.isDriver = true;
            startActivity(new Intent(JustDeliver.this, DriverHomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    displaySnackbar(getString(R.string.error_invalid_email));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    displaySnackbar(getString(R.string.error_password_field_empty));
                    return;
                }

                showProgress();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                            userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    hideProgress();
                                    User me = dataSnapshot.getValue(User.class);
                                    SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("phone", me.phone);
                                    editor.putString("email", me.email);
                                    editor.putString("uid", me.uid);
                                    editor.putString("fullName", me.fullName);
                                    editor.putString("asDriver", me.asDriver);
                                    editor.putString("profilePic", me.profilePic);
                                    if (me.driver != null) {
                                        editor.putString("licenceNumber", me.driver.licenceNumber);
                                        editor.putString("validDate", me.driver.validDate);
                                        editor.putString("vehicalNumber", me.driver.vehicalNumber);
                                        editor.putString("vehicalType", me.driver.vehicalType);
                                        editor.putString("status", me.driver.status);
                                    }
                                    editor.apply();

                                    if (!me.asDriver.equals("true")) {
                                        CommonConstants.isDriver = false;
                                        startActivity(new Intent(JustDeliver.this, DriverHomeActivity.class));
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    } else if (me.asDriver.equals("true") && (me.driver == null || me.driver.licenceNumber.equals(""))) {
                                        Intent intent = new Intent(JustDeliver.this, DriverDetailsActivity.class);
                                        intent.putExtra("user", me);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    } else {
                                        CommonConstants.isDriver = true;
                                        startActivity(new Intent(JustDeliver.this, DriverHomeActivity.class));
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            try {
                                displaySnackbar(task.getException().getMessage());
                            } catch (Exception e) {
                                displaySnackbar(getString(R.string.something_went_wrong));
                            }
                        }
                    }
                });
                break;
            case R.id.tvRegisterMe:
                startActivity(new Intent(this, RegisterActivity.class));
//                startActivity(new Intent(this, RegisterNewUserActivity .class));
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etEmail, message, Snackbar.LENGTH_SHORT).show();
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
}
