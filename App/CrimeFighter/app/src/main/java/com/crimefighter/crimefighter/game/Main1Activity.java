package com.crimefighter.crimefighter.game;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class Main1Activity extends AppCompatActivity {
    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView myName = (TextView) findViewById(R.id.name);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pistara.otf");

        myName.setTypeface(tf);

        ((TextView) findViewById(R.id.name1)).setTypeface(tf);

        TextView createdBy = (TextView) findViewById(R.id.createdBy);
        createdBy.setTypeface(tf);

        Button goToLevels = (Button) findViewById(R.id.goToLevels);
        goToLevels.setTypeface(tf);


        startButton = (Button) findViewById(R.id.startButton);
        Button howToPlayButton = (Button) findViewById(R.id.howToPlay);
        startButton.setTypeface(tf);
        howToPlayButton.setTypeface(tf);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(view);
            }
        });

        goToLevels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLevels();
            }
        });

        howToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                howToPlay(view);
            }
        });

    }

    public void goToLevels() {
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
    }

    public void howToPlay (View view) {
        Intent intent = new Intent(this, HowToPlay.class);
        startActivity(intent);
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
    }
    public void startFullscreenGame (View view) {
        Intent intent = new Intent(this, MyGame.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
