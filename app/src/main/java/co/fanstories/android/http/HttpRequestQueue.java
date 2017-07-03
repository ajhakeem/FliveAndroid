package co.fanstories.android.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by mohsal on 7/3/17.
 */

public class HttpRequestQueue {
    public static RequestQueue requestQueue = null;

    public static RequestQueue getQueue(Context context) {
        if(requestQueue == null ) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }
}
