package com.crimefighter.crimefighter.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class WaitActivity extends AppCompatActivity {

    public static Typeface mTypeface;
    private TextView mNameTextView;
    private TextView mDistanceTextView;
    private TextView mDescriptionTextView;
    private Button mButtonHere;
    private Button mButtonGone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mNameTextView = (TextView)findViewById(R.id.name);
        mNameTextView.setTypeface(mTypeface);
        mDistanceTextView = (TextView)findViewById(R.id.distance);
        mDistanceTextView.setTypeface(mTypeface);
        mDescriptionTextView = (TextView)findViewById(R.id.description);
        mDescriptionTextView.setTypeface(mTypeface);
        mButtonHere = (Button)findViewById(R.id.here);
        mButtonHere.setTypeface(mTypeface);
        mButtonGone = (Button)findViewById(R.id.gone);
        mButtonGone.setTypeface(mTypeface);
    }
}
