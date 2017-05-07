package com.crimefighter.crimefighter.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class Main3Activity extends AppCompatActivity {

    public TextView myScore;
    public TextView highScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pistara.otf");

        TextView frowny = (TextView) findViewById(R.id.frowny);
        myScore = (TextView) findViewById(R.id.myScore);
        TextView levelsCompleted = (TextView) findViewById(R.id.levelsCompleted);
        TextView highScore = (TextView) findViewById(R.id.highScore);
        TextView notSurvivor = (TextView) findViewById(R.id.notSurvivor);
        //frowny.setTypeface(tf);
        myScore.setTypeface(tf);
        levelsCompleted.setTypeface(tf);
        highScore.setTypeface(tf);
        notSurvivor.setTypeface(tf);

        Button restartButton = (Button) findViewById(R.id.restart);
        restartButton.setTypeface(tf);

        Intent myIntent = getIntent();
        int level = myIntent.getIntExtra("level", 1);
        boolean survivor = myIntent.getBooleanExtra("survivor", true);


        if(survivor==true) {
            myScore.setText(level - 1 + "");
            notSurvivor.setText("");
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            int score = prefs.getInt("key", 0);
            if ((level - 1) > score) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("key", level - 1);
                editor.commit();
            }
            highScore.setText("HIGHSCORE: " + prefs.getInt("key", 0));

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startGame(view);
                }
            });
        } else {
            myScore.setText(level + "");
            notSurvivor.setText("LEVEL");
            levelsCompleted.setText("");
            highScore.setText("TRY NOT FAILING NEXT TIME");
            restartButton.setText("BACK");

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backToLevels(view);
                }
            });
        }
    }

    public void backToLevels (View view) {
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
        finish();
    }
    public void startGame (View view)
    {
        Intent intent = new Intent(this, MyGame.class);
        intent.putExtra("level",1);
        intent.putExtra("offsetX",100);
        intent.putExtra("offsetY", 200);
        intent.putExtra("innerOffsetX", 0);
        intent.putExtra("innerOffsetY", 0);
        intent.putExtra("speed", 300);
        intent.putExtra("changeColors", 1);
        intent.putExtra("numLines", 10);
        intent.putExtra("collRectLength", 4);
        startActivity(intent);
        finish();
    }

}
