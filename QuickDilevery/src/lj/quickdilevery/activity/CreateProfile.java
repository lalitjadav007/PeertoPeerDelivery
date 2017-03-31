package lj.quickdilevery.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import lj.quickdelivery.raw.CheckConnectivity;
import lj.quickdelivery.raw.StringFromURL;
import lj.quickdilevery.activity.CreateGig.Getgig;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CreateProfile extends AppCompatActivity {
	private EditText eName, eBirth, eNum, eEmail, ePass, eConfimPass, eAddress, eCity, eState;
	private RadioButton rSender, rDriver;
	private String name, birth, num, email, pass, cpass, address, city, state, profile = "";
	Button submit;
	protected Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_profile);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));

		getViews();
		rSender.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				profile = "sender";
			}
		});
		rDriver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				profile = "driver";
			}
		});
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAllStrings();
				if (name.equals("")) {
					eName.requestFocus();
					eName.setText("");
					eName.setHintTextColor(Color.parseColor("#122345"));
					eName.setHint("Field can not be empty");
				} else if (birth.equals("")) {
					eBirth.requestFocus();
					eBirth.setText("");
					eBirth.setHintTextColor(Color.parseColor("#122345"));
					eBirth.setHint("Field can not be empty");
				} else if (num.equals("")) {
					eNum.requestFocus();
					eNum.setText("");
					eNum.setHintTextColor(Color.parseColor("#122345"));
					eNum.setHint("Field can not be empty");
				} else if (email.equals("")) {
					eEmail.requestFocus();
					eEmail.setText("");
					eEmail.setHintTextColor(Color.parseColor("#122345"));
					eEmail.setHint("Field can not be empty");
				} else if (pass.equals("")) {
					ePass.requestFocus();
					ePass.setText("");
					ePass.setHintTextColor(Color.parseColor("#122345"));
					ePass.setHint("Field can not be empty");
				} else if (cpass.equals("")) {
					eConfimPass.requestFocus();
					eConfimPass.setText("");
					eConfimPass.setHintTextColor(Color.parseColor("#122345"));
					eConfimPass.setHint("Field can not be empty");
				} else if (!pass.equals(cpass)) {
					Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();
				} else if (address.equals("")) {
					eAddress.requestFocus();
					eAddress.setText("");
					eAddress.setHintTextColor(Color.parseColor("#122345"));
					eAddress.setHint("Field can not be empty");
				} else if (profile.equals("") || profile.equals("driver")) {
					Toast.makeText(CreateProfile.this, "Please select sender profile", Toast.LENGTH_SHORT).show();
				} else {
					CheckConnectivity check = new CheckConnectivity();
					boolean s = check.checknow(context);
					if (s) {
						new GetRegister().execute();
					} else {
						Toast.makeText(context, "No network!!!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	private void getAllStrings() {
		name = eName.getText().toString().trim();
		name = name.replaceAll(" ", "%20");
		birth = eBirth.getText().toString().trim();
		birth = birth.replaceAll(" ", "%20");
		num = eNum.getText().toString().trim();
		num = num.replaceAll(" ", "%20");
		email = eEmail.getText().toString().trim();
		email = email.replaceAll(" ", "%20");
		pass = ePass.getText().toString().trim();
		pass = pass.replaceAll(" ", "%20");
		cpass = eConfimPass.getText().toString().trim();
		cpass = cpass.replaceAll(" ", "%20");
		address = eAddress.getText().toString().trim();
		address = address.replaceAll(" ", "%20");
	}

	private void getViews() {
		eName = (EditText) findViewById(R.id.et_reg_name);
		eBirth = (EditText) findViewById(R.id.et_reg_birth_date);
		eNum = (EditText) findViewById(R.id.et_reg_num);
		eEmail = (EditText) findViewById(R.id.et_reg_email);
		ePass = (EditText) findViewById(R.id.et_reg_password);
		eConfimPass = (EditText) findViewById(R.id.et_reg_confirm_pass);
		eAddress = (EditText) findViewById(R.id.et_reg_address);
		rSender = (RadioButton) findViewById(R.id.rb_reg_sender);
		rDriver = (RadioButton) findViewById(R.id.rb_reg_driver);
		submit = (Button) findViewById(R.id.btn_reg_submit);
	}

	public class GetRegister extends AsyncTask<Void, Void, Void> {
		ProgressDialog pDialog;
		HttpURLConnection urlConnection;
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateProfile.this);
			pDialog.setMessage("Registering ...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				CheckConnectivity check = new CheckConnectivity();
				if (check.checknow(getBaseContext())) {
					if (profile.equals("driver")) {
						profile = "1";
					} else {
						profile = "0";
					}
					String url = "http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/insertPofile?name="
							+ name + "&mobile=" + num + "&age=" + birth + "&address=" + address + "&email=" + email
							+ "&pass=" + pass + "&photo=notawailable" + "&person=" + profile;
					URL url1 = new URL(url);
					urlConnection = (HttpURLConnection) url1.openConnection();
					InputStream is = urlConnection.getInputStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String inputLine;
					StringBuffer res = new StringBuffer();
					while ((inputLine = br.readLine()) != null) {
						res.append(inputLine);
					}
					br.close();
					response = res.toString();
					response = response.replaceAll(
							"<?xml version=\"1.0\" encoding=\"utf-8\"?><int xmlns=\"http://tempuri.org/\">", "");

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			if (pDialog.isShowing()) {
				pDialog.cancel();
				if (response.contains("1")) {
					Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_SHORT).show();
					onBackPressed();
					CreateProfile.this.finish();
				} else if (response.toString().contains("0")) {
					Toast.makeText(getApplicationContext(), "Registration not done", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Response not found", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
