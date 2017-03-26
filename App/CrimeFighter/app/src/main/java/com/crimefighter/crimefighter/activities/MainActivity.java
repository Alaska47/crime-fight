package com.crimefighter.crimefighter.activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;
import com.crimefighter.crimefighter.utils.Item;
import com.crimefighter.crimefighter.utils.RVAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Typeface mTypeface;
    private TextView mTitleTextView;
    private TextView mRemNumTextView;
    private TextView mRemTextView;
    private TextView mSolvNumTextView;
    private TextView mSolvTextView;
    private Button mButton;

    private List<Item> items;

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

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        initializeData();

        RVAdapter adapter = new RVAdapter(items);
        rv.setAdapter(adapter);
    }

    private void initializeData(){
        items = new ArrayList<>();
        items.add(new Item("Chocolates", 0.001));
        items.add(new Item("Backpack", 0.5));
        items.add(new Item("Bicycle", 1.25));
    }
}
