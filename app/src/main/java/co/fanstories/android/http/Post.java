package co.fanstories.android.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsal on 7/1/17.
 */

public class Post extends Http {

    public Post(Context context) {
        super();
        requestQueue = HttpRequestQueue.getQueue(context);
    }

    @Override
    public void request(String url, HashMap<String, String> params, final Callback callback) {
        Log.d("Making", "post");
        Log.d("Params", new JSONObject(params).toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Making", response.toString());
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                callback.Onerror(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        requestQueue.add(jsObjRequest);
    }

    @Override
    public void request(String url, HashMap<String, String> params, Map<String, String> headers, final Callback callback) {
        Log.d("Making", "post");
        mHeaders.putAll(headers);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
            Log.d("error", error.toString());
            callback.Onerror(error);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return mHeaders;
            }
        };
        requestQueue.add(jsObjRequest);
    }
}

