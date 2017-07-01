package co.fanstories.android.user;

/**
 * Created by mohsal on 7/1/17.
 */

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import co.fanstories.android.utils.json.JSONUtils;
import co.fanstories.android.utils.jwt.JWTUtils;

/**
 * Created by mohsal on 6/2/17.
 */

public class Token {
    public static final String TOKEN_KEY = "FliveTOken";
    public static String TOKEN = "";
    SharedPreferences mData;

    public Token (SharedPreferences data) {
        mData = data;
        TOKEN = get();
    }

    public void put(String token) {
        SharedPreferences.Editor editor =  mData.edit();
        editor.putString(TOKEN_KEY, token);
        editor.commit();
        TOKEN = get();
    }

    public void remove() {
        SharedPreferences.Editor editor = mData.edit();
        editor.remove(TOKEN_KEY);
        editor.commit();
        TOKEN = get();
    }

    public String get() {
        return mData.getString(TOKEN_KEY, "");
    }

    public Boolean present() {
        return mData.contains(TOKEN_KEY);
    }

    public Map<String, Object> decode() throws Exception {
        return JSONUtils.jsonToMap(new JSONObject(JWTUtils.decoded(TOKEN)));
    }
}
