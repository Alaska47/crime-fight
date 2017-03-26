package com.crimefighter.crimefighter;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mRemNumTextView;
    private TextView mRemTextView;
    private TextView mSolvNumTextView;
    private TextView mSolvTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.main_title);
        mTitleTextView.setTypeface(mTypeface);
        mRemNumTextView = (TextView)findViewById(R.id.main_remaining_number);
        mRemNumTextView.setTypeface(mTypeface);
        mRemTextView = (TextView)findViewById(R.id.main_remaining);
        mRemTextView.setTypeface(mTypeface);
        mSolvNumTextView = (TextView)findViewById(R.id.main_solved_number);
        mSolvNumTextView.setTypeface(mTypeface);
        mSolvTextView = (TextView)findViewById(R.id.main_solved);
        mSolvTextView.setTypeface(mTypeface);
    }
}
