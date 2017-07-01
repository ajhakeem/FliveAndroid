package co.fanstories.android.http;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohsal on 6/2/17.
 */

public interface Callback {
    public void onSuccess(JSONObject response) throws JSONException;

    public void Onerror(VolleyError error);
}
