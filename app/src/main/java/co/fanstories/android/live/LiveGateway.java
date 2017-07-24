package co.fanstories.android.live;

import android.content.Context;
import android.telecom.Call;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
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
    public static final String WEBSOCKET_URI = "ws://analytics.fanadnetwork.com/ws";


    public static WebSocketClient webSocketClient;

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

    public String mapToString(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (String key: map.keySet()) {
            stringBuilder.append(key + "=" + map.get(key));
            if(i < map.size() - 1) {
                stringBuilder.append("&");
            }
        }
        return stringBuilder.toString();
    }

    public WebSocketClient getWebSocketConnection(final String pageId) throws URISyntaxException {

        HashMap<String, String> params = new HashMap<>();
        params.put("ws_request", "true");
        params.put("ws_pub_id", pageId);

        URI uri = new URI(WEBSOCKET_URI + "?" + mapToString(params));
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "Websocket connected");
            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, "Websocket: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "Closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.d(TAG, "Websocket: " + ex.getMessage());
            }
        };

        return webSocketClient;
    }
}

