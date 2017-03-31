package lj.quickdelivery.raw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class StringFromURL {

	private HttpURLConnection urlConnection;

	public StringFromURL() {

	}

	public String getString(URL url) {

		InputStream is;
		StringBuffer response = null;
		try {
			Log.i("lj.quick-str", url + "");
			urlConnection = (HttpURLConnection) url.openConnection();
			is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			urlConnection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("lj.quick", e.getMessage());
		}
		Log.i("lj.quick-str", response.toString());
		return response.toString();

	}
}
