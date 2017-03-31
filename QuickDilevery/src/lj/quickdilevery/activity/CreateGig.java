package lj.quickdilevery.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lj.quickdelivery.raw.CheckConnectivity;
import lj.quickdelivery.raw.XMLParser1;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateGig extends AppCompatActivity implements OnClickListener {

	String gigName, gigDesc, gigSize, gigSenderLoc, gigReciverLoc, gigSenderName, gigRecieverName, gigDelTime,
			gigCharge;
	double total;
	public static int BASE_CHARGE = 100;
	EditText EgigName, EgigDesc, EgigSenderName, EgigReciverName;
	Spinner SgigSenderLoc, SgigReceiverLoc;
	RadioButton Rsmall, Rmedium, Rlarge, Rnow, Rtommorow, RwhenFree;
	Button sendRequest;
	SharedPreferences sharedPreference;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_gig);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));
		sharedPreference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);
		findViews();
	}

	private void findViews() {
		EgigName = (EditText) findViewById(R.id.edit_gigname);
		EgigDesc = (EditText) findViewById(R.id.edit_gigdesc);
		EgigSenderName = (EditText) findViewById(R.id.edit_gigsender);
		EgigReciverName = (EditText) findViewById(R.id.edit_gigreceiver);
		SgigSenderLoc = (Spinner) findViewById(R.id.spinner_senderloc);
		SgigReceiverLoc = (Spinner) findViewById(R.id.spinner_receiverloc);
		Rsmall = (RadioButton) findViewById(R.id.radio_small);
		Rmedium = (RadioButton) findViewById(R.id.radio_medium);
		Rlarge = (RadioButton) findViewById(R.id.radio_large);
		Rnow = (RadioButton) findViewById(R.id.radio_now);
		Rtommorow = (RadioButton) findViewById(R.id.radio_tommorow);
		RwhenFree = (RadioButton) findViewById(R.id.radio_whenfree);
		sendRequest = (Button) findViewById(R.id.button_submitgig);

		Rsmall.setOnClickListener(this);
		Rmedium.setOnClickListener(this);
		Rlarge.setOnClickListener(this);
		Rnow.setOnClickListener(this);
		Rtommorow.setOnClickListener(this);
		RwhenFree.setOnClickListener(this);
		sendRequest.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.radio_small:
			gigSize = "1";
			total = BASE_CHARGE;
			break;
		case R.id.radio_large:
			gigSize = "3";
			total = BASE_CHARGE * 2;
			break;
		case R.id.radio_medium:
			gigSize = "2";
			total = BASE_CHARGE + 50;
			break;
		case R.id.radio_now:
			gigDelTime = "now";
			break;
		case R.id.radio_tommorow:
			gigDelTime = "tommorow";
			break;
		case R.id.radio_whenfree:
			gigDelTime = "free";
			break;
		case R.id.button_submitgig:
			gigName = EgigName.getText().toString();
			gigName = gigName.replaceAll(" ", "%20");
			gigDesc = EgigDesc.getText().toString();
			gigDesc = gigDesc.replaceAll(" ", "%20");
			gigSenderLoc = SgigSenderLoc.getSelectedItem().toString();
			gigSenderName = EgigSenderName.getText().toString();
			gigSenderName = gigSenderName.replaceAll(" ", "%20");
			gigReciverLoc = SgigReceiverLoc.getSelectedItem().toString();
			gigRecieverName = EgigReciverName.getText().toString();
			gigRecieverName = gigRecieverName.replaceAll(" ", "%20");
			if ((gigSenderLoc.equals("C.G.Road") && gigReciverLoc.equals("S.G.Road"))
					|| (gigSenderLoc.equals("S.G.Road") && gigReciverLoc.equals("C.G.Road"))) {
				total += 20;
			}
			gigSenderLoc = gigSenderLoc.replaceAll(" ", "%20");
			gigReciverLoc = gigReciverLoc.replaceAll(" ", "%20");
			if (gigName.equals("") || gigDesc.equals("") || gigSenderLoc.equals("") || gigSenderName.equals("")
					|| gigRecieverName.equals("")) {
				Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_SHORT).show();
			} else {
				CheckConnectivity check = new CheckConnectivity();
				boolean s = check.checknow(context);
				if (s) {
					new Getgig().execute();
				} else {
					Toast.makeText(context, "No network!!!", Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}

	public class Getgig extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog;
		HttpURLConnection urlConnection;
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateGig.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				CheckConnectivity check = new CheckConnectivity();
				if (check.checknow(getBaseContext())) {
					int total1 = (int) total;
					String totals = total1 + "";
					String url = ("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/InsertGig?pid="
							+ sharedPreference.getString(Login.PID, "3") + "&gigname=" + gigName + "&description="
							+ gigDesc + "&size=" + gigSize + "&plocation=" + gigSenderLoc + "&pperson=" + gigSenderName
							+ "&dlocation=" + gigReciverLoc + "&dperson=" + gigRecieverName + "&dtime=" + gigDelTime
							+ "&gigphoto=notawailable&paymenttype=cod&amount=" + totals + "&paymentstatus=false");
					// URL url = new
					// URL("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/Login?email=jadav.lalit57@gmail.com&pass=lalit");
					Log.i("lj.quick--url", url);
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
			} catch (Exception e) {
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
					showAlertDialog1(context, "Payment", "You have to pay " + total, true);
				} else if (response.contains("0")) {
					Toast.makeText(getApplicationContext(), "There may be some problem", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
				}
				urlConnection.disconnect();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void showAlertDialog1(Context context, String title, String message, Boolean status) {
		// TODO Auto-generated method stub
		final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
				.create();
		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting alert dialog icon

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// alertDialog.cancel();
				finish();

			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
