package co.fanstories.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import co.fanstories.android.authentication.AuthGateway;
import co.fanstories.android.authentication.LoginActivity;
import co.fanstories.android.http.Callback;
import co.fanstories.android.live.LiveGateway;
import co.fanstories.android.liveVideoBroadcaster.CameraResolutionsFragment;
import co.fanstories.android.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import co.fanstories.android.pages.PageGateway;
import co.fanstories.android.pages.Pages;
import co.fanstories.android.utils.json.JSONUtils;
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HOME";
    private RelativeLayout rlHomeView;
    FloatingActionButton fab, fabLogout;
    ProgressBar progressBar;

    private boolean isFabOpen;

    List<String> arrayListPageNames = new ArrayList<String>();
    Spinner pagesSpinner;
    String spinnerSelectedPage = "";
    Button go_live_button;
    static HashMap<String, Pages.Page> hashMapPageDetails = new HashMap<>();
    static Pages.Page selectedLivePage;

    /**********************************************************************/

    private String streamKey;
    private String blogUrl;
    private String pageId;

    private ViewGroup mRootView;
    boolean mIsRecording = false;
    private Timer mTimer;
    private long mElapsedTime;
    //public HomeActivity.TimerHandler mTimerHandler;
    private ImageButton mSettingsButton;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private TextView mStreamLiveStatus;
    private GLSurfaceView mGLView;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private Button mBroadcastControlButton;
    private boolean isConnecting = false;

    private LiveGateway liveGateway;

    private LinearLayout mLiveFBShareLayout;
    private EditText mLiveFBMessageText;
    private Button mLiveFBMessageSendButton;
    private boolean isLiveFBMessageSendButtonEnabled = false;
    private ProgressBar mLiveSendProgressBar;

    /*********************************************************************/

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            mLiveVideoBroadcaster = binder.getService();
            mLiveVideoBroadcaster.init(HomeActivity.this, mGLView);
            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_home);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        rlHomeView = (RelativeLayout) findViewById(R.id.home_view);

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
                        Snackbar.make(rlHomeView, "Could not log you out.", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        isFabOpen = false;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    fab.setImageResource(R.drawable.ic_close_black_24dp);
                    showFabMenu();
                } else {
                    fab.setImageResource(R.mipmap.ic_burger_menu);
                    hideFabMenu();
                }
            }
        });

        initializeSpinner();
        //pagesSpinner = (Spinner) findViewById(R.id.pagesSpinner);

        /*pagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedPage = pagesSpinner.getSelectedItem().toString();
                retrieveSelectedPageDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        /*********************************************************************/

    }

    private void showFabMenu() {
        isFabOpen = true;
        fabLogout.animate().translationY(-getResources().getDimension(R.dimen.fab_logout_position));
    }

    private void hideFabMenu() {
        isFabOpen = false;
        fabLogout.animate().translationY(0);
    }

    private void initializeSpinner() {
        PageGateway pageGateway1 = new PageGateway(getApplicationContext());
        HashMap<String, String> hashMap1 = new HashMap<>();
        pageGateway1.getPages(hashMap1, new Callback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                if (response.length() > 0) {
                    arrayListPageNames = Pages.Page.fetchPageNames(response);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.pages_list_item, arrayListPageNames);
                //pagesSpinner.setAdapter(arrayAdapter);
            }
        });
    }

    public void retrieveSelectedPageDetails() {
        PageGateway pageGateway = new PageGateway(getApplicationContext());
        final HashMap<String, String> hashMap = new HashMap<>();

        Log.d(TAG, "Pages Object Out");

        pageGateway.getPages(hashMap, new Callback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                if (response.length() > 0) {
                    hashMapPageDetails = Pages.Page.hashFromJson(response, hashMapPageDetails);
                }

                //selectedLivePage = hashMapPageDetails.get(spinnerSelectedPage);
            }
        });
    }

    public void goLive() {
        //initBroadcastActivity();

        if (selectedLivePage.verified == true && selectedLivePage.blogUrl != null && selectedLivePage.blogUrl.length() > 0) {
            LiveGateway liveGateway = new LiveGateway(getApplicationContext());
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("page_id", selectedLivePage.pageId);
            liveGateway.getStreamKey(params, new Callback() {
                @Override
                public void onSuccess(JSONObject response) throws JSONException {
                    Map<String, Object> res = JSONUtils.jsonToMap(response);
                    Intent intent = new Intent(getApplicationContext(), LiveVideoBroadcasterActivity.class);
                    intent.putExtra("streamKey", ((Map)res.get("data")).get("stream_key").toString());
                    intent.putExtra("blogUrl", "http://" + selectedLivePage.blogUrl + "/live");
                    intent.putExtra("pageId", selectedLivePage.pageId);

                    streamKey = ((Map)res.get("data")).get("stream_key").toString();
                    blogUrl = "http://" + selectedLivePage.blogUrl + "/live";
                    pageId = selectedLivePage.pageId;
                    getApplicationContext().startActivity(intent);
                }

                @Override
                public void Onerror(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "Could not get key. Contact support.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            Toast.makeText(getApplicationContext(), "The selected page does not have a blog", Toast.LENGTH_SHORT).show();
        }
    }

}
