package com.example.sethq.quizapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import org.w3c.dom.Text;

/**
 * Created by SethQ on 2017-03-02.
 */

public class QuestionActivity extends AppCompatActivity {

    //private fileds of the question activity
    //used only inside of this activity
    private List<QuestionContainer> questionHolder;
    private  int questionIndex;
    private int score;
    private  boolean isActiveButton;
    private String selectedAnserwButton;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        String fileName = getIntent().getStringExtra("SELECTED");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        questionHolder = new ArrayList<>();
        selectedAnserwButton = null;
        questionIndex = 0;
        isActiveButton = false;
        score =0;




        //Sending bytes from file through stream
        String str = loadQuestonFile(fileName);

        try {
            pharseJsonFile(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initilizeAnserwButtonListeners();
        fillFields(questionIndex);
        setUpSeekerBar();

        //setting up toolbar and adding home button
        Toolbar toolbar = (Toolbar) findViewById(R.id.question_activity_toolbar);
        toolbar.setTitle(getResources().getString(R.string.question_activity_title));
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        backToCategoryAcitvity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToCategoryAcitvity();
        }
        if (item.getItemId() == R.id.forward)
        {
            onClickNext();
        }
        return super.onOptionsItemSelected(item);
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
                if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0)
                {
                   onClickNext();
                }
                if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0)
                {
                    backToCategoryAcitvity();
                }

                break;
        }

        return super.onTouchEvent(event);
    }


    //back function
    private void  backToCategoryAcitvity()
    {
        Intent intent = new Intent (this, CategoryActivity.class);
        startActivity(intent);
        finish();
    }
    //  we goes to next question or if there are not left any
    //we open summary activity

    private void onClickNext ()
    {
        if (selectedAnserwButton == null)
        {
            Toast.makeText(this, "Please select anserw", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questionIndex < questionHolder.size()-1)
        {
            if (checkCorrectAnserw())
                score += 1;
            clearImageView();
            questionIndex ++;
            fillFields(questionIndex);
            resetButtonState();
            updateSeekBar(questionIndex);
            selectedAnserwButton = null;
            isActiveButton = false;

            Toolbar toolbar = (Toolbar) findViewById(R.id.question_activity_toolbar);
            toolbar.setTitle(getResources().getString(R.string.question_activity_title, questionIndex + 1));


        }
        
        else
        {
            if (checkCorrectAnserw())
                score += 1;

            Intent intent = new Intent (this, SummaryActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("MAX", questionHolder.size());
            intent.putExtra("CATEGORY", category);
            startActivity(intent);
        }





    }

    private void setUpSeekerBar ()
    {
        SeekBar bar = (SeekBar) findViewById(R.id.question_activity_seek_bar);
        bar.setClickable(false);
        bar.setMax(questionHolder.size()-1);
    }

    private void updateSeekBar(int pos)
    {
        SeekBar bar = (SeekBar) findViewById(R.id.question_activity_seek_bar);
        bar.setProgress(pos);

    }

    private void clearImageView ()
    {
        ImageView view = (ImageView) findViewById(R.id.question_activity_image_view);
        view.setImageResource(0);
    }
    private boolean checkCorrectAnserw ()
    {
        int correct = questionHolder.get(questionIndex).getCorrectAnserw();
        int selectedAnserw = Integer.parseInt(selectedAnserwButton.split("_")[3]);

        if (correct == selectedAnserw)
            return true;
        else
            return false;
    }

    //Loading  question json file from assets adequate to the fileTpye passed to the function
    // from category activity
    private String loadQuestonFile(String fileName)
    {
        String fileContent = null;
        fileName = fileName + ".json";
        try{
            InputStream stream = getAssets().open(fileName);
            int size = stream.available();
            byte buffer [] = new byte[size];
            stream.read(buffer);
            Log.i("File byte size  ", Integer.toString(buffer.length));
            fileContent = new String (buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            Log.i("Unable to open  ", "file");
            return null;
        }

        return fileContent;
    }


    //It is used for pharsing the json file to retrive
    //qunestions, anserws and img resurce names
    private void pharseJsonFile (String fileContent) throws JSONException {

        JSONObject obj;
        Log.i("JSon  ", "creating obj");


        obj = new JSONObject(fileContent);
        String questionTag = "question";

        int i = 1;
        Log.i("JSon start ", "pharsing");

        category = obj.getString("category");
        Log.i("category_field in cat ", category);

        while (obj.getJSONObject(questionTag + i) != null) {

            List<String> anserw = new <String>ArrayList();
            Log.i("Question numb", questionTag+i);
            Log.i("loop start", "retriving data");
            String question = obj.getJSONObject(questionTag + i).getString("question");
            Log.i("Question", question);
            String imgRes = obj.getJSONObject(questionTag + i).getString("image_id");
            Log.i("Question", question);
            int correctAnserw = obj.getJSONObject(questionTag + i).getJSONObject("anserws").getInt("correct");
            Log.i("Question", question);


            for (int j = 1; j <= 4; j++) {
                String temp = obj.getJSONObject(questionTag + i).getJSONObject("anserws").getString(Integer.toString(j));
                anserw.add(temp);
            }

            questionHolder.add(new QuestionContainer(question, correctAnserw, imgRes, anserw));
            Log.d("anserw list ", Integer.toString(anserw.size()));

            Log.d("size: ", Integer.toString(questionHolder.size()));

            i++;

        }

    }


    // function to fill question activity fields
    private void fillFields ( int index ) {
        // Setting question text
        TextView text = (TextView) findViewById(R.id.question_activity_text_view);
        text.setText(getQuestionData(index).getQuestion());


        //Setting image if there is any
        if (!getQuestionData(index).imgName.equals("false")) {
            Log.d("Loading img ", "starts");
            ImageView imgView = (ImageView) findViewById(R.id.question_activity_image_view);


            int res = 0;
            try {
                res = getResources().getIdentifier(getQuestionData(index).getImageResource(), "drawable", getPackageName());
            } catch (Resources.NotFoundException e) {
                Log.d("No item: ", "exit");
                return;
            }

            Log.d("Loading img ", Integer.toString(res));
            imgView.setImageResource(res);
        }

        //Setting anserws
        // dynamicly itterate through all 4 buttons
        String[] anserwsTags = {"A)", "B)", "C)", "D)"};
        for (int i = 0; i <= 3; i++) {
            String name = "question_activity_button_" + (i + 1);
            int res;

            try {
                res = getResources().getIdentifier(name, "id", getPackageName());
            } catch (Resources.NotFoundException e) {
                return;
            }

            final Button btn = (Button) findViewById(res);
            if (btn != null)
                btn.setText(anserwsTags[i] + " " + getQuestionData(index).getAnserws().get(i));


        }
    }
    private void initilizeAnserwButtonListeners ()
    {
        for ( int i = 0; i<=3; i++ )
        {
            String name = "question_activity_button_" + (i+1);
            int res;

            try
            {
                res = getResources().getIdentifier(name, "id", getPackageName());
            }
            catch (Resources.NotFoundException e)
            {
                return;
            }

            final Button btn = (Button) findViewById(res);


            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    String tempName = v.getResources().getResourceName(v.getId());

                    if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

                    if (event.getAction() != MotionEvent.ACTION_UP) return false;
                    if(!isActiveButton)
                    {
                        Log.d("False statment ", "has been triggered");
                        btn.setPressed(true);
                        isActiveButton = true;
                        selectedAnserwButton = tempName;
                        btn.setTypeface(null, Typeface.BOLD);
                        btn.setTextColor(Color.WHITE);


                    }

                    else

                    if (selectedAnserwButton.equals(tempName))
                    {
                        Log.d("True statment ", "has been triggered");
                        btn.setPressed(false);
                        isActiveButton = false;
                        selectedAnserwButton = null;
                        btn.setTypeface(null, Typeface.NORMAL);
                        btn.setTextColor(Color.BLACK);

                    }

                    return true;
                }
            });

        }
    }

    private void resetButtonState()
    {
        for ( int i = 0; i<=3; i++ )
        {
            String name = "question_activity_button_" + (i+1);
            int res;

            try
            {
                res = getResources().getIdentifier(name, "id", getPackageName());
            }
            catch (Resources.NotFoundException e)
            {
                return;
            }

            final Button btn = (Button) findViewById(res);


            btn.setPressed(false);
            btn.setTypeface(null, Typeface.NORMAL);
            btn.setTextColor(Color.BLACK);
        }
    }
    //function for retriving data about specified question
    private QuestionContainer getQuestionData (int index)
    {
        return  questionHolder.get(index);
    }




    //private inner class
    //used only in question activity
    private static class QuestionContainer
    {
        //fields of the container
        private final String question;
        private final int correctAnserw;
        private final String imgName;
        // list to store anserws from json file
        private final List<String> anserwsList;

        //constructor
        public QuestionContainer (String arg_q, int arg_correct, String arg_img, List<String> arg_anserws)
        {
            question = arg_q;
            correctAnserw = arg_correct;
            imgName = arg_img;
            anserwsList = arg_anserws;
        };

        public String getQuestion ()
        {
            return question;
        }

        public int getCorrectAnserw()
        {
            return correctAnserw;
        }

        public boolean hasImg()
        {
            if (!imgName.equals("false"))
            return true;
            else
            return false;
        }

        public String getImageResource()
        {
         return imgName;
        }

        public List<String> getAnserws ()
        {
            return anserwsList;
        }
    }



  }
