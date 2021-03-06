package co.fanstories.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import co.fanstories.android.authentication.LoginActivity;
import co.fanstories.android.R;
import co.fanstories.android.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import co.fanstories.android.user.Token;
import co.fanstories.android.user.User;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DISPLAY_LENGTH = 750;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    initialize();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not load. Contact the developer", Toast.LENGTH_SHORT).show();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void initialize() throws Exception {
        Log.d("Initializing", "inside initialize");
        Token token = new Token(getSharedPreferences("FilvePref", MODE_APPEND));
        Log.d("Token present", token.present().toString());
        if(token.present()) {
            User user = new User(token);
            Toast.makeText(getApplicationContext(), "Welcome back " + user.getEmail(), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this, LiveVideoBroadcasterActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(loginIntent);
            SplashActivity.this.finish();
        }
    }

}
