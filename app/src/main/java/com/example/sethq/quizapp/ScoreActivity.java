package com.example.sethq.quizapp;

import android.content.Context;
import android.content.pm.ActivityInfo;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by SethQ on 2017-04-03.
 */

public class ScoreActivity extends AppCompatActivity {


    int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.score_activity_toolbar);
        toolbar.setTitle(R.string.score_activity_title);
        setSupportActionBar(toolbar);


        String scoreData = openFile(this);


            if (scoreData!= null)
            {
                //List <ScoreContainer> data = new ArrayList<ScoreContainer>( );
                Log.i("Size",Integer.toString(pharseScoreJsonFile(scoreData).size()));
                loadData(pharseScoreJsonFile(scoreData));
            }







        if (getSupportActionBar() !=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x1,x2 =0;
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x2 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x1 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0)
                {
                   finish();
                }

                if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0)
                {
                    finish();
                }


                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return true;
    }


    public static String openFile ( Context context)
    {
        String fileContent = null;

        try {
            InputStream inputStream = context.openFileInput("score.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                fileContent = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }




        return fileContent;
    }

    public static List<ScoreContainer> pharseScoreJsonFile(String file)
    {
        JSONObject obj = null;


        try {
            obj = new JSONObject(file);
        }
        catch (JSONException e) {

        }


        int i = 1;
        Log.i("JSon start ", "pharsing");
        List<ScoreContainer> dataList = new ArrayList<ScoreContainer>();
        if( obj!= null) {


            try
            {

                while ( obj.getJSONObject(Integer.toString(i)) != null){



                    String data;

                    data = obj.getJSONObject(Integer.toString(i)).getString("category");
                    String score = obj.getJSONObject(Integer.toString(i)).getString("score");
                    dataList.add( new ScoreContainer(data, Integer.parseInt(score)));

                    Log.i("Pharse Score", dataList.get(i-1).getText() + dataList.get(i-1).getScore() );

                    i++;


                }
            }
            catch (JSONException e)
            {

            }
        }




        return dataList;
    }

    private void loadData (List<ScoreContainer> dataList)
    {      int i =0;

        dataList.size();
            do
        {

            int id = getResources().getIdentifier("score_activity_point_text_view_"+(i+1), "id", getPackageName());
            TextView text = (TextView) findViewById(id);
            if (text!= null)
            {
                text.setText( (i+1) + ". " + dataList.get(i).getText() + " " + dataList.get(i).getScore() + " " + getResources().getString(R.string.score_activity_point));
                Log.i("Index", dataList.get(i).getText());
            }

            i++;
        } while (i<dataList.size());
    }


    public static class ScoreContainer
    {
     private String text;
     private int score;


        ScoreContainer(String text, int score)
        {
            this.score = score;
            this.text = text;

        }

        public String getText()
        {
            return text;
        }

        public int getScore()
        {
            return score;
        }

        public void setScore (int score)
        {
            this.score = score;
        }

        public void setText (String text)
        {
            this.text =text;
        }


    }
}
