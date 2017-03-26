package com.crimefighter.crimefighter.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class LoginActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mSubTextTextView;
    private Button mButton;

    public void advance() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTypeface = Typeface.createFromAsset(getAssets(),"fonts/montserrat.ttf");
        mTitleTextView = (TextView)findViewById(R.id.login_title);
        mTitleTextView.setTypeface(mTypeface);
        mSubTextTextView = (TextView)findViewById(R.id.login_subtext);
        mSubTextTextView.setTypeface(mTypeface);
        mButton = (Button)findViewById(R.id.login_button);
        mButton.setTypeface(mTypeface);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advance();
            }
        });
    }
}
