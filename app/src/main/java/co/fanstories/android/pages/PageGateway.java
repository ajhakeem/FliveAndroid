package co.fanstories.android.pages;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import co.fanstories.android.http.Callback;
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

    public PageGateway(Context context) {
        httpGet = new Get(context);
        Token token = new Token(context.getSharedPreferences("FilvePref", Context.MODE_APPEND));
    }

    public void getPages(HashMap<String, String> params, Callback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + Token.TOKEN);
        httpGet.request(
                Http.ROOT_URL + "/" + BASE_URI + "/" + GET_PAGES_URI,
                params,
                headers,
                callback,
                Http.RETURNS_ARRAY
        );
    }
}
