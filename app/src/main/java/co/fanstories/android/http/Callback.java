package co.fanstories.android.http;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohsal on 6/2/17.
 */

public abstract class Callback {
    public void onSuccess(JSONObject response) throws JSONException {

    }

    public void onSuccess(JSONArray response) throws JSONException {

    }

    public void Onerror(VolleyError error) {

    }

    public void OnSuccess(boolean isSuccess) {

    }

    public void onError(boolean isError) {

    }
}
