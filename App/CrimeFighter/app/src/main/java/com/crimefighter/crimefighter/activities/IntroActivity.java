package com.crimefighter.crimefighter.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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


    }

}
