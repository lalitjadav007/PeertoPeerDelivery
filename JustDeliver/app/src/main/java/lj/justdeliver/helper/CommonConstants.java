package lj.justdeliver.helper;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by lj on 2/23/2017.
 */

public class CommonConstants {
    public static final float BASE_FARE = 0.01f; // 10 rs/km
    public static final float MIN_FARE = 50;
    public static final String distanceUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=%s&destinations=%s&key=AIzaSyBPleNqFTUxWwucP1QKJ7GdSYjbXUPCGM4";
    public static boolean isDriver = false;
    public static ArrayList<String> status;
    private static ProgressDialog progressDialog;

    public static void showProgress(Context context, String msg) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public static void hideProgress() {
        progressDialog.hide();
        progressDialog = null;
    }
}
