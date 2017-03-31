package lj.quickdelivery.adapter;

import java.util.ArrayList;

import lj.quickdelivery.raw.ListClass;
import lj.quickdilevery.activity.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SenderListAdapter extends BaseAdapter {
	Context context;
	ArrayList<ListClass> items;
	LayoutInflater inflater;

	public SenderListAdapter(Context context, ArrayList<ListClass> items) {
		this.context = context;
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public ListClass getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null)
			view = inflater.inflate(R.layout.raw_list_item, parent, false);
		TextView Tname, Tfrom, Tto, TtoWhom, Tstatus;
		Tname = (TextView) view.findViewById(R.id.text_list_gigname);
		Tfrom = (TextView) view.findViewById(R.id.text_list_from);
		Tto = (TextView) view.findViewById(R.id.text_list_to);
		TtoWhom = (TextView) view.findViewById(R.id.text_to_whom);
		Tstatus = (TextView) view.findViewById(R.id.text_status);

		String name = items.get(position).getName();
		if (name.length() > 10) {
			name = name.substring(0, 10);
		}
		Tname.setText(name);
		Tfrom.setText(items.get(position).getFirstLoc());
		Tto.setText(items.get(position).getLastLoc());
		TtoWhom.setText(" to " + items.get(position).getToWhom());
		Tstatus.setText(items.get(position).getStatus());
		return view;
	}

}
