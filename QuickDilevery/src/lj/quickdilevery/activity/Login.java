package lj.quickdilevery.activity;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import lj.quickdelivery.raw.CheckConnectivity;
import lj.quickdelivery.raw.StringFromURL;
import lj.quickdelivery.raw.XMLParser1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity implements View.OnClickListener {
	Button create_profile, use_profile;
	String login, password;
	EditText eLogin, ePassword;
	SharedPreferences sharedPreference;
	public static final String MYPREFERENCES = "LoginPref1";
	public static final String EMAIL = "email";
	public static final String PID = "pid";
	public static final String SAVED = "no";
	public static final String NAME = "name";
	public static final String MYAGE = "0";
	public static final String MOBILE = "0";
	public static final String ADDRESS = "not awailable";
	public static final String PROFILE = "sender";
	Context context = this;
	LinearLayout ll, llogin;
	ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);
		getSupportActionBar().hide();
		ll = (LinearLayout) findViewById(R.id.linearlayout_qd);
		llogin = (LinearLayout) findViewById(R.id.linearlayout_login);
		pb = (ProgressBar) findViewById(R.id.progressb_login_load);

		TranslateAnimation animation = new TranslateAnimation(0, 0, 500, 0);
		animation.setDuration(4000);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				sharedPreference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);

				if (sharedPreference.getString(SAVED, "no").equals("yes")) {
					if (sharedPreference.getString(PROFILE, "sender").equals("driver")) {
						startActivity(new Intent(getApplicationContext(), DriverGig.class));
					} else {
						startActivity(new Intent(getApplicationContext(), Gig.class));
					}
					Login.this.finish();
				} else {
					pb.setVisibility(View.GONE);
					llogin.setVisibility(View.VISIBLE);
				}
			}
		});
		ll.setAnimation(animation);
		animation.start();

		create_profile = (Button) findViewById(R.id.btn_create_profile);
		create_profile.setOnClickListener(this);

		use_profile = (Button) findViewById(R.id.btn_use_profile);
		use_profile.setOnClickListener(this);

		eLogin = (EditText) findViewById(R.id.et_login_email);
		ePassword = (EditText) findViewById(R.id.et_login_password);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create_profile:
			startActivity(new Intent(getApplicationContext(), CreateProfile.class));
			break;

		case R.id.btn_use_profile:
			login = eLogin.getText().toString().trim();
			password = ePassword.getText().toString().trim();

			if (login.equals("")) {
				eLogin.requestFocus();
				eLogin.setText("");
				eLogin.setHintTextColor(Color.parseColor("#122345"));
				eLogin.setHint("Field can not be empty");
			} else if (password.equals("")) {
				ePassword.requestFocus();
				ePassword.setText("");
				ePassword.setHintTextColor(Color.parseColor("#122345"));
				ePassword.setHint("Field can not be empty");
			} else {
				CheckConnectivity check = new CheckConnectivity();
				boolean cnow = check.checknow(context);
				if (cnow) {
					new GetLogin().execute();
				} else {
					Toast.makeText(context, "No network !!!", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}

	public class GetLogin extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog;
		HttpURLConnection urlConnection;
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Loggin...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				CheckConnectivity check = new CheckConnectivity();
				if (check.checknow(getBaseContext())) {
					URL url = new URL("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/Login?email="
							+ login + "&pass=" + password);
					// URL url = new
					// URL("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/Login?email=jadav.lalit57@gmail.com&pass=lalit");
					response = new StringFromURL().getString(url);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (pDialog.isShowing()) {
				pDialog.cancel();
				if (response.contains("true")) {
					Editor editor = sharedPreference.edit();
					editor.putString(EMAIL, login);
					XMLParser1 parser = new XMLParser1(context);
					String xml = parser
							.getXmlFromUrl("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/GetPID?Email="
									+ login);
					Log.i("lj.quick", xml);
					xml = xml.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
					Document doc = parser.getDomElement(xml);
					NodeList nl = doc.getElementsByTagName("Table");
					String pid = parser.getValue((Element) nl.item(0), "PID");
					editor.putString(PID, pid);
					editor.putString(SAVED, "yes");
					editor.putString(NAME, parser.getValue((Element) nl.item(0), "Name"));
					String age = parser.getValue((Element) nl.item(0), "Age");
					Log.i("lj.quick-age1", age);
					editor.putString(MYAGE, age);
					editor.putString(MOBILE, parser.getValue((Element) nl.item(0), "Mobile"));
					editor.putString(ADDRESS, parser.getValue((Element) nl.item(0), "Address"));
					String profile = parser.getValue((Element) nl.item(0), "Person");
					Log.i("lj.quick-pprofile", profile);
					if (profile.equals("0")) {
						profile = "sender";
					} else {
						profile = "driver";
					}
					Log.i("lj.quick-pprofile", profile);
					editor.putString(PROFILE, profile);
					editor.commit();
					Log.i("lj.quick-age", sharedPreference.getString(MYAGE, ""));
					if (profile.equalsIgnoreCase("driver")) {
						startActivity(new Intent(getApplicationContext(), DriverGig.class));
					} else {
						startActivity(new Intent(getApplicationContext(), Gig.class));
					}
					Login.this.finish();
				} else if (response.toString().contains("false")) {
					Toast.makeText(getApplicationContext(), "user ID and Password does not match", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
