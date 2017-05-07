package com.crimefighter.crimefighter.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class MyGame extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);


    }

    // GameView class will go here

    // Here is our implementation of GameView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside SimpleGameEngine

    // Notice we implement runnable so we have
    // A thread and can override the run method.


    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        //Intent
        Intent intent = getIntent();
        int level = intent.getIntExtra("level", 1);
        boolean survivor = intent.getBooleanExtra("survivor", true);
        int offsetX = intent.getIntExtra("offsetX", 0);
        int offsetY = intent.getIntExtra("offsetY", 0);
        int innerOffsetX = intent.getIntExtra("innerOffsetX", 0);
        int innerOffsetY = intent.getIntExtra("innerOffsetY", 0);
        int speed = intent.getIntExtra("speed", 150);
        int changeColors = intent.getIntExtra("changeColors", 1);
        int numLines = intent.getIntExtra("numLines", 0);
        int collRectLength = intent.getIntExtra("collRectLength", 4);


        //Change Colors
        Random rand = new Random();
        int randomNum = rand.nextInt(changeColors) + 1;
        float multiplier = 1;
        int color = Color.rgb(0,150,136);
        int lineColor = Color.rgb(0, 150, 136);
        // This variable tracks the game frame rate
        int count = numLines;
        long fps;
        int width;
        int height;
        float vertical_speed = speed;
        int radius = 25;
        //Line drawing
        float initialX = 0;
        float initialY = 0;
        float endX = 0;
        float endY = 0;
        //Ball bouncing calculations
        double dX;
        double dY;
        Vector2f ball;
        Vector2f line;
        //Triangles
        int myHeight;
        int myWidth;
        boolean hasTouched = false;

        int numTriVertical;
        float verts[][];
        float innerVerts[][];
        float collisionRectangle[][];
        float safeSpace[][];
        private final int verticesColors[] = {
                Color.BLACK, Color.BLACK, Color.BLACK, 0xFF000000, 0xFF000000, 0xFF000000
        };
        // This is used to help calculate the fps
        private long timeThisFrame;

        // Declare an object of type Bitmap
        Bitmap bitmapBob;

        // Bob starts off not moving
        boolean isMoving = false;
        boolean fingerUp = false;
        boolean rightSide = false;
        boolean bottom = false;
        boolean hasHit = false;
        // He can walk at 150 pixels per second
        float walkSpeedPerSecond = speed;
        // He starts 10 pixels from the left
        float ballInitialX = 300;
        float ballInitialY = 300;
        float xPosition = 300;
        float yPosition = 300;

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Pistara.otf");
        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);
            //System.out.println(speed + "SPEED");
            //System.out.println(level + " LEVEL");
            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Load Bob from his .png file

            // Set display, width and height
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

            //Outer Triangles Box
            int numTriHorizontal = 15;


            myWidth = width - (2*offsetX);
            myHeight = (height-100)- (2*offsetY);
            numTriVertical = myHeight / (myWidth/numTriHorizontal);
            int triSize = myWidth / numTriHorizontal;
            verts = new float[(2*numTriVertical) + (2*numTriHorizontal)][6];

            collisionRectangle = new float[collRectLength][4];
            safeSpace = new float[1][4];

            makeTriangleBox(offsetX, offsetY, myWidth, myHeight, numTriHorizontal, numTriVertical, 0, verts);
            collRect(0, numTriHorizontal, numTriVertical, verts);
            makeSafeSpace(numTriHorizontal, numTriVertical);


            myWidth = width - (2*innerOffsetX);
            numTriHorizontal = myWidth / triSize;
            myHeight = (height-100)- (2*innerOffsetY);
            numTriVertical = myHeight / (myWidth/numTriHorizontal);
            innerVerts = new float[(2*numTriVertical) + (2*numTriHorizontal)][6];
            if(innerOffsetX !=0) {
                makeInnerTriangleBox(innerOffsetX, innerOffsetY, myWidth, myHeight, numTriHorizontal, numTriVertical, 4, innerVerts);
                collRect(4, numTriHorizontal, numTriVertical, innerVerts);
            }

            // Set our boolean to true - game on!

            playing = true;





        }
        // Mind the Gap!
        public void makeSafeSpace(int numTriHorizontal, int numTriVertical) {
            Random rand = new Random();
            //System.out.println(numTriHorizontal + " numTriHorizontal");
            //System.out.println(numTriVertical + " numTriVertical");
            int randomNum = rand.nextInt((verts.length-2) + 1);
            //System.out.println(randomNum + " randomNum");

            if(randomNum==numTriHorizontal || randomNum==numTriVertical || randomNum==numTriHorizontal + numTriVertical || randomNum==(2*numTriHorizontal)+numTriVertical || randomNum == (2*numTriVertical) + numTriHorizontal || randomNum==2*(numTriHorizontal + numTriVertical)) {
                //System.out.println("Equals");
                randomNum= randomNum+1;
            }
            if(randomNum+1==numTriHorizontal || randomNum+1==numTriVertical || randomNum+1 == numTriHorizontal + numTriVertical || randomNum+1==(2*numTriHorizontal)+numTriVertical || randomNum+1 == (2*numTriVertical) + numTriHorizontal || randomNum+1==2*(numTriHorizontal + numTriVertical)) {
                //System.out.println("One away");
                randomNum = randomNum-1;
            }
            float smallX = Math.min(verts[randomNum][0], Math.min(verts[randomNum][2], verts[randomNum][4]));
            float largeX = Math.max(verts[randomNum+1][0], Math.max(verts[randomNum+1][2], verts[randomNum+1][4]));
            float smallY = Math.min(verts[randomNum][1], Math.min(verts[randomNum][3], verts[randomNum][5]));
            float largeY = Math.max(verts[randomNum+1][1], Math.max(verts[randomNum+1][3], verts[randomNum+1][5]));
            safeSpace[0][0] = smallX;
            safeSpace[0][1] = smallY;
            safeSpace[0][2] = largeX;
            safeSpace[0][3] = largeY;

            for(int i=0; i<6; i++) {
                verts[randomNum][i] = 0;
                verts[randomNum+1][i] = 0;
            }

        }

        //Collision Rectangles
        public void collRect(int collRect, int numTriHorizontal, int numTriVertical, float[][] verts) {
            makeCollisionRectangleInitial(collRect, 0, 0, 1, verts);
            makeCollisionRectangleEnd(collRect, numTriHorizontal-1, 2, 5, verts);
            makeCollisionRectangleInitial(collRect+1, numTriHorizontal, 0, 1, verts);
            makeCollisionRectangleEnd(collRect+1, numTriVertical+(numTriHorizontal-1), 4, 5, verts);
            makeCollisionRectangleInitial(collRect+2, numTriVertical+numTriHorizontal, 0, 1, verts);
            makeCollisionRectangleEnd(collRect+2, (2*numTriVertical)+(numTriHorizontal-1), 4, 5, verts);
            makeCollisionRectangleInitial(collRect+3, (2*numTriVertical)+numTriHorizontal, 0, 1, verts);
            makeCollisionRectangleEnd(collRect+3, (2*numTriVertical)+(2*numTriHorizontal)-1, 2, 5, verts);
        }
        public void makeCollisionRectangleInitial(int coll, int i, int num1, int num2, float[][] verts) {

                collisionRectangle[coll][0] = verts[i][num1];
                collisionRectangle[coll][1] = verts[i][num2];

        }
        public void makeCollisionRectangleEnd(int coll, int i, int num1, int num2, float[][] verts) {
            collisionRectangle[coll][2] = verts[i][num1];
            collisionRectangle[coll][3] = verts[i][num2];
        }

        //The one with the pointy things sticking out
        public void makeInnerTriangleBox(int offsetX, int offsetY, int myWidth, int myHeight, int numTriHorizontal, int numTriVertical, int collRect, float[][] innerVerts) {
            makeTriangleBox(offsetX, offsetY, myWidth, myHeight, numTriHorizontal, numTriVertical, collRect, innerVerts);
            for (int i = 0; i < numTriHorizontal; i++) {
                innerVerts[i][1] += myHeight;
                innerVerts[i][3] += myHeight;
                innerVerts[i][5] += myHeight;
            }
            for (int i = numTriHorizontal; i < (numTriVertical + numTriHorizontal); i++) {
                innerVerts[i][0] += myWidth;
                innerVerts[i][2] += myWidth;
                innerVerts[i][4] += myWidth;
            }
            for(int i=numTriVertical+numTriHorizontal; i<(2*numTriVertical)+numTriHorizontal; i++) {
                innerVerts[i][0] -= myWidth;
                innerVerts[i][2] -= myWidth;
                innerVerts[i][4] -= myWidth;
            }
            for(int i=(2*numTriVertical)+numTriHorizontal; i<(2*numTriVertical)+(2*numTriHorizontal); i++) {
                innerVerts[i][1] -= myHeight;
                innerVerts[i][3] -= myHeight;
                innerVerts[i][5] -= myHeight;
            }
        }

        //The one with the pointy things sticking in
        public void makeTriangleBox(int offsetX, int offsetY, int myWidth, int myHeight, int numTriHorizontal, int numTriVertical, int collRect, float[][] verts) {
            //Top Triangles
            for (int i = 0; i < numTriHorizontal; i++) {
                verts[i][0] = (i*myWidth/numTriHorizontal) + offsetX;
                verts[i][1] =  offsetY;
                verts[i][2] = ((i+1)*(myWidth/numTriHorizontal)) + offsetX;
                verts[i][3] = offsetY;
                verts[i][4] = (myWidth/(2*numTriHorizontal)) + (i*myWidth/numTriHorizontal) + offsetX;
                if(i%2==0) {
                    verts[i][5] = (float) ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)) + offsetY;
                } else {
                    verts[i][5] = (float)((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)) + offsetY;
                }

            }

            //Left Triangles

            for (int i = numTriHorizontal; i < (numTriVertical + numTriHorizontal); i++) {
                verts[i][0] = offsetX;
                verts[i][1] = offsetY + ((i-numTriHorizontal) * (myWidth/numTriHorizontal));
                verts[i][2] = offsetX;
                verts[i][3] = offsetY + ((i-(numTriHorizontal-1)) * (myWidth/numTriHorizontal));
                if(i%2==0) {
                    verts[i][4] = offsetX + (float) ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal));
                } else {
                    verts[i][4] = offsetX + (float) ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal));
                }
                verts[i][5] = offsetY + ((i-numTriHorizontal) * (myWidth/numTriHorizontal)) + myWidth/(2*numTriHorizontal);

            }


            //Right Triangles
            for(int i=numTriVertical+numTriHorizontal; i<(2*numTriVertical)+numTriHorizontal; i++) {
                verts[i][0] = width-offsetX;
                verts[i][1] = offsetY + ((i-(numTriVertical+numTriHorizontal)) * (myWidth/numTriHorizontal));
                verts[i][2] = width-offsetX;
                verts[i][3] = offsetY + ((i-(numTriVertical+(numTriHorizontal-1))) * (myWidth/numTriHorizontal));
                if(i%2==0) {
                    verts[i][4] = width-(offsetX + (float) ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)));
                } else {
                    verts[i][4] = width-(offsetX + (float) ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)));
                }
                verts[i][5] = offsetY + ((i-(numTriVertical+numTriHorizontal)) * (myWidth/numTriHorizontal)) + myWidth/(2*numTriHorizontal);

            }

            //Bottom Triangles
            for(int i=(2*numTriVertical)+numTriHorizontal; i<(2*numTriVertical)+(2*numTriHorizontal); i++) {
                verts[i][0] = ((i-((2*numTriVertical)+numTriHorizontal))*myWidth/numTriHorizontal) + offsetX;
                verts[i][1] =  (height-100) - offsetY;
                verts[i][2] = ((i-((2*numTriVertical)+numTriHorizontal) +1)*(myWidth/numTriHorizontal)) + offsetX;
                verts[i][3] = (height-100) - offsetY;
                verts[i][4] = (myWidth/(2*numTriHorizontal)) + ((i-((2*numTriVertical)+numTriHorizontal))*myWidth/numTriHorizontal) + offsetX;
                if(i%2==0) {
                    verts[i][5] = (float)((height-100) - ( ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)) + offsetY));
                } else {
                    verts[i][5] = (float)((height-100) - ( ((Math.sqrt(3) / 2) * (myWidth / numTriHorizontal)) + offsetY));
                }

            }

        }
        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                update();

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // In later projects we will have dozens (arrays) of objects.
        // We will also do other things like collision detection.

        //uses fake, smaller "radius" for collision detection
        public boolean triCollide(float[][] myFloat, int radius) {
            for(int i=0; i<myFloat.length; i++) {
                if(myFloat[i][0] > myFloat[i][2]) {
                    if(xPosition>(myFloat[i][2]-radius) && xPosition<(myFloat[i][0]+radius)) {
                        if(myFloat[i][1]>myFloat[i][3]) {
                            if(yPosition<(myFloat[i][1]+radius) && yPosition >(myFloat[i][3]-radius)) {
                                return true;
                            }
                        } else {
                            if(yPosition>=(myFloat[i][1]-radius) && yPosition <=(myFloat[i][3]+radius)) {
                                return true;
                            }
                        }

                    }
                } else {
                    if(xPosition<=(myFloat[i][2]+radius) && xPosition>=(myFloat[i][0]-radius)) {
                        if(myFloat[i][1]>myFloat[i][3]) {
                            if(yPosition<(myFloat[i][1]+radius) && yPosition >(myFloat[i][3]-radius)) {
                                return true;
                            }
                        } else {
                            if(yPosition>=(myFloat[i][1]-radius) && yPosition <=(myFloat[i][3]+radius)) {
                                return true;
                            }
                        }

                    }
                }
            }
            return false;
        }
        public void update() {

            // If bob is moving (the player is touching the screen)
            // then move him to the right based on his target speed and the current fps.
            if(triCollide(safeSpace, radius)==false) {
                if (triCollide(collisionRectangle, radius-10)) {
                    if(survivor==true) {
                        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                        intent.putExtra("survivor", false);
                        intent.putExtra("level", level);
                        intent.putExtra("completed", false);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            boolean win = false;

            if(xPosition>=width || xPosition<=0 || yPosition>=(height-100) || yPosition<=0) {
                if(survivor==true) {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    //System.out.println("LEVEL" + level);
                    intent.putExtra("level", level);
                    intent.putExtra("offsetX", offsetX);
                    intent.putExtra("offsetY", offsetY);
                    intent.putExtra("innerOffsetX", innerOffsetX);
                    intent.putExtra("innerOffsetY", innerOffsetY);
                    intent.putExtra("speed", speed);
                    intent.putExtra("changeColors", changeColors);
                    intent.putExtra("numLines", numLines);
                    intent.putExtra("collRectLength", collRectLength);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                    intent.putExtra("survivor", false);
                    intent.putExtra("level", level);
                    intent.putExtra("completed", true);
                    startActivity(intent);
                    finish();
                }
            }

            if(count>=0) {
                if ((initialX >= endX && xPosition <= initialX && xPosition >= endX) || (initialX <= endX && xPosition >= initialX && xPosition <= endX)) {
                    if ((initialY >= endY && yPosition <= initialY && yPosition >= endY) || (initialY <= endY && yPosition >= initialY && yPosition <= endY)) {
                        if (fingerUp) {
                            double t = dX * (xPosition - initialX) + dY * (yPosition - initialY);
                            double eX = t * dX + initialX;
                            double eY = t * dY + initialY;
                            double LEC = Math.sqrt(Math.pow((eX - xPosition), 2) + Math.pow((eY - yPosition), 2));

                            if (LEC <= radius) {
                                //double dt = Math.sqrt(625-Math.pow(LEC, 2));
                                // compute first intersection point
                                //double fX = (t-dt)*dX + initialX;
                                //double fY = (t-dt)*dY + initialY;

                                // compute second intersection point
                                //double gX = (t+dt)*dX + initialX;
                                //double gY = (t+dt)*dY + initialY;
                                ball = new Vector2f(xPosition - ballInitialX, yPosition - ballInitialY);
                                Vector2f ballProjLine = ball.proj(line);
                                Vector2f ballPerpLine = ball.sub(ballProjLine);
                                Vector2f newBall = ballProjLine.sub(ballPerpLine);
                                //System.out.println(multiplier);
                                walkSpeedPerSecond = walkSpeedPerSecond * multiplier * (newBall.x() / ball.x());
                                vertical_speed = vertical_speed * multiplier * (newBall.y() / ball.y());
                                //System.out.println(randomNum + " color randomNum");

                                color = lineColor;

                                hasHit = true;
                                initialX = 0;
                                initialY = 0;
                                endX = 0;
                                endY = 0;
                                ballInitialX = xPosition;
                                ballInitialY = yPosition;
                            }
                        }
                    }
                }
            }
            if(fps!=0 && hasTouched == true){

                xPosition = xPosition + (walkSpeedPerSecond / fps);
                yPosition = yPosition + (vertical_speed / fps);
            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();
                // Draw the background color
                paint.setTypeface(tf);
                canvas.drawColor(Color.rgb(178,223,219));
                paint.setColor(Color.rgb(255,255,255));
                canvas.drawRect(offsetX, offsetY, width-offsetX, (height-100)-offsetY, paint);
                // Choose the brush color for drawing
                paint.setColor(Color.argb(50,0,0,0));
                paint.setFlags(Paint.ANTI_ALIAS_FLAG);
                // Make the text a bit bigger
                paint.setTextSize(40);
                paint.setStrokeWidth(5);
                paint.setStrokeCap(Paint.Cap.ROUND);
                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);
                paint.setTextSize(400);
                if(hasTouched == true) {
                    if (count < 0) {
                        canvas.drawText(0 + "", (width / 2) - 100, height / 2, paint);
                    } else if(count<=numLines) {
                        canvas.drawText(count + "", (width / 2) - 100, height / 2, paint);
                    }
                } else {
                    paint.setTextSize(100);
                    canvas.drawText("TAP TO START", (width / 2) - 300, height / 2, paint);
                }
                paint.setColor(Color.rgb(0,150,136));

                paint.setColor(color);

                canvas.drawCircle(xPosition, yPosition, radius, paint);

                paint.setColor(lineColor);

                if(isMoving==true && hasHit == false && count>=0) {
                    canvas.drawLine(initialX, initialY, endX, endY, paint);
                }
                paint.setColor(Color.rgb(103,58,183));

                for(int i=0; i<verts.length; i++) {
                    canvas.drawVertices(Canvas.VertexMode.TRIANGLES, verts[0].length, verts[i], 0, null, 0, verticesColors, 0, null, 0, 0, new Paint());
                }
                for (int i=0; i<innerVerts.length; i++) {
                    canvas.drawVertices(Canvas.VertexMode.TRIANGLES, innerVerts[0].length, innerVerts[i], 0, null, 0, verticesColors, 0, null, 0, 0, new Paint());
                }
                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);

                //Display size and width

            }

        }



        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {


            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if(hasTouched == false) {
                        count+=1;
                    }
                    hasTouched = true;
                    if(count!=0) {
                        randomNum = rand.nextInt(changeColors) + 1;
                        //System.out.println(randomNum);
                        if(randomNum == 1) {
                            multiplier = 1;
                            lineColor = Color.rgb(0, 150, 136);
                        }
                        if(randomNum == 2) {
                            multiplier = 1.25f;
                            lineColor = Color.rgb(76, 175, 80);
                        }
                        if(randomNum == 3) {
                            multiplier = 0.75f;
                            lineColor = Color.rgb(0, 188, 212);
                        }
                        if(randomNum == 4) {
                            multiplier = 1.5f;
                            lineColor = Color.rgb(255, 193, 7);
                        }
                        if(randomNum == 5) {
                            multiplier = 0.66f;
                            lineColor = Color.rgb(63, 81, 181);
                        }
                        if(randomNum == 6) {
                            multiplier = 2;
                            lineColor = Color.rgb(255, 87, 34);
                        }
                        if(randomNum == 7) {
                            multiplier = 0.5f;
                            lineColor = Color.rgb(103, 58, 183);

                        }

                        isMoving = false;
                        fingerUp = false;
                        hasHit = false;
                        // Set isMoving so Bob is moved in the update method
                        initialX = motionEvent.getX();
                        initialY = motionEvent.getY();
                    }
                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_MOVE:

                    // Set isMoving so Bob does not move
                    if(count!=0) {
                        isMoving = true;
                        fingerUp = false;
                        hasHit = false;
                        endX = motionEvent.getX();
                        endY = motionEvent.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    count-=1;
                    isMoving = true;
                    fingerUp = true;
                    endX = motionEvent.getX();
                    endY = motionEvent.getY();
                    double direction = Math.atan((endY-initialY)/(endX-initialX));

                    double LAB = Math.sqrt(Math.pow((endX-initialX),2) + Math.pow((endY-initialY), 2));
                    dX = (endX-initialX)/LAB;
                    dY = (endY-initialY)/LAB;

                    line = new Vector2f(endX-initialX, endY-initialY);

                    break;
            }
            return true;
        }

    }
    // This is the end of our GameView inner class

    // More SimpleGameEngine methods will go here

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

    class Vector2f
    {
        public Vector2f()
        {
            xyz[0] = 0;
            xyz[1] = 0;
        }

        public Vector2f(float x, float y)
        {
            xyz[0] = x;
            xyz[1] = y;
        }

        public Vector2f(float[] array)
        {
            if(array.length != 2)
                throw new RuntimeException("Must create vector with 2 element array");

            xyz[0] = array[0];
            xyz[1] = array[1];
        }

        public float[] array()
        {
            return (float[])xyz.clone();
        }
        public float magnitude() {
            return (float) Math.sqrt(Math.pow(xyz[0], 2) + Math.pow(xyz[1], 2));
        }
        public Vector2f proj(Vector2f w) {
            return w.mul((float) ((this.dot(w)) / (Math.pow(w.magnitude(), 2))));
        }
        public Vector2f add(Vector2f rhs)
        {
            return new Vector2f(
                    xyz[0] + rhs.xyz[0],
                    xyz[1] + rhs.xyz[1]);
        }

        public Vector2f sub(Vector2f rhs)
        {
            return new Vector2f(
                    xyz[0] - rhs.xyz[0],
                    xyz[1] - rhs.xyz[1] );
        }

        public Vector2f neg()
        {
            return new Vector2f(-xyz[0], -xyz[1]);
        }

        public Vector2f mul(float c)
        {
            return new Vector2f(c*xyz[0], c*xyz[1]);
        }

        public Vector2f div(float c)
        {
            return new Vector2f(xyz[0]/c, xyz[1]/c);
        }

        public float dot(Vector2f rhs)
        {
            return xyz[0]*rhs.xyz[0] +
                    xyz[1]*rhs.xyz[1];
        }

        public Vector2f cross(Vector2f rhs)
        {
            return new Vector2f(
                    xyz[1]*rhs.xyz[2] - xyz[2]*rhs.xyz[1],
                    xyz[0]*rhs.xyz[2] - xyz[2]*rhs.xyz[0]
            );
        }

        public boolean equals(Object obj)
        {
            if( obj instanceof Vector2f )
            {
                Vector2f rhs = (Vector2f)obj;

                return xyz[0]==rhs.xyz[0] &&
                        xyz[1]==rhs.xyz[1];
            }
            else
            {
                return false;
            }

        }

        public float norm()
        {
            return (float)Math.sqrt(this.dot(this));
        }

        public Vector2f normalize()
        {
            return this.div(norm());
        }

        public float distance(Vector2f rhs)
        {
            return rhs.sub(this).norm();
        }

        public float x()
        {
            return xyz[0];
        }

        public float y()
        {
            return xyz[1];
        }

        public void setX(float x) {
            xyz[0] = x;
        }
        public void setY(float y) {
            xyz[1] = y;
        }

        public String toString()
        {
            return "( " + xyz[0] + " " + xyz[1] + " )";
        }

        public float xyz[] = new float[2];
    }
}