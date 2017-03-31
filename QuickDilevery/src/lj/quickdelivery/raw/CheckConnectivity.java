package lj.quickdelivery.raw;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckConnectivity {
	
	ConnectivityManager connectivityManager;
	NetworkInfo wifiInfo, mobileInfo;
	
	public boolean checknow(Context con){
		
		try{
			connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if(wifiInfo.isConnected() || mobileInfo.isConnected()){
				Log.v("connection", "success");
				return true;
			}
		}catch(Exception e){
			Log.i("error - connection", e.getMessage());
		}
		
		Log.v("connection", "by pass");
		return false;
		
		
	}

}
