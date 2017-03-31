package lj.justdeliver;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import lj.justdeliver.fragments.NameNumberFragment;
import lj.justdeliver.fragments.UserAndEmailFragment;

public class RegisterNewUserActivity extends AppCompatActivity implements UserAndEmailFragment.UserAndEmailListener,
        NameNumberFragment.OnUserRegister {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentRegister, UserAndEmailFragment.newInstance());
        transaction.commit();
    }

    @Override
    public void onEmailCompleted(String email, String uid) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentRegister, NameNumberFragment.newInstance(email, uid));
        transaction.commit();
    }

    @Override
    public void onAlresyUserExist() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onRegisterd() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
