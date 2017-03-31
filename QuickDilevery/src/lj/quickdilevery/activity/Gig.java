package lj.quickdilevery.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Gig extends AppCompatActivity implements View.OnClickListener {
	Button createGig, viewGig, profile;
	private Context context = this;
	SharedPreferences preference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gig);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));

		createGig = (Button) findViewById(R.id.btn_create_gig);
		viewGig = (Button) findViewById(R.id.btn_view_gig);
		profile = (Button) findViewById(R.id.btn_profile_sender);

		createGig.setOnClickListener(this);
		viewGig.setOnClickListener(this);
		profile.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_gig:
			startActivity(new Intent(getApplicationContext(), CreateGig.class));
			break;
		case R.id.btn_view_gig:
			startActivity(new Intent(getApplicationContext(), ViewGig.class));
			break;
		case R.id.btn_profile_sender:
			showDetails();
			break;
		}
	}

	private void showDetails() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_dialog1);
		preference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);
		dialog.setTitle(preference.getString(Login.NAME, ""));

		TextView mName = (TextView) dialog.findViewById(R.id.mname);
		TextView jDate = (TextView) dialog.findViewById(R.id.jdate);
		TextView aDate = (TextView) dialog.findViewById(R.id.adate);
		TextView address = (TextView) dialog.findViewById(R.id.address);
		TextView person = (TextView) dialog.findViewById(R.id.person1);

		Log.i("lj.quick-gigview", preference.getString(Login.MYAGE, ""));

		mName.setText(preference.getString(Login.MYAGE, ""));
		jDate.setText(preference.getString(Login.MOBILE, ""));
		aDate.setText(preference.getString(Login.EMAIL, ""));
		address.setText(preference.getString(Login.ADDRESS, ""));
		person.setText(preference.getString(Login.PROFILE, ""));

		dialog.show();
	}
}
