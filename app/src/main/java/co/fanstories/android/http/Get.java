package co.fanstories.android.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

/**
 * Created by mohsal on 7/3/17.
 */

public class Get extends Http {

    public Get(Context context) {
        super();
        requestQueue = HttpRequestQueue.getQueue(context);
    }

    public String convertMapToString(HashMap<String, String> params) {
        return params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce("", String::concat);
    }

    @Override
    public void request(String url, HashMap<String, String> params, Http.Callback callback) {
        url = url + convertMapToString(params);
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
    }

    @Override
    public void request(String url, HashMap<String, String> params, Map<String, String> headers, Callback callback) {
        url = url + convertMapToString(params);
        mHeaders.putAll(headers);
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
    }
}
