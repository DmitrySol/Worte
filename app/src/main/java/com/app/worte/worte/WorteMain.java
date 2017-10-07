package com.app.worte.worte;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WorteMain extends AppCompatActivity implements View.OnClickListener
{
    Button btnAsk;

    Button btnAnsw1;
    Button btnAnsw2;
    Button btnAnsw3;
    Button btnAnsw4;

    static String LOG_TAG = "WorteMain";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worte_main);

        btnAsk = (Button) findViewById(R.id.askButton);

        btnAnsw1 = (Button) findViewById(R.id.answ1);
        btnAnsw2 = (Button) findViewById(R.id.answ2);
        btnAnsw3 = (Button) findViewById(R.id.answ3);
        btnAnsw4 = (Button) findViewById(R.id.answ4);

        btnAsk.setOnClickListener(this);

        btnAnsw1.setOnClickListener(this);
        btnAnsw2.setOnClickListener(this);
        btnAnsw3.setOnClickListener(this);
        btnAnsw4.setOnClickListener(this);
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
        Context context = getApplicationContext();

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
        switch (v.getId())
        {
            case R.id.askButton:
                Log.i(LOG_TAG, "Ask Button clicked");
                break;
            case R.id.answ1:
                Log.i(LOG_TAG, "Answer 1 selected");
                break;
            case R.id.answ2:
                Log.i(LOG_TAG, "Answer 2 selected");
                break;
            case R.id.answ3:
                Log.i(LOG_TAG, "Answer 3 selected");
                break;
            case R.id.answ4:
                Log.i(LOG_TAG, "Answer 4 selected");
                break;
            default:
                Log.w(LOG_TAG, "Inknown button selected!");
                break;
        }
    }
}
