package com.example.divyang.myapplication;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MyFirstApp extends Activity {

    SnakeEngine snakeEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_first_app);

        //getting default display of endroid
        Display display =  getWindowManager().getDefaultDisplay();

        // Initialize the result into a Point object (pixels of device)
        Point size = new Point();
        display.getSize(size);

        // Create a new instance of the SnakeEngine class
        snakeEngine = new SnakeEngine(this, size);

        // Make snakeEngine the view of the Activity
        setContentView(snakeEngine);
    }


    @Override
    protected void onResume() {
        super.onResume();
        snakeEngine.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        snakeEngine.pause();
    }
}
