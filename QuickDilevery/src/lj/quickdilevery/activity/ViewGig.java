package lj.quickdilevery.activity;

import java.util.ArrayList;

import lj.quickdelivery.adapter.SenderListAdapter;
import lj.quickdelivery.raw.ListClass;
import lj.quickdelivery.raw.XMLParser1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ViewGig extends AppCompatActivity {
	Context context = this;
	ArrayList<ListClass> items;
	SharedPreferences preference;
	ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_gig);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00b668")));

		lv = (ListView) findViewById(R.id.listview_sender);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				lv.deferNotifyDataSetChanged();
				Intent intent = new Intent(getApplicationContext(), OneGigShow.class);
				intent.putExtra("GID", items.get(position).getGid());
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		XMLParser1 parser = new XMLParser1(context);
		preference = getSharedPreferences(Login.MYPREFERENCES, Context.MODE_PRIVATE);
		String xml = parser.getXmlFromUrl("http://lalitjadav-001-site1.atempurl.com/QDMasterService.asmx/GetGigMaster");
		xml = xml.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", " ");
		Document doc = parser.getDomElement(xml);
		NodeList nl = doc.getElementsByTagName("Table");
		items = new ArrayList<ListClass>();
		for (int i = 0; i < nl.getLength(); i++) {
			ListClass lc = new ListClass(context);
			if (parser.getValue((Element) nl.item(i), "PID").equals(preference.getString(Login.PID, "0"))) {
				lc.setName(parser.getValue((Element) nl.item(i), "Gname"));
				lc.setFirstLoc(parser.getValue((Element) nl.item(i), "PLocation"));
				lc.setLastLoc(parser.getValue((Element) nl.item(i), "Dlocation"));
				Boolean status = Boolean.getBoolean(parser.getValue((Element) nl.item(i), "Status"));
				lc.setStatus(status);
				lc.setToWhom(parser.getValue((Element) nl.item(i), "DPerson"));
				lc.setGid(parser.getValue((Element) nl.item(i), "GID"));
				items.add(lc);
			}
		}
		Log.i("lj.quickvg--items-length", items.size() + "");
		lv.setAdapter(new SenderListAdapter(this, items));
	}
}
