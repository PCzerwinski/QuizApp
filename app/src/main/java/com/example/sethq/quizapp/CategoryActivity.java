package com.example.sethq.quizapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by SethQ on 2017-02-28.
 */

public class CategoryActivity extends AppCompatActivity {

    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private boolean isActiveButton;
    private String selectedCategoryId;
    int currentSelected;
    private ActionMenuView menuV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        currentSelected = 0;
        isActiveButton = false;
        selectedCategoryId = null;


        bindOnTouchListener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_category__toolbar);
        toolbar.showOverflowMenu();

        toolbar.setTitle(R.string.title_category);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() !=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

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
                    ButtonSubmmitClick();
                }

                if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0)
                {
                   backToMainAcitvity();
                }


                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
       backToMainAcitvity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.category_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        if (item.getItemId() == R.id.forward)
        {
            ButtonSubmmitClick();
        }
        return super.onOptionsItemSelected(item);
    }

    public void ButtonSubmmitClick ()
    {
        if (currentSelected == 0)
        {
            Toast.makeText(this, "Please select category", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("SELECTED", Integer.toString(currentSelected));
            startActivity(intent);
        }

    }

    //on back action invoked
    private void  backToMainAcitvity()
    {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //binging listeners to buttons
    private void bindOnTouchListener ()
    {
            int res;
            String name;
            String id;
            int i = 1;

            while(true)
            {
                 name = "category_button_id_" + i;

                Log.d("Button name ", name);

                try
                {
                    res = getResources().getIdentifier(name, "id", getPackageName());
                    id = getResources().getResourceName(res);
                }
                catch(Resources.NotFoundException e)
                {   Log.d ("No item: ","exit");
                    return;
                }

                 final Button buttn = (Button) findViewById(res);

                Log.d("Button id name", id);

                i += 1;


                // adding onTouch listener if element has been found

                if (buttn != null)
                {

                    Log.d("Button state", buttn.toString());
                    buttn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            String tempName = v.getResources().getResourceName(v.getId());
                            if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

                            if (event.getAction() != MotionEvent.ACTION_UP) return false;
                            if(!isActiveButton)
                            {
                                Log.d("False statment ", "has been triggered");
                                buttn.setPressed(true);
                                isActiveButton = true;
                                buttn.setTextColor(Color.BLACK);
                                selectedCategoryId = tempName;
                                Log.d("Button id ", selectedCategoryId);
                                currentSelected = Integer.parseInt(selectedCategoryId.split("_")[3]);
                                Log.d("Button id ", Integer.toString(currentSelected));

                            }

                            else

                            if (selectedCategoryId.equals(tempName))
                            {
                                Log.d("True statment ", "has been triggered");
                                buttn.setPressed(false);
                                buttn.setTextColor(Color.WHITE);
                                isActiveButton = false;
                                selectedCategoryId = null;
                                currentSelected = 0;
                            }

                            return true;
                        }

                    });
               // buttn.setOnTouchListener(new MyCustomOnTouchListener(buttonState);
                }
                else
                    return;


            }



    }
}
