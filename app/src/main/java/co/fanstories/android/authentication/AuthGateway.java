package co.fanstories.android.authentication;

import android.content.Context;
import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Http;
import co.fanstories.android.http.Post;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static co.fanstories.android.http.Http.ROOT_URL;

/**
 * Created by mohsal on 6/2/17.
 */

public class AuthGateway {
    final String SEPERATOR = "/";
    final String LOGIN_PATH = ROOT_URL + "/users/login.php";
    final Post httpPostRequest;

    AuthGateway (Context context) {
        httpPostRequest = new Post(context);
    }

    public void login(HashMap<String, String> params, final Http.Callback callback) {
        httpPostRequest.request(LOGIN_PATH, params, new Http.Callback() {

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

