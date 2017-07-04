package co.fanstories.android.authentication;

import android.content.Context;
import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Http;
import co.fanstories.android.http.Post;
import co.fanstories.android.user.Token;

import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static co.fanstories.android.http.Http.ROOT_URL;

/**
 * Created by mohsal on 6/2/17.
 */

public class AuthGateway {
    final String SEPERATOR = "/";
    final String LOGIN_PATH = ROOT_URL + "/users/login.php";
    final Post httpPostRequest;
    Context context;

    AuthGateway (Context context) {
        httpPostRequest = new Post(context);
        this.context = context;
    }

    public void login(HashMap<String, String> params, final Callback callback) {
        httpPostRequest.request(LOGIN_PATH, params, new Callback() {

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

    public boolean logout(final Callback callback) {
        Token token = new Token(context.getSharedPreferences("FilvePref", MODE_PRIVATE));
        token.remove();
        return true;
    }

}

