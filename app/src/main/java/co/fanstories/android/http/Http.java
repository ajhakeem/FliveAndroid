package co.fanstories.android.http;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsal on 7/3/17.
 */

public class Http {
    public static final String ROOT_URL = "https://fbfanads.com/fanads-backend";
    public static final boolean RETURNS_ARRAY = true;
    RequestQueue requestQueue;

    public Map<String, String> mHeaders;

    Http() {
        mHeaders = new HashMap<String, String>();
        mHeaders.put("Content-Type", "application/form-data");
        mHeaders.put("X-Client", "MOBILE");
    }

    public void request(String url, HashMap<String, String> params, final Callback callback) {

    }

    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Callback callback) {

    }

    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Callback callback, boolean returnsArray) {

    }
}
