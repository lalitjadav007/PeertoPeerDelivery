package lj.justdeliver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import lj.justdeliver.helper.CommonConstants;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ArrayList<String> status = new ArrayList<>();
        status.add("Selected");
        status.add("Picked up");
        status.add("Confirm pick up");
        status.add("On the way");
        status.add("Deliverd");
        status.add("Confirm deliverd");
        status.add("Payed");
        CommonConstants.status = status;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, JustDeliver.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 1000);
    }
}
