package com.app.worte.worte;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class WorteMain extends AppCompatActivity implements View.OnClickListener
{
    Context context;

    WorteEngine wEngine;
    Button btnAsk;

    Button btnAnsw1;
    Button btnAnsw2;
    Button btnAnsw3;
    Button btnAnsw4;

    ConstraintLayout mainLayout;

    static String LOG_TAG = "WorteMain";

    boolean isAnsweringEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worte_main);

        context = getApplicationContext();

        wEngine = new WorteEngine();

        btnAsk = (Button) findViewById(R.id.askButton);

        btnAnsw1 = (Button) findViewById(R.id.answ1);
        btnAnsw2 = (Button) findViewById(R.id.answ2);
        btnAnsw3 = (Button) findViewById(R.id.answ3);
        btnAnsw4 = (Button) findViewById(R.id.answ4);

        mainLayout = (ConstraintLayout)findViewById(R.id.mainLayout);

        btnAsk.setOnClickListener(this);

        btnAnsw1.setOnClickListener(this);
        btnAnsw2.setOnClickListener(this);
        btnAnsw3.setOnClickListener(this);
        btnAnsw4.setOnClickListener(this);
        mainLayout.setOnClickListener(this);

        UpdateActivityForNewAnswer();
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_chosedb:
                Log.i(LOG_TAG, "Choose Data Base is Selected");
                return true;

            case R.id.menu_settings:
                Log.i(LOG_TAG, "Settings is Selected");
                return true;

            case R.id.menu_quit:
                Log.i(LOG_TAG, "Quit is Selected");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.askButton)
        {
            Log.i(LOG_TAG, "Ask Button clicked");
        }
        else
        {
            if (isAnsweringEnabled == false)
            {
                UpdateActivityForNewAnswer();
            }
            else
            {
                switch (v.getId())
                {
                    case R.id.mainLayout:
                        Log.i(LOG_TAG, "Main layout clicked");
                        break;
                    case R.id.answ1:
                        Log.i(LOG_TAG, "Answer 1 selected");
                        checkAnswer(WorteTypeId.ANS_1);
                        break;
                    case R.id.answ2:
                        Log.i(LOG_TAG, "Answer 2 selected");
                        checkAnswer(WorteTypeId.ANS_2);
                        break;
                    case R.id.answ3:
                        Log.i(LOG_TAG, "Answer 3 selected");
                        checkAnswer(WorteTypeId.ANS_3);
                        break;
                    case R.id.answ4:
                        Log.i(LOG_TAG, "Answer 4 selected");
                        checkAnswer(WorteTypeId.ANS_4);
                        break;
                    default:
                        Log.w(LOG_TAG, "Inknown button selected!");
                        break;
                }
            }
        }
    }

    private Button getButtonByAnswerID(int id)
    {
        Button b;

        switch (id)
        {
            case WorteTypeId.QUESTION:
                b = btnAsk;
                break;
            case WorteTypeId.ANS_1:
                b = btnAnsw1;
                break;
            case WorteTypeId.ANS_2:
                b = btnAnsw2;
                break;
            case WorteTypeId.ANS_3:
                b = btnAnsw3;
                break;
            case WorteTypeId.ANS_4:
                b = btnAnsw4;
                break;
            default:
                throw new NullPointerException("Unknown ID");
        }

        return b;
    }

    private void checkAnswer(int answId)
    {
        if(isAnsweringEnabled == true)
        {
            int correctAnswerId = wEngine.getCorrectId();

            if (answId == correctAnswerId)
            {
                Button correctBtn = getButtonByAnswerID(answId);
                correctBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_correct));
                btnAsk.setBackground(ContextCompat.getDrawable(context, R.drawable.ask_btn_correct));
            }
            else
            {
                Button wrongBtn = getButtonByAnswerID(answId);
                Button correctBtn = getButtonByAnswerID(correctAnswerId);

                wrongBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_wrong));
                correctBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_correct));
            }
            isAnsweringEnabled = false;
        }
    }

    private void UpdateActivityForNewAnswer()
    {
        isAnsweringEnabled = true;

        wEngine.generateNextQuestion();

        btnAsk.setBackground(ContextCompat.getDrawable(context, R.drawable.ask_btn_def));

        btnAnsw1.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_def));
        btnAnsw2.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_def));
        btnAnsw3.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_def));
        btnAnsw4.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_def));

        btnAsk.setText(wEngine.getWorte(WorteTypeId.QUESTION));

        btnAnsw1.setText(wEngine.getWorte(WorteTypeId.ANS_1));
        btnAnsw2.setText(wEngine.getWorte(WorteTypeId.ANS_2));
        btnAnsw3.setText(wEngine.getWorte(WorteTypeId.ANS_3));
        btnAnsw4.setText(wEngine.getWorte(WorteTypeId.ANS_4));
    }
}
