package lj.quickdilevery.activity;

import java.net.MalformedURLException;
import java.net.URL;

import lj.quickdelivery.raw.StringFromURL;
import lj.quickdelivery.raw.XMLParser1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OneGigDriver extends AppCompatActivity {
	TextView Ttitle, Tfromplace, Ttoplace, Tfromp, Ttop, Tptype, Tamount, Tstatus, TdelStatus;
	Button ok, cancel;
	private SharedPreferences preference;
	private Context context = this;
	private String GID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_gig_driver);
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
		XMLParser1 parser = new XMLParser1(context);
		preference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);
		String xml = parser.getXmlFromUrl("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/GetGigMaster");
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
		ok = (Button) findViewById(R.id.btn_buynow);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlertDialog1(context, "Select Gig", "Are you sure want to select gig?", true);
			}
		});

		cancel = (Button) findViewById(R.id.btn_cancel_del);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
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
		alertDialog.setButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
				finish();

			}
		});

		alertDialog.setButton2("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
