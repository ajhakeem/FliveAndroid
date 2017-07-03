package co.fanstories.android.http;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsal on 7/3/17.
 */

public class Http {
    public static final String ROOT_URL = "https://testapi.fbfanadnetwork.com";

    RequestQueue requestQueue;

    public Map<String, String> mHeaders;

    Http() {
        mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/form-data");
        mHeaders.put("X-Client", "MOBILE");
    }

    public interface Callback {
        public void onSuccess(JSONObject response) throws JSONException;

        public void Onerror(VolleyError error);
    }

    public void request(String url, HashMap<String, String> params, final Callback callback) {

    }

    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Http.Callback callback) {

    }

}
