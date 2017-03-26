package com.crimefighter.crimefighter.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class IntroActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mSubTextTextView;
    public TextView textView;
    public Button mButton;

    public void advance() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.intro_title);
        mTitleTextView.setTypeface(mTypeface);
        mSubTextTextView = (TextView)findViewById(R.id.intro_subtext);
        mSubTextTextView.setTypeface(mTypeface);
        mButton = (Button)findViewById(R.id.intro_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advance();
            }
        });

    }
}
