package co.fanstories.android.utils.jwt;

import android.util.Base64;
import java.io.UnsupportedEncodingException;

/**
 * Created by mohsal on 6/2/17.
 */

public class JWTUtils {

    public static String decoded(String JWTEncoded) throws Exception {
        String[] split = JWTEncoded.split("\\.");
        return getJson(split[1]);
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
