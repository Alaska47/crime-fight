package com.crimefighter.crimefighter.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AlphaAnimation;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.services.RegistrationIntentService;
import com.crimefighter.crimefighter.utils.QuickstartPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Random;
import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mSubTextTextView;
    public TextView mIntroText;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    final String userID = Integer.toString(1000 + (new Random()).nextInt(2000 - 1000 + 1));

    public void advance() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(getData("firstRun").equals("")) {
            storeData("UserID", userID);
            storeData("firstRun", "done");
        }

        LinearLayout rlayout = (LinearLayout) findViewById(R.id.activity_intro);
        rlayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                advance();
            }

        });

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.intro_title);
        mTitleTextView.setTypeface(mTypeface);
        mSubTextTextView = (TextView)findViewById(R.id.intro_subtext);
        mSubTextTextView.setTypeface(mTypeface);
        mIntroText = (TextView) findViewById(R.id.intro_text);
        mIntroText.setTypeface(mTypeface);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(800);
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(800);

        out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mIntroText.startAnimation(in);
            }
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mIntroText.startAnimation(out);
            }
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mIntroText.startAnimation(in);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.i("gu", "started");
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        };
        registerReceiver();




    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("gu", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void storeData(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getData(String key) {
        return getSharedPreferences("XPLORE_PREFS", Context.MODE_PRIVATE).getString(key, "");
    }

}
