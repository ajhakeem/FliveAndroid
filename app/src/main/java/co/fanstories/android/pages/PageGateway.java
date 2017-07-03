package co.fanstories.android.pages;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import co.fanstories.android.http.Get;
import co.fanstories.android.http.Http;
import co.fanstories.android.user.Token;

/**
 * Created by mohsal on 7/3/17.
 */

public class PageGateway {
    private static final String TAG = "PageGateway";
    public static final String BASE_URI = "pages";
    public static final String GET_PAGES_URI = "getAllPages.php";

    public static Get httpGet;

    public static Token token;

    public PageGateway(Context context) {
        httpGet = new Get(context);
        token = new Token(context.getSharedPreferences("FlivePref", Context.MODE_APPEND));
        Log.d(TAG, token.get());
    }

    public void getPages(HashMap<String, String> params, Http.Callback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authrization", "Bearer " + Token.TOKEN);
        httpGet.request(
                Http.ROOT_URL + "/" + BASE_URI + "/" + GET_PAGES_URI,
                params,
                headers,
                callback
        );
    }
}
