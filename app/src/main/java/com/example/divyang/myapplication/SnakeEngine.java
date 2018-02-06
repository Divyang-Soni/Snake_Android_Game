package com.example.divyang.myapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Divyang on 06-Feb-18.
 */

public class SnakeEngine extends SurfaceView implements Runnable {

    private Thread thread; // game loop

    private Context context; // reference to current activity

    // for plaing sound effects
   // private SoundPool soundPool;
    private int eat_food = -1;
    private int snake_crash = -1;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.RIGHT;


    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // How long is the snake
    private int snakeLength;

    // food location
    private int foodX;
    private int foodY;

    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;
    // We will draw the frame much more often

    // The location in the grid of all the segments
    private int[] snakeXs;
    private int[] snakeYs;


    // to control pausing
    private volatile boolean isPlaying;

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;


    // How many points does the player have
    private int score;



    public SnakeEngine(Context context, Point size) {  // size inherited from main class
        super(context);// calling Surface View constructure

        // allocating screen size to variables
        screenX = size.x;
        screenY = size.y;

        // getting pixels per block
        blockSize = screenX / NUM_BLOCKS_WIDE;
        // number of blocks of the same size that will fit into the height
        numBlocksHigh = screenY / blockSize;

        // Set the sound up
      //  soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // Create objects of the 2 required classes
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;

        // Adding the two sounds in memory
        //  descriptor = assetManager.openFd("get_mouse_sound.ogg");
        //  eat_food = soundPool.load(descriptor, 0);

        //  descriptor = assetManager.openFd("death_sound.ogg");
        //  snake_crash = soundPool.load(descriptor, 0);


        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start the game
        newGame();
    }

    @Override
    public void run() {
        while (isPlaying) {

            // Update 10 times a second
            if(updateRequired()) {
                update();
                draw();
            }

        }
    }

    public void  newGame(){
        // Start with a single snake segment
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        // Get food ready for dinner
        spawnFood();

        // Reset the score
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }

    // method to create new food at random location
    private void spawnFood(){
        Random random = new Random();
        foodX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        foodY = random.nextInt(numBlocksHigh - 1) + 1;
    }

    // method to eat food
    // called when snake collide with food
    private void eatFood(){
        // Increase the size of the snake
        snakeLength++;

        // create new food at random position
        spawnFood();

        //add to the score
        score = score + 1;
       // soundPool.play(eat_food, 1, 1, 0, 0, 1);
    }


    private void moveSnake(){
        // Move the body
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate heading
        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }


    // if snake is collided with boundaries or is collied with its own body then game over
    private boolean detectDeath(){
        // Has the snake died?
        boolean isDead = false;

        // Hit the screen edge
        if (snakeXs[0] == -1) isDead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) isDead = true;
        if (snakeYs[0] == -1) isDead = true;
        if (snakeYs[0] == numBlocksHigh) isDead = true;

        // hit itself
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                isDead = true;
            }
        }

        return isDead;
    }




    public void update() {
        // Did the head of the snake eat food
        if (snakeXs[0] == foodX && snakeYs[0] == foodY) {
            eatFood();
        }

        moveSnake();

        if (detectDeath()) {
            //start again
          //  soundPool.play(snake_crash, 1, 1, 0, 0, 1);

            newGame();
        }
    }



    public boolean updateRequired() {

        // Are we due to update the frame
        if(nextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }

    // method to pause the game
    public void pause(){
        isPlaying = false;
        try {
            thread.wait();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // method to resume the game
    public void resume(){
        isPlaying = true;
        thread.notifyAll();
        //thread = new Thread(this);
        //thread.start();
    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:

                switch(heading){
                    case UP:
                        heading = Heading.UP;
                        break;
                    case RIGHT:
                        heading = Heading.RIGHT;
                        break;
                    case DOWN:
                        heading = Heading.DOWN;
                        break;
                    case LEFT:
                        heading = Heading.LEFT;
                        break;
                }

                /*if (motionEvent.getX() >= screenX / 2) {
                    switch(heading){
                        case UP:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.UP;
                            break;
                    }
                } else {
                    switch(heading){
                        case UP:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.UP;
                            break;
                    }
                }*/
        }
        return true;
    }



    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the color of the paint to draw the snake white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Scale the HUD text
            paint.setTextSize(90);
            canvas.drawText("Score:" + score, 10, 70, paint);

            // Draw the snake one block at a time
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            // Set the color of the paint to draw Bob red
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Bob
            canvas.drawRect(foodX * blockSize,
                    (foodY * blockSize),
                    (foodX * blockSize) + blockSize,
                    (foodY * blockSize) + blockSize,
                    paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }
}
