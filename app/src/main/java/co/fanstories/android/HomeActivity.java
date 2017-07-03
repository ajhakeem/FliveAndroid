package co.fanstories.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import co.fanstories.android.http.Http;
import co.fanstories.android.liveVideoBroadcaster.R;
import co.fanstories.android.pages.PageGateway;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        loadPages();
    }

    private void loadPages() {
        PageGateway pageGateway = new PageGateway(getApplicationContext());
        Log.d(TAG, PageGateway.token.get());
        HashMap<String, String> hashMap = new HashMap<>();
        pageGateway.getPages(hashMap, new Http.Callback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                Log.d(TAG, response.toString());
            }

            @Override
            public void Onerror(VolleyError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }

}
