package co.fanstories.android.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

/**
 * Created by mohsal on 7/3/17.
 */

public class Get extends Http {

    public static final String TAG = "Http.Get";

    public Get(Context context) {
        super();
        requestQueue = HttpRequestQueue.getQueue(context);
    }

    public String convertMapToString(HashMap<String, String> params) {
        StringBuilder paramsString = new StringBuilder();
        paramsString.append("");
        int i = 0;
        for (String key: params.keySet()) {
            i += 1;
            paramsString.append(key + "=" + params.get(key) + (i == params.size() ? "" : "&"));
        }
        return paramsString.toString();
    }

    @Override
    public void request(String url, HashMap<String, String> params, final Callback callback) {
        url = url + "?" + convertMapToString(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.Onerror(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Callback callback) {
        url = url + convertMapToString(params);
        mHeaders.putAll(headers);
        Log.d(TAG, mHeaders.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.Onerror(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Callback callback, boolean returnsArray) {
        url = url + convertMapToString(params);
        mHeaders.putAll(headers);
        Log.d(TAG, mHeaders.toString());
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.Onerror(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
