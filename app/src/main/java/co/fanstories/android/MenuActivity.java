package co.fanstories.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Http;
import co.fanstories.android.liveVideoBroadcaster.LiveTestFragment;
import co.fanstories.android.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import co.fanstories.android.R;
import co.fanstories.android.pages.PageGateway;
import co.fanstories.android.pages.PagesFragment;

public class MenuActivity extends AppCompatActivity implements PagesFragment.OnFragmentInteractionListener, LiveTestFragment.OnFragmentInteractionListener {
    public static final String TAG = "Menu";
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d(TAG, "Selected home");
                    loadPages();
                    selectedFragment = PagesFragment.newInstance("", "");
                    mTextMessage.setText(R.string.title_home);
                    break;
                case R.id.navigation_dashboard:
                    Log.d(TAG, "Selected dash");
                    mTextMessage.setText(R.string.title_dashboard);
                    break;
                case R.id.navigation_live:
                    Log.d(TAG, "Selected live");
                    selectedFragment = LiveTestFragment.newInstance("a", "b");
                    mTextMessage.setText(R.string.title_live);
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    private void loadPages() {
        Log.d(TAG, "load pages");
        PageGateway pageGateway = new PageGateway(getApplicationContext());
        HashMap<String, String> hashMap = new HashMap<>();
        pageGateway.getPages(hashMap, new Callback() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, PagesFragment.newInstance("", ""));
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
