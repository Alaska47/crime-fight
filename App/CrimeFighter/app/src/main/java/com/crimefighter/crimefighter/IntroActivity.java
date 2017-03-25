package com.crimefighter.crimefighter;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTextView = (TextView)findViewById(R.id.intro_title);
        mTextView.setTypeface(mTypeface);

    }
}
