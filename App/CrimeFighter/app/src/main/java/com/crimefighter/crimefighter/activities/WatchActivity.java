package com.crimefighter.crimefighter.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class WatchActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitle;
    private EditText mItemName;
    private EditText mTime;
    private EditText mDescription;
    private Button mReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitle = (TextView)findViewById(R.id.title);
        mTitle.setTypeface(mTypeface);
        mItemName = (EditText)findViewById(R.id.name_edit_text);
        mItemName.setTypeface(mTypeface);
        mTime = (EditText)findViewById(R.id.time_edit_text);
        mTime.setTypeface(mTypeface);
        mDescription = (EditText)findViewById(R.id.description_edit_text);
        mDescription.setTypeface(mTypeface);
        mReportButton = (Button) findViewById(R.id.report_button);
        mReportButton.setTypeface(mTypeface);
    }
}
