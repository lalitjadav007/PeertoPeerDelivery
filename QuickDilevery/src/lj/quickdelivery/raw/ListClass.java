package lj.quickdelivery.raw;

import android.content.Context;
import android.widget.TextView;

public class ListClass {
	TextView name, firstLoc, lastLoc, status, toWhom;
	boolean statusValue;
	String gid;

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public boolean getStatusValue() {
		return statusValue;
	}

	public void setStatus(Boolean statusa) {
		statusValue = statusa;
		String val;
		if (statusa) {
			val = "Completed";
		} else {
			val = "In Progress";
		}
		this.status.setText(val);
	}

	public ListClass(Context context) {
		name = new TextView(context);
		firstLoc = new TextView(context);
		lastLoc = new TextView(context);
		status = new TextView(context);
		toWhom = new TextView(context);
	}

	public String getToWhom() {
		return toWhom.getText().toString();
	}

	public void setToWhom(String toWhom) {
		this.toWhom.setText(toWhom);
	}

	public String getName() {
		return name.getText().toString();
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public String getFirstLoc() {
		return firstLoc.getText().toString();
	}

	public void setFirstLoc(String firstLoc) {
		this.firstLoc.setText(firstLoc);
	}

	public String getLastLoc() {
		return lastLoc.getText().toString();
	}

	public void setLastLoc(String lastLoc) {
		this.lastLoc.setText(lastLoc);
	}

	public String getStatus() {
		return status.getText().toString();
	}
}
