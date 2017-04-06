package com.example.sethq.quizapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        toolbar.setTitle(R.string.app_name);
    }

//    Buttons methods

//Start button
//Opening the category menu

   public void startButton(View v)
   {
       Intent intent= new Intent (this, CategoryActivity.class);
       startActivity(intent);

   }

//    Opening score table
    public void scoreButton(View v)
    {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }



//    Push exit button method..
//    for closing app
   public void exitApp (View v)
    {

        System.exit(0);

    }

}
