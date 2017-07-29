package co.fanstories.android.liveVideoBroadcaster;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import co.fanstories.android.authentication.AuthGateway;
import co.fanstories.android.authentication.LoginActivity;
import co.fanstories.android.http.Callback;
import co.fanstories.android.live.LiveGateway;
import co.fanstories.android.live.StreamViews;
import co.fanstories.android.pageScrollView.SelectedPageInterface;
import co.fanstories.android.pages.PageGateway;
import co.fanstories.android.pages.Pages;
import co.fanstories.android.utils.json.JSONUtils;
import co.fanstories.android.pageScrollView.HorizontalAdapter;
import co.fanstories.android.pageScrollView.Icon;
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;

import static co.fanstories.android.liveVideoBroadcaster.StreamBaseURL.RTMP_BASE_URL;
import co.fanstories.android.R;

public class LiveVideoBroadcasterActivity extends AppCompatActivity implements View.OnClickListener, SelectedPageInterface {
    private static final String TAG = LiveVideoBroadcasterActivity.class.getSimpleName();

    private String streamKey;
    private String blogUrl;
    private String pageId;

    private RelativeLayout wrapperShareAndStream;
    private LinearLayout wrapperStreamSettings;
    private RelativeLayout fabContainer;
    private RelativeLayout wrapperVideoScroll;
    private RelativeLayout wrapperButtons;
    private RelativeLayout wrapperShareLink;
    private ViewGroup mRootView;

    private ImageButton mSettingsButton;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private TextView mStreamLiveStatus;
    private GLSurfaceView mGLView;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private Button mBroadcastControlButton;
    private Button mStopBroadcast;
    private ImageView live_fb_logo;
    private ImageButton viewsIcon;
    private TextView viewsCount;
    private TextView tvBlogSelect;
    Long totalViewsCount;

    DisplayMetrics displayMetrics;
    Resolution detectedResolution;
    CameraResolutionInterface cameraResolutionInterface;

    private WebSocketClient mWebSocketClient;

    private Timer mTimer;
    public TimerHandler mTimerHandler;
    private long mElapsedTime;
    int fb_logo_click_counter = 0;

    private boolean isConnecting = false;
    boolean mIsRecording = false;
    private boolean scrollViewExpanded = true;
    private boolean isLiveFBMessageSendButtonEnabled = false;
    private boolean isLivePrepared = false;
    private boolean isCountdownFinished = false;
    private boolean isFabOpen;
    private long lastBackPressTime = 0;
    private Toast exitToast;

    FloatingActionButton fab, fabLogout;

    public RecyclerView horizontal_recycler_view;
    public HorizontalAdapter horizontalAdapter;
    List<Icon> iconList = new ArrayList<>();

    private LiveGateway liveGateway;

    private EditText mLiveFBMessageText;
    private Button mLiveFBMessageSendButton;
    private ProgressBar mLiveSendProgressBar;
    private CountDownTimer recordCountdownTimer;
    private TextView tvCountdownTimer;
    List<String> arrayListPageNames = new ArrayList<String>();
    String viewClickedPage = "";
    static HashMap<String, Pages.Page> hashMapPageDetails = new HashMap<>();
    static Pages.Page selectedLivePage;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            mLiveVideoBroadcaster = binder.getService();
            mLiveVideoBroadcaster.init(LiveVideoBroadcasterActivity.this, mGLView);
            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //binding on resume not to having leaked service connection
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        //this makes service do its job until done
        startService(mLiveVideoBroadcasterServiceIntent);
        //bind service here because you can only use the setRender method ONCE
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);

        setContentView(R.layout.activity_live_video_broadcaster);

        mTimerHandler = new TimerHandler();

        mRootView = (ViewGroup)findViewById(R.id.root_layout);
        mSettingsButton = (ImageButton)findViewById(R.id.settings_button);
        mStreamLiveStatus = (TextView) findViewById(R.id.stream_live_status);
        wrapperShareAndStream = (RelativeLayout) findViewById(R.id.wrapperShareAndStream);
        wrapperStreamSettings = (LinearLayout) findViewById(R.id.wrapperStreamSettings);
        wrapperShareLink = (RelativeLayout) findViewById(R.id.wrapperShareLink);
        fabContainer = (RelativeLayout) findViewById(R.id.fabContainer);
        wrapperVideoScroll = (RelativeLayout) findViewById(R.id.wrapperPageScroll);
        wrapperButtons = (RelativeLayout) findViewById(R.id.wrapperButtons);
        tvCountdownTimer = (TextView) findViewById(R.id.tvCountdownTimer);
        tvBlogSelect = (TextView) findViewById(R.id.tvBlogSelect);

        live_fb_logo = (ImageView) findViewById(R.id.live_fb_logo);
        live_fb_logo.setOnClickListener(this);

        mBroadcastControlButton = (Button) findViewById(R.id.toggle_broadcasting);
        mBroadcastControlButton.setOnClickListener(this);

        mStopBroadcast = (Button) findViewById(R.id.stop_broadcast);
        mStopBroadcast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isCountdownFinished) {
                    triggerStopRecording();
                    fb_logo_click_counter = 0;
                    recordCountdownTimer.cancel();
                    tvCountdownTimer.setText("");
                    mStopBroadcast.setVisibility(View.GONE);
                    mBroadcastControlButton.setVisibility(View.VISIBLE);
                    isCountdownFinished = false;
                }

                else {
                    triggerStopRecording();
                    fb_logo_click_counter = 0;
                    recordCountdownTimer.cancel();
                    tvCountdownTimer.setText("");
                    mStopBroadcast.setVisibility(View.GONE);
                    mBroadcastControlButton.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        viewsIcon = (ImageButton) findViewById(R.id.viewsIcon);
        viewsCount = (TextView) findViewById(R.id.viewsCount);

        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL activity.
        mGLView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        if (mGLView != null) {
            mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        }

        mLiveFBMessageText = (EditText) findViewById(R.id.live_fb_share_link_message);
        mLiveFBMessageSendButton = (Button)  findViewById(R.id.live_fb_message_send_button);
        mLiveFBMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        mLiveSendProgressBar = (ProgressBar) findViewById(R.id.live_progress_bar);

        liveGateway = new LiveGateway(getApplicationContext());

        wrapperButtons.setVisibility(View.GONE);

        initFAB();
        hideViewsCount();
        initializeScrollItems();

        recordCountdownTimer = new CountDownTimer(5050, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdownTimer.setText("" + millisUntilFinished / 1000);
                isCountdownFinished = false;
            }

            @Override
            public void onFinish() {
                tvCountdownTimer.setText("");
                toggleBroadcasting(mBroadcastControlButton);
                isCountdownFinished = true;
            }

        };

        //getScreenResolution();
    }

    public void initializeViewsCount() {
        totalViewsCount = 0L;
    }

    public void hideViewsCount() {
        viewsCount.setVisibility(View.GONE);
        viewsIcon.setVisibility(View.GONE);
    }

    public void showViewsCount() {
        viewsCount.setVisibility(View.VISIBLE);
        viewsIcon.setVisibility(View.VISIBLE);
    }

    public void initFAB() {
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
                        Toast.makeText(getApplicationContext(), "Could not log you out", Toast.LENGTH_SHORT).show();
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
    }

    private void showFabMenu() {
        isFabOpen = true;
        fabLogout.animate().translationY(getResources().getDimension(R.dimen.fab_logout_position));
    }

    private void hideFabMenu() {
        isFabOpen = false;
        fabLogout.animate().translationY(0);
    }

    /** Retrieve names for scrolling page selection **/

    public List<Icon> retrievePageScrollNames(List<String> iconN) {
        for (int i = 0; i < iconN.size(); i++) {
            iconList.add(new Icon(iconN.get(i).toString()));
        }

        return iconList;
    }

    /** Retrieve and set user page names for scrolling selector **/

    private void initializeScrollItems() {
        PageGateway pageGateway1 = new PageGateway(getApplicationContext());
        HashMap<String, String> hashMap1 = new HashMap<>();
        pageGateway1.getPages(hashMap1, new Callback() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {
                if (response.length() > 0) {
                    arrayListPageNames = Pages.Page.fetchPageNames(response);
                    hashMapPageDetails = Pages.Page.hashFromJson(response, hashMapPageDetails);
                    initializeScrollView(arrayListPageNames);
                }
            }
        });

    }

    /** Initialize scrolling selector **/

    private void initializeScrollView(List<String> iconNames) {
        tvBlogSelect.setText(getResources().getString(R.string.select_blog_to_go_live));
        horizontalAdapter = new HorizontalAdapter(retrievePageScrollNames(iconNames), getApplicationContext(), this);
        wrapperButtons.setVisibility(View.VISIBLE);

        horizontal_recycler_view= (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(LiveVideoBroadcasterActivity.this, LinearLayoutManager.HORIZONTAL, false);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(horizontal_recycler_view);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
    }

    /** Retrieve selected page from icon click **/

    @Override
    public void setSelectedPage(String selectedPage) {
        viewClickedPage = selectedPage;
        selectedLivePage = hashMapPageDetails.get(viewClickedPage);
        //retrieveSelectedPageDetails();
    }

    /** Confirm page selection and go live with selected page **/

    public void goLive() {

        if (viewClickedPage.length() == 0 || viewClickedPage == null) {
            if (arrayListPageNames.size() == 0 || arrayListPageNames == null) {
                resetButtons();
                Toast.makeText(getApplicationContext(), "No pages loaded, please check your connection", Toast.LENGTH_SHORT).show();
            }

            resetButtons();
            Toast.makeText(getApplicationContext(), "Please select a page to start streaming", Toast.LENGTH_SHORT).show();
        }

        else {
            if (selectedLivePage.verified == true && selectedLivePage.blogUrl != null && selectedLivePage.blogUrl.length() > 0) {
                collapsePageScrollView();
                recordingButtons();
                LiveGateway liveGateway = new LiveGateway(getApplicationContext());
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("page_id", selectedLivePage.pageId);
                liveGateway.getStreamKey(params, new Callback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        Map<String, Object> res = JSONUtils.jsonToMap(response);

                        streamKey = ((Map)res.get("data")).get("stream_key").toString();
                        blogUrl = "http://" + selectedLivePage.blogUrl + "/live";
                        pageId = selectedLivePage.pageId;

                        isLivePrepared = true;
                        fabContainer.setVisibility(View.GONE);
                        wrapperShareAndStream.setVisibility(View.VISIBLE);

                        wrapperShareLink.setAlpha(0.0f);
                        wrapperStreamSettings.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void Onerror(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Could not get key. Contact support.", Toast.LENGTH_SHORT).show();
                    }
                });

                recordCountdownTimer.start();
            }

            else {
                resetButtons();
                Toast.makeText(getApplicationContext(), "The selected page does not have a blog", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Reset button view configuration to default **/

    public void resetButtons() {
        mBroadcastControlButton.setVisibility(View.VISIBLE);
        mStopBroadcast.setVisibility(View.GONE);
        fabContainer.setVisibility(View.VISIBLE);
    }

    /** Set button view configuration to recording buttons **/

    public void recordingButtons() {
        mBroadcastControlButton.setVisibility(View.GONE);
        mStopBroadcast.setVisibility(View.VISIBLE);
        fabContainer.setVisibility(View.GONE);
    }

    /** Expand the scroll view by translating up **/

    public void expandPageScrollView() {
        if (scrollViewExpanded == false) {
            wrapperVideoScroll.animate().translationY(0);
            wrapperButtons.animate().translationY(0);
            scrollViewExpanded = true;
        }
    }

    /** Collapse scroll view by translating down **/

    public void collapsePageScrollView() {
        if (scrollViewExpanded == true) {
            wrapperVideoScroll.animate().translationY(800);
            wrapperButtons.animate().translationY(400);
            scrollViewExpanded = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mBroadcastControlButton) {
            goLive();
        }

        if (v == live_fb_logo) {
            fb_logo_click_counter++;

            if (fb_logo_click_counter % 2 == 1) {
                wrapperShareLink.animate().alpha(1.0f);
            }

            else {
                wrapperShareLink.animate().alpha(0.0f);
            }
        }

    }

    public void disableFBShare() {
        mLiveFBMessageSendButton.setBackground(getResources().getDrawable(R.drawable.live_share_button_disabled));
        mLiveFBMessageSendButton.setEnabled(false);
        mLiveFBMessageText.setEnabled(false);
    }

    /** Share broadcast link to facebook via post **/

    public void share() {
        mLiveSendProgressBar.setVisibility(View.VISIBLE);
        mLiveFBMessageSendButton.setVisibility(View.GONE);
        final String message = String.valueOf(mLiveFBMessageText.getText());
        if(!isLiveFBMessageSendButtonEnabled) {
            Snackbar.make(mRootView, "You can post once you are live", Snackbar.LENGTH_LONG).show();
            mLiveSendProgressBar.setVisibility(View.GONE);
            mLiveFBMessageSendButton.setVisibility(View.VISIBLE);
        }
        else if(message.length() == 0 && isLiveFBMessageSendButtonEnabled) {
            mLiveFBMessageText.setError("Message cannot be empty");
            mLiveSendProgressBar.setVisibility(View.GONE);
            mLiveFBMessageSendButton.setVisibility(View.VISIBLE);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("page_id", pageId);
            params.put("link", blogUrl);
            params.put("message", message);
            liveGateway.shareLink(params, new Callback() {
                @Override
                public void onSuccess(JSONObject response) throws JSONException {
                    //Log.d(TAG, response.toString());
                    if(response.get("status").equals("success")) {
                        mLiveSendProgressBar.setVisibility(View.GONE);
                        mLiveFBMessageSendButton.setVisibility(View.VISIBLE);
                        disableFBShare();
                        Snackbar.make(mRootView, "Successfully shared!", Snackbar.LENGTH_LONG).show();
                    } else {
                        mLiveSendProgressBar.setVisibility(View.GONE);
                        mLiveFBMessageSendButton.setVisibility(View.VISIBLE);
                        Snackbar.make(mRootView, "Could not share now!", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void Onerror(VolleyError error) {
                    error.printStackTrace();
                    mLiveSendProgressBar.setVisibility(View.GONE);
                    mLiveFBMessageSendButton.setVisibility(View.VISIBLE);
                    Snackbar.make(mRootView, "Uh-oh. We messed up!", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void changeCamera(View v) {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.changeCamera();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //this lets activity bind
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);
    }

    /** Handle necessary permissions for broadcasting from user  **/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (mLiveVideoBroadcaster.isPermissionGranted()) {
                    mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                }
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.RECORD_AUDIO) ) {
                        mLiveVideoBroadcaster.requestPermission();
                    }
                    else {
                        new AlertDialog.Builder(LiveVideoBroadcasterActivity.this)
                                .setTitle(R.string.permission)
                                .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            //Open the specific App Info page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                            startActivity(intent);

                                        } catch ( ActivityNotFoundException e ) {
                                            //e.printStackTrace();

                                            //Open the generic Apps page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                            startActivity(intent);

                                        }
                                    }
                                })
                                .show();
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //hide dialog if visible not to create leaked window exception
        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible()) {
            mCameraResolutionsDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(mConnection);
    }

    /** On camera oreintation changed **/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLiveVideoBroadcaster.setDisplayOrientation();
        }

    }

    /*public void getScreenResolution() {
        displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int deviceHeight = displayMetrics.heightPixels;
        int deviceWidth = displayMetrics.widthPixels;
        detectedResolution = new Resolution(deviceWidth, deviceHeight);

        ArrayList<Resolution> sizeList = new ArrayList<>();
        sizeList = mLiveVideoBroadcaster.getPreviewSizeList();
        mCameraResolutionsDialog = new CameraResolutionsFragment();
        mCameraResolutionsDialog.setCameraResolutions(sizeList, detectedResolution);
    }*/

    public void showSetResolutionDialog(View v) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");
        if (fragmentDialog != null) {

            ft.remove(fragmentDialog);
        }

        ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();

        if (sizeList != null && sizeList.size() > 0) {
            mCameraResolutionsDialog = new CameraResolutionsFragment();

            mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
            mCameraResolutionsDialog.show(ft, "resolution_dialog");
        }
        else {
            Snackbar.make(mRootView, "No resolution available",Snackbar.LENGTH_LONG).show();
        }

    }

    /** Turn on/off broadcasting **/

    public void toggleBroadcasting(View v) {
        if (isLivePrepared) {
            wrapperShareLink.animate().alpha(0.0f);
            if(!isConnecting) {
                if (!mIsRecording)
                {
                    isConnecting = true;
                    isLiveFBMessageSendButtonEnabled = false;
                    if (mLiveVideoBroadcaster != null) {
                        if (!mLiveVideoBroadcaster.isConnected()) {
                            String streamName = streamKey;

                            new AsyncTask<String, String, Boolean>() {
                                ContentLoadingProgressBar
                                        progressBar;
                                @Override
                                protected void onPreExecute() {
                                    progressBar = new ContentLoadingProgressBar(LiveVideoBroadcasterActivity.this);
                                    progressBar.show();
                                }

                                @Override
                                protected Boolean doInBackground(String... url) {
                                    return mLiveVideoBroadcaster.startBroadcasting(url[0]);
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    progressBar.hide();
                                    mIsRecording = result;
                                    if (result) {
                                        mStreamLiveStatus.setVisibility(View.VISIBLE);
                                        isLiveFBMessageSendButtonEnabled = true;
                                        isConnecting = false;
                                        mSettingsButton.setVisibility(View.GONE);
                                        startTimer();//start the recording duration
                                    }
                                    else {
                                        triggerStopRecording();
                                    }
                                }
                            }.execute(RTMP_BASE_URL + streamName);

                            try {
                                initializeViewsCount();
                                mWebSocketClient = liveGateway.getWebSocketConnection(pageId, new StreamViews() {
                                    @Override
                                    public void updateViewsCount(String message) {
                                        if(message.equals("1")) {
                                            totalViewsCount += 1;
                                            viewsCount.setText(String.valueOf(totalViewsCount));
                                        }
                                    }
                                });
                                mWebSocketClient.connect();
                                showViewsCount();
                            }

                            catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Snackbar.make(mRootView, R.string.streaming_not_finished, Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Snackbar.make(mRootView, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    triggerStopRecording();
                    hideViewsCount();
                }
            }
        }

        else {
            initializeScrollItems();
            //retrieveSelectedPageDetails();
        }
    }

    /** Stop recording and reset to selection menu **/

    public void triggerStopRecording() {
        if (mIsRecording) {

            mStreamLiveStatus.setVisibility(View.GONE);
            mStreamLiveStatus.setText(R.string.live_indicator);
            mSettingsButton.setVisibility(View.VISIBLE);

            stopTimer();
            recordCountdownTimer.cancel();
            tvCountdownTimer.setText("");
            isCountdownFinished = false;
            mLiveVideoBroadcaster.stopBroadcasting();
            expandPageScrollView();
            wrapperShareAndStream.setVisibility(View.GONE);
            wrapperStreamSettings.setVisibility(View.VISIBLE);
            fabContainer.setVisibility(View.VISIBLE);
        }

        else {
            mStreamLiveStatus.setVisibility(View.GONE);
            mStreamLiveStatus.setText(R.string.live_indicator);
            mSettingsButton.setVisibility(View.VISIBLE);

            stopTimer();
            recordCountdownTimer.cancel();
            tvCountdownTimer.setText("");
            isCountdownFinished = false;
            mLiveVideoBroadcaster.stopBroadcasting();
            expandPageScrollView();
            wrapperShareAndStream.setVisibility(View.GONE);
            wrapperStreamSettings.setVisibility(View.VISIBLE);
            fabContainer.setVisibility(View.VISIBLE);
        }
        hideViewsCount();
        initializeViewsCount();
        mIsRecording = false;
    }

    /** Starts timer and updates textview to show elapsed recording time **/

    public void startTimer() {

        if(mTimer == null) {
            mTimer = new Timer();
        }

        mElapsedTime = 0;
        mTimer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                mElapsedTime += 1; //increase every sec
                mTimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();

                if (mLiveVideoBroadcaster == null || !mLiveVideoBroadcaster.isConnected()) {
                    mTimerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }

    /** Stops the elapsed recording time timer **/

    public void stopTimer()
    {
        if (mTimer != null) {
            this.mTimer.cancel();
        }
        this.mTimer = null;
        this.mElapsedTime = 0;
    }

    public void setResolution(Resolution size) {
        mLiveVideoBroadcaster.setResolution(size);
    }

    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    mStreamLiveStatus.setText(getString(R.string.live_indicator) + " - " + getDurationString((int) mElapsedTime));
                    break;
                case CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Connection to server lost", Toast.LENGTH_SHORT).show();
                    triggerStopRecording();
                    break;
            }
        }
    }

    public static String getDurationString(int seconds) {

        if(seconds < 0 || seconds > 2000000)//there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if(hours == 0)
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        else
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mIsRecording) {
            triggerStopRecording();
            expandPageScrollView();
            mBroadcastControlButton.setVisibility(View.VISIBLE);
            mStopBroadcast.setVisibility(View.GONE);
        }

        if (isCountdownFinished == false) {
            triggerStopRecording();
            expandPageScrollView();
            mBroadcastControlButton.setVisibility(View.VISIBLE);
            mStopBroadcast.setVisibility(View.GONE);
        }

        if (this.lastBackPressTime < System.currentTimeMillis() - 3000) {
            exitToast = Toast.makeText(getApplicationContext(), "Press back again to close app", Toast.LENGTH_SHORT);
            exitToast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        }

        else {
            if (exitToast != null) {
                exitToast.cancel();
            }

            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        triggerStopRecording();
        unbindService(mConnection);
        super.onDestroy();
    }
}
