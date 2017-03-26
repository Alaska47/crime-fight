package com.crimefighter.crimefighter.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class StolenActivity extends AppCompatActivity {

    public static Typeface mTypeface;
    private TextView mNameTextView;
    private TextView mDistanceTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mNameTextView = (TextView)findViewById(R.id.stolen_name);
        mNameTextView.setTypeface(mTypeface);
        mDistanceTextView = (TextView)findViewById(R.id.stolen_distance);
        mDistanceTextView.setTypeface(mTypeface);
        mDescriptionTextView = (TextView)findViewById(R.id.stolen_description);
        mDescriptionTextView.setTypeface(mTypeface);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stolen);
    }
}
