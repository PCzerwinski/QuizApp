package com.example.sethq.quizapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SethQ on 2017-03-10.
 */

public class SummaryActivity extends AppCompatActivity {

    //variables to store total number of questions and score
    //used later to calculate %% and star rating

    private int maxScore;
    private int actualScore;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //Setting up custom toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.summary_activity_toolbar);
        toolbar.setTitle(R.string.summary_activity_title);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        maxScore = intent.getIntExtra("MAX",0 ) ;
        actualScore = intent.getIntExtra("SCORE", 0 );
        category = intent.getStringExtra("CATEGORY");
        Log.i("category_field in summ", category);

    if (actualScore > 0) checkScore(actualScore);

        int progress = calculatePercentScore((double)actualScore, (double)maxScore);
        setSeekBarrProgress(progress);
        setRattingBar((double)actualScore, (double)maxScore);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    backToCategoryACtivity();
                }

                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summary_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.forward)
        {
           backToCategoryACtivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backToCategoryACtivity();
    }
    private int calculatePercentScore(double score, double max)
    {
        double temp = score/max;
        int progress = (int) (temp * 100);
        return progress;
    }

    private void setSeekBarrProgress(int data)
    {
       ProgressBar bar = (ProgressBar) findViewById(R.id.summary_activity_progressBar);
        bar.setProgress(data);

        TextView text = (TextView) findViewById(R.id.summary_activity_text_view_progress);
        text.setText(Integer.toString(data)+ "%");

    }

    private void setRattingBar (double score, double max)
    {
        RatingBar bar = (RatingBar) findViewById(R.id.summar_activity_rating_bar);
        double maxProgress = bar.getMax();

        double temp = score/max;
        temp = maxProgress * temp;

        bar.setRating((float)temp);
    }

    //on back action invocked
    private void backToCategoryACtivity()
    {
        Intent intent = new Intent (this, CategoryActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkScore (int score)
    {

        String file = ScoreActivity.openFile(this);
        List<ScoreActivity.ScoreContainer> data = new ArrayList<>();


        if (file !=null)
        {
            data = ScoreActivity.pharseScoreJsonFile(file);
            createJsonFile();
        }

        if (file == null) createJsonFile();



        int bestScore;
        String bestStringScore;
        int prevScore;
        String prevStringScore;
        prevScore = actualScore;
        prevStringScore = category;


        if (data.size()==3) {
            int i = 0;
            for (; i < data.size(); i++) {
                if (data.get(i).getScore() < prevScore) {
                    bestScore = prevScore;
                    bestStringScore = prevStringScore;

                    prevScore = data.get(i).getScore();
                    prevStringScore = data.get(i).getText();
                    data.get(i).setScore(bestScore);
                    data.get(i).setText(bestStringScore);

                    Log.d("Score at pos" + Integer.toString(i), String.valueOf(Integer.toString(data.get(i).getScore())));
                    Log.d("Current score", String.valueOf(Integer.toString(bestScore)));
                }
            }

        }

        if (data.size()<3 && data.size()!=0 )
        {   int i = 0;
            for (; i < data.size(); i++) {
                if (data.get(i).getScore() < prevScore) {
                    bestScore = prevScore;
                    bestStringScore = prevStringScore;

                    prevScore = data.get(i).getScore();
                    prevStringScore = data.get(i).getText();
                    data.get(i).setScore(bestScore);
                    data.get(i).setText(bestStringScore);

                    Log.d("Score at pos" + Integer.toString(i), String.valueOf(Integer.toString(data.get(i).getScore())));
                    Log.d("Current score", String.valueOf(Integer.toString(bestScore)));
                }

            }
            data.add(new ScoreActivity.ScoreContainer(prevStringScore, prevScore));

        }
        if (data.size() == 0)
        {
            ScoreActivity.ScoreContainer container = new ScoreActivity.ScoreContainer(category, actualScore);
            data.add(container);
        }


        //saving score to json file
        JSONObject obj = makeJsonObject(data);
        saveData (obj);


    }

    private void saveData (JSONObject obj)
    {

        try {
            File f = new File (this.getFilesDir(), "score.json");
            FileOutputStream file = new FileOutputStream(f,false );
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, "UTF-8");
            outputStreamWriter.write(obj.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return;
        }
    }

    private JSONObject makeJsonObject(List<ScoreActivity.ScoreContainer> data)
    {
        JSONObject mainFile = new JSONObject();
        try
        {

            for (int i = 0; i <data.size(); i++)
            {
               JSONObject obj = new JSONObject();
               obj.put("category", data.get(i).getText());
               obj.put("score", data.get(i).getScore());

               mainFile.put(Integer.toString(i+1), obj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mainFile;

    }


    //create .json file by calling file constructor
    private void createJsonFile () {

        File file = new File(this.getFilesDir(), "score.json");
    }




}

