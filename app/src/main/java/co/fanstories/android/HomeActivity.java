package co.fanstories.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import co.fanstories.android.authentication.AuthGateway;
import co.fanstories.android.authentication.LoginActivity;
import co.fanstories.android.http.Callback;
import co.fanstories.android.pages.PageAdapter;
import co.fanstories.android.pages.PageGateway;
import co.fanstories.android.pages.Pages;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HOME";
    PageAdapter adapter;
    ArrayList<Pages.Page> mPages;

    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fab, fabLogout;
    ListView pagesListView;
    ProgressBar progressBar;
    private boolean isFabOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.home_view);

        final LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View headerFooter = inflater.inflate(R.layout.header_footer, null);

        pagesListView = (ListView) findViewById(R.id.page_list);
        pagesListView.addFooterView(headerFooter);
        pagesListView.addHeaderView(headerFooter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshPages);
        swipeRefreshLayout.setDistanceToTriggerSync(120);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                loadPages();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.pages_progress);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabLogout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new AuthGateway(getApplicationContext())).logout(new Callback() {
                    @Override
                    public void OnSuccess(boolean isSuccess) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "You've been logged out", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(boolean isError) {
                        Snackbar.make(coordinatorLayout, "Could not log you out.", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        isFabOpen = false;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFabOpen) {
                    showFabMenu();
                } else {
                    hideFabMenu();
                }
            }
        });
        loadPages();
    }

    private void showFabMenu() {
        isFabOpen = true;
        fabLogout.animate().translationY(-getResources().getDimension(R.dimen.fab_logout_position));
    }

    private void hideFabMenu() {
        isFabOpen = false;
        fabLogout.animate().translationY(0);
    }

    private void initialize() {
        if(adapter != null) {
            adapter.clear();
        }
        adapter = new PageAdapter(this, mPages);

        pagesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else if (!swipeRefreshLayout.isRefreshing() || firstVisibleItem != 0){
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
        pagesListView.setAdapter(adapter);
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            pagesListView.setVisibility(show ? View.GONE : View.VISIBLE);
            pagesListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    pagesListView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            pagesListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void loadPages() {
        showProgress(true);
        PageGateway pageGateway = new PageGateway(getApplicationContext());
        HashMap<String, String> hashMap = new HashMap<>();
        pageGateway.getPages(hashMap, new Callback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                Log.d(TAG, response.toString());
                mPages = Pages.Page.fromJson(response);

                //Sort the list based on their verified status.
                Collections.sort(mPages, new Comparator<Pages.Page>() {
                    @Override
                    public int compare(Pages.Page o1, Pages.Page o2) {
                        return (o1.verified ^ o2.verified) ? ((o1.verified ^ true) ? 1 : -1) : 0;
                    }
                });
                initialize();
            }

            @Override
            public void Onerror(VolleyError error) {
                Log.d(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(), "Could not load pages.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
