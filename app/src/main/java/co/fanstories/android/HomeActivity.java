package co.fanstories.android;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.fanstories.android.http.Callback;
import co.fanstories.android.http.Http;
import co.fanstories.android.R;
import co.fanstories.android.pages.PageAdapter;
import co.fanstories.android.pages.PageGateway;
import co.fanstories.android.pages.Pages;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HOME";
    PageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initialize();
        loadPages();
    }

    private void initialize() {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);

        ArrayList<Pages.Page> pages = new ArrayList<Pages.Page>();
        adapter = new PageAdapter(this, pages);
        ListView listView = (ListView) findViewById(R.id.page_list);
        listView.setAdapter(adapter);
    }

    private void loadPages() {
        PageGateway pageGateway = new PageGateway(getApplicationContext());
        HashMap<String, String> hashMap = new HashMap<>();
        Toast.makeText(getApplicationContext(), "Loading pages..", Toast.LENGTH_SHORT).show();
        pageGateway.getPages(hashMap, new Callback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                Toast.makeText(getApplicationContext(), "Loaded pages..", Toast.LENGTH_SHORT).show();
                ArrayList<Pages.Page> pages = Pages.Page.fromJson(response);
                adapter.addAll(pages);
            }

            @Override
            public void Onerror(VolleyError error) {
                Log.d(TAG, error.getMessage());
            }
        });
    }
}
