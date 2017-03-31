package lj.justdeliver.asyncs;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import lj.justdeliver.helper.CommonConstants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lj on 2/23/2017.
 */

public class GetDistanceAsyncs extends AsyncTaskLoader<Float> {
    private String senderLatLng;
    private String deliverLatLng;

    public GetDistanceAsyncs(Context context, String senderLatLng, String deliverLatLng) {
        super(context);
        this.senderLatLng = senderLatLng;
        this.deliverLatLng = deliverLatLng;
    }

    @Override
    public Float loadInBackground() {
        String url = String.format(CommonConstants.distanceUrl, senderLatLng, deliverLatLng);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String jsonResponse = response.body().string();
            JSONObject responseObject = new JSONObject(jsonResponse);
            if (responseObject.getString("status").equalsIgnoreCase("OK")) {
                JSONArray rowsArray = responseObject.getJSONArray("rows");
                JSONObject firstRow = rowsArray.getJSONObject(0);
                JSONArray elementsArray = firstRow.getJSONArray("elements");
                JSONObject firstElement = elementsArray.getJSONObject(0);
                JSONObject distance = firstElement.getJSONObject("distance");
                return (float) distance.getDouble("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0f;
    }
}
