package com.example.divyang.myapplication;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

public class MyFirstApp extends Activity {

    SnakeEngine snakeEngine;
    protected Button button;

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


        button = (Button) findViewById(R.id.button_id);
        if(button!=null){
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    button.setVisibility(View.GONE);
                    onResume();
                }
            });
        }

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
