package com.crimefighter.crimefighter.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.crimefighter.crimefighter.R;

public class Levels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //TextView one = (TextView) findViewById(R.id.one);
        //TextView two = (TextView) findViewById(R.id.two);
        //TextView three = (TextView) findViewById(R.id.three);
        //TextView four = (TextView) findViewById(R.id.four);
        //TextView five = (TextView) findViewById(R.id.five);
        //TextView six = (TextView) findViewById(R.id.six);
        //TextView seven = (TextView) findViewById(R.id.seven);
        //TextView eight = (TextView) findViewById(R.id.eight);
        //TextView nine = (TextView) findViewById(R.id.nine);\
        //TextView ten = (TextView) findViewById(R.id.ten);
        //TextView eleven = (TextView) findViewById(R.id.eleven);
        //TextView twelve = (TextView) findViewById(R.id.twelve);
        //TextView thirteen = (TextView) findViewById(R.id.thirteen);
        //TextView fourteen = (TextView) findViewById(R.id.fourteen);
        //TextView fifteen = (TextView) findViewById(R.id.fifteen);
        //TextView sixteen = (TextView) findViewById(R.id.sixteen);
        //TextView seventeen = (TextView) findViewById(R.id.seventeen);
        //TextView eighteen = (TextView) findViewById(R.id.eighteen);
        //TextView nineteen = (TextView) findViewById(R.id.nineteen);
        //TextView twenty = (TextView) findViewById(R.id.twenty);
        //TextView twentyone = (TextView) findViewById(R.id.twentyone);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pistara.otf");
        TextView levels = (TextView) findViewById(R.id.levelS);
        levels.setTypeface(tf);
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);
        boolean completed = intent.getBooleanExtra("completed", false);
        if(completed==true) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Integer.toString(level), true);
            editor.commit();
        }
        int [] pickPlayers = { R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.ten, R.id.eleven, R.id.twelve, R.id.thirteen, R.id.fourteen, R.id.fifteen, R.id.sixteen, R.id.seventeen, R.id.eighteen, R.id.nineteen, R.id.twenty, R.id.twentyone};
        TextView b;
        for(int i = 0; i<pickPlayers.length; i++){
            b = (TextView) findViewById(pickPlayers[i]);
            b.setOnClickListener(new MyListener(i));
            b.setTypeface(tf);
            if(prefs.getBoolean(Integer.toString(i+1), false)) {
                b.setTextColor(Color.rgb(255, 255, 255));
            }
        }


    }

    private class MyListener implements TextView.OnClickListener {
        int pos;
        public MyListener (int position) {
            pos = position;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            startActivityForResult(pos+1);

        }
    }

    public void startActivityForResult(int level) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        Intent myIntent = new Intent(this, MyGame.class);
        myIntent.putExtra("level", level);
        myIntent.putExtra("survivor", false);
        if(level==1) {
            myIntent.putExtra("offsetX",100);
            myIntent.putExtra("offsetY", 200);
            myIntent.putExtra("innerOffsetX", 0);
            myIntent.putExtra("innerOffsetY", 0);
            myIntent.putExtra("speed", 300);
            myIntent.putExtra("changeColors", 1);
            myIntent.putExtra("numLines", 10);
            myIntent.putExtra("collRectLength", 4);
            startActivity(myIntent);
            finish();
        }
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
