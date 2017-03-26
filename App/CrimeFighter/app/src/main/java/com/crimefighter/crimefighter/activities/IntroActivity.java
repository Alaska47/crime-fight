package com.crimefighter.crimefighter.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.util.TimerTask;

public class IntroActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mSubTextTextView;
    public TextView textView;
    public TextView mIntroText;

    public void advance() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

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

        mIntroText.setVisibility(View.VISIBLE);
        mIntroText.setAlpha(0);

        new java.util.Timer().schedule( new TimerTaskCustom(), 3000);


        mIntroText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIntroText.animate().setDuration(500).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIntroText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 3000);

    }

    private class TimerTaskCustom extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIntroText.animate()
                            .alpha(1.0f)
                            .setDuration(1000);
                }
            });
        }
    }
}
