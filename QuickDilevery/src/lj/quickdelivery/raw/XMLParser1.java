package lj.quickdelivery.raw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class XMLParser1 {
	ProgressDialog pDialog;
	GetResult2 xmlprs;
	Context context;

	public XMLParser1(Context context) {
		this.context = context;
	}

	public String getXmlFromUrl(String url) {
		String xml = null;
		try {
			xmlprs = new GetResult2();
			xml = xmlprs.execute(url).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return xml;
	}

	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} catch (ParserConfigurationException e) {
			Log.e("Error1: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error2: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error3: ", e.getMessage());
			return null;
		}
		return doc;
	}

	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	private class GetResult2 extends AsyncTask<String, Void, String> {
		String line = " ";
		String line2 = " ";
		private HttpURLConnection urlConnection;
		InputStream is;
		StringBuffer response = null;
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(context);
			dialog.setMessage("please wait...");
			dialog.setCancelable(false);
			dialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			Log.v(".doInBackground", "doInBackground method call");
			try {
				URL url = new URL(urls[0]);
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
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response.toString();
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (dialog.isShowing())
				dialog.dismiss();
			xmlprs.cancel(false);
		}
	}
}
