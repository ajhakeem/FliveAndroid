package co.fanstories.android.authentication;

import android.content.Context;
import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Post;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mohsal on 6/2/17.
 */

public class AuthGateway {
    static final String ROOT = "https://testapi.fbfanadnetwork.com";
    final String SEPERATOR = "/";
    final String LOGIN_PATH = ROOT + "/users/login.php";
    final Post httpRequest;

    AuthGateway (Context context) {
        httpRequest = new Post(context);
    }

    public void login(HashMap<String, String> params, final Callback callback) {
        httpRequest.post(LOGIN_PATH, params, new Callback() {

            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                callback.onSuccess(response);
            }

            @Override
            public void Onerror(VolleyError error) {
                callback.Onerror(error);
            }
        });
    }

}

