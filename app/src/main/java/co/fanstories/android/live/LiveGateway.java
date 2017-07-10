package co.fanstories.android.live;

import android.content.Context;
import android.telecom.Call;

import java.util.HashMap;
import java.util.Properties;

import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Get;
import co.fanstories.android.http.Http;
import co.fanstories.android.user.Token;

/**
 * Created by mohsal on 7/5/17.
 */

public class LiveGateway {
    private static final String TAG = "LiveGateway";
    public static final String BASE_URI = "pages/live";
    public static final String GET_STREAM_KEY_URI = "getStreamKey.php";
    public static final String SHARE_LINK_URI = "shareLink.php";

    public static Get httpGet;

    public LiveGateway(Context context) {
        httpGet = new Get(context);
        Token token = new Token(context.getSharedPreferences("FilvePref", Context.MODE_APPEND));
    }

    public void shareLink(HashMap<String, String> params, Callback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + Token.TOKEN);
        httpGet.request(
                Http.ROOT_URL + "/" + BASE_URI + "/" + SHARE_LINK_URI,
                params,
                headers,
                callback
        );
    }

    public void getStreamKey(HashMap<String, String> params, Callback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + Token.TOKEN);
        httpGet.request(
                Http.ROOT_URL + "/" + BASE_URI + "/" + GET_STREAM_KEY_URI,
                params,
                headers,
                callback
        );
    }
}

