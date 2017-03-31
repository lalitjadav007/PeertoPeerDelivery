package lj.quickdilevery.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import lj.quickdelivery.raw.CheckConnectivity;
import lj.quickdelivery.raw.StringFromURL;
import lj.quickdelivery.raw.XMLParser1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

public class OneGigShow extends AppCompatActivity {
	TextView Ttitle, Tfromplace, Ttoplace, Tfromp, Ttop, Tptype, Tamount, Tstatus, TdelStatus;
	Button ok, cancel;
	private SharedPreferences preference;
	private Context context = this;
	private String GID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_gig_show);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));
		getSupportActionBar().hide();
		Ttitle = (TextView) findViewById(R.id.text_title);
		Tfromplace = (TextView) findViewById(R.id.text_one_fromplace);
		Ttoplace = (TextView) findViewById(R.id.text_one_toplace);
		Tfromp = (TextView) findViewById(R.id.text_one_fromp);
		Ttop = (TextView) findViewById(R.id.text_one_top);
		Tptype = (TextView) findViewById(R.id.text_one_ptype);
		Tamount = (TextView) findViewById(R.id.text_one_amount);
		Tstatus = (TextView) findViewById(R.id.text_one_pstatus);
		TdelStatus = (TextView) findViewById(R.id.text_one_status);
		Intent intent = getIntent();
		if (intent.hasExtra("GID")) {
			GID = intent.getExtras().getString("GID");
		}
		CheckConnectivity check = new CheckConnectivity();
		boolean connected = check.checknow(context);
		if (!connected) {
			Toast.makeText(context, "No network !!!", Toast.LENGTH_SHORT).show();
		} else {
			XMLParser1 parser = new XMLParser1(context);
			preference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);
			String xml = parser
					.getXmlFromUrl("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/GetGigMaster");
			xml = xml.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
			Document doc = parser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("Table");
			for (int i = 0; i < nl.getLength(); i++) {
				if (parser.getValue((Element) nl.item(i), "GID").equals(GID)) {
					Ttitle.setText(parser.getValue((Element) nl.item(i), "Gname") + " to "
							+ parser.getValue((Element) nl.item(i), "DPerson"));
					Tfromplace.setText(parser.getValue((Element) nl.item(i), "PLocation"));
					Ttoplace.setText(parser.getValue((Element) nl.item(i), "Dlocation"));
					Tfromp.setText(parser.getValue((Element) nl.item(i), "PPerson"));
					Ttop.setText(parser.getValue((Element) nl.item(i), "DPerson"));
					Tptype.setText(parser.getValue((Element) nl.item(i), "PaymentType"));
					Tamount.setText(parser.getValue((Element) nl.item(i), "Amount"));
					String paystatus = parser.getValue((Element) nl.item(i), "PayStatus");
					if (paystatus.equals("false")) {
						paystatus = "Remaining";
					} else {
						paystatus = "Paid";
					}
					Tstatus.setText(paystatus);
					String delstatus = parser.getValue((Element) nl.item(i), "Status");
					if (delstatus.equals("false")) {
						delstatus = "In Progress";
					} else {
						delstatus = "Deliverd";
					}
					TdelStatus.setText(delstatus);
				}
			}
		}

		ok = (Button) findViewById(R.id.btn_buynow);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		cancel = (Button) findViewById(R.id.btn_cancel_del);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showAlertDialog1(context, "Cancel Gig", "Are you sure want to cancel gig?", true);
			}
		});
	}

	@SuppressWarnings("deprecation")
	public void showAlertDialog1(Context context, String title, String message, Boolean status) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
				.create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new Deletegig().execute();
			}
		});
		alertDialog.setButton2("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.cancel();
			}
		});
		alertDialog.show();
	}

	public class Deletegig extends AsyncTask<String, Void, Void> {
		ProgressDialog pDialog;
		String response;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(OneGigShow.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				CheckConnectivity check = new CheckConnectivity();
				if (check.checknow(getBaseContext())) {
					URL url = new URL("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/DeleteGig?gid="
							+ GID);
					Log.i("lj.quick--url", url + "");
					response = new StringFromURL().getString(url);
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
				response = response.replaceAll(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?><int xmlns=\"http://tempuri.org/\">", "");
				if (response.contains("true")) {
					Toast.makeText(getApplicationContext(), "Gig " + Ttitle.getText().toString() + " deleted",
							Toast.LENGTH_LONG).show();
					OneGigShow.this.finish();
				} else {
					Toast.makeText(getApplicationContext(), "Please try after some time", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
