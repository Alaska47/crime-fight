package com.crimefighter.crimefighter.game;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class Main2Activity extends AppCompatActivity {

    public TextView numLevelsComplete;
    public Button nextLevel;
    public boolean done = false;
    public int thisLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pistara.otf");
        TextView noSurvivor = (TextView) findViewById(R.id.noSurvivor);
        TextView smiley = (TextView) findViewById(R.id.smiley);
        numLevelsComplete = (TextView) findViewById(R.id.numLevelsComplete);
        TextView levelsCompleted = (TextView) findViewById(R.id.levelsCompleted);
        updateLevel();
        //smiley.setTypeface(tf);
        numLevelsComplete.setTypeface(tf);
        levelsCompleted.setTypeface(tf);
        noSurvivor.setTypeface(tf);

        nextLevel = (Button) findViewById(R.id.goToNext);
        nextLevel.setTypeface(tf);

        Intent intent = getIntent();
        boolean survivor = intent.getBooleanExtra("survivor", true);
        thisLevel = intent.getIntExtra("level", 1);

        if(survivor==true) {

            noSurvivor.setText("");

            if (done == false) {
                nextLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getLevel(view);
                    }
                });
            } else {
                nextLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish(view);
                    }
                });
            }
        } else {
            levelsCompleted.setText("COMPLETED");
            nextLevel.setText("BACK");
            nextLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToLevels(view);
                }
            });
        }


    }

    public void goToLevels(View view) {
        Intent intent = new Intent(this, Levels.class);
        intent.putExtra("level", thisLevel);
        intent.putExtra("completed", true);
        startActivity(intent);
        finish();
    }
    public void finish(View view) {
        Intent intent = new Intent(this, Main1Activity.class);
        startActivity(intent);
        finish();
    }

    public void updateLevel() {
        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);
        numLevelsComplete.setText(level + "");
        if(level==20) {
            nextLevel.setText("YOU WIN!");
            done=true;
        }

    }
    public void getLevel(View view) {
        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);
        int offsetX = intent.getIntExtra("offsetX", 0);
        int offsetY = intent.getIntExtra("offsetY", 0);
        int innerOffsetX = intent.getIntExtra("innerOffsetX", 0);
        int innerOffsetY = intent.getIntExtra("innerOffsetY", 0);
        int speed = intent.getIntExtra("speed", 150);
        int changeColors = intent.getIntExtra("changeColors", 0);
        int numLines = intent.getIntExtra("numLines", 0);
        int collRectLength = intent.getIntExtra("collRectLength", 4);

        Intent myIntent = new Intent(this, MyGame.class);
        level = level+1;
        System.out.println(level +  "LEVEL");
        myIntent.putExtra("level",level);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if(level==2) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", 0);
            myIntent.putExtra("innerOffsetY", 0);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 3);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 4);
            startActivity(myIntent);
            finish();

        }

        if(level==3) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-50);
            myIntent.putExtra("innerOffsetY", (height/2)-50);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 1);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==4) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", 0);
            myIntent.putExtra("innerOffsetY", 0);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 5);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 4);
            startActivity(myIntent);
            finish();

        }

        if(level==5) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", 0);
            myIntent.putExtra("innerOffsetY", 0);
            myIntent.putExtra("speed", 400);
            myIntent.putExtra("changeColors", 4);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 4);
            startActivity(myIntent);
            finish();

        }
        if(level==6) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-100);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 5);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==7) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 5);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==8) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-100);
            myIntent.putExtra("speed", 350);
            myIntent.putExtra("changeColors", 6);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==9) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 350);
            myIntent.putExtra("changeColors", 6);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==10) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-50);
            myIntent.putExtra("innerOffsetY", (height)/2-50);
            myIntent.putExtra("speed", 400);
            myIntent.putExtra("changeColors", 6);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==11) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-50);
            myIntent.putExtra("innerOffsetY", (height)/2-50);
            myIntent.putExtra("speed", 450);
            myIntent.putExtra("changeColors", 6);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==12) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-50);
            myIntent.putExtra("innerOffsetY", (height)/2-50);
            myIntent.putExtra("speed", 500);
            myIntent.putExtra("changeColors", 6);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==13) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-50);
            myIntent.putExtra("innerOffsetY", (height)/2-50);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==14) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-100);
            myIntent.putExtra("speed", 350);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==15) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-100);
            myIntent.putExtra("speed", 450);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==16) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-100);
            myIntent.putExtra("speed", 500);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==17) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 400);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==18) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 450);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==19) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-100);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 500);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==20) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-150);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed", 450);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }

        if(level==21) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", (width/2)-150);
            myIntent.putExtra("innerOffsetY", (height)/2-200);
            myIntent.putExtra("speed",500);
            myIntent.putExtra("changeColors", 7);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 8);
            startActivity(myIntent);
            finish();

        }


    }


}
