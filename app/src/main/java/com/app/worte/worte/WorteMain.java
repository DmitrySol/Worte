package com.app.worte.worte;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.Snackbar;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class WorteMain extends AppCompatActivity implements View.OnClickListener
{
    Context context;
    Resources resources;

    WorteEngine wEngine;
    Button btnAsk;

    Button btnAnsw1;
    Button btnAnsw2;
    Button btnAnsw3;
    Button btnAnsw4;

    ProgressBar knowlegeBar;

    ConstraintLayout mainLayout;

    WortePreferences wPref;
    List<String> lastPrefList;

    final static String LOG_TAG = "WorteMain";

    private final static int MIN_DICT_SIZE = 4;

    private final static int BAD_PROGRESS_TH = -3;
    private final static int GOOD_PROGRESS_TH = 3;

    /**
     * Id to identify a storage permission request.
     */
    private static final int REQUEST_STORAGE_PERMISSIONS = 0;

    boolean isAnsweringEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worte_main);

        context = getApplicationContext();
        resources = context.getResources();

        btnAsk = (Button) findViewById(R.id.askButton);

        btnAnsw1 = (Button) findViewById(R.id.answ1);
        btnAnsw2 = (Button) findViewById(R.id.answ2);
        btnAnsw3 = (Button) findViewById(R.id.answ3);
        btnAnsw4 = (Button) findViewById(R.id.answ4);

        knowlegeBar = (ProgressBar)findViewById(R.id.progress);

        mainLayout = (ConstraintLayout)findViewById(R.id.mainLayout);

        ensurePermissions();

        btnAsk.setOnClickListener(this);

        btnAnsw1.setOnClickListener(this);
        btnAnsw2.setOnClickListener(this);
        btnAnsw3.setOnClickListener(this);
        btnAnsw4.setOnClickListener(this);
        mainLayout.setOnClickListener(this);

        wPref = new WortePreferences(context);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        List<String> curPrefList = wPref.getChoosenDbList();

        if(curPrefList != null)
        {
            if (!curPrefList.equals(lastPrefList))
            {
                wEngine = new WorteEngine(curPrefList);

                if(wEngine.getDictSize() == 0)
                {
                    showWorteProblem("DB is Empty!");
                }
                else if(wEngine.getDictSize() < MIN_DICT_SIZE)
                {
                    showWorteProblem("DB is too small to show!");
                }
                else
                {
                    lastPrefList = curPrefList;
                    nextQuestion();
                }
            }
        }
        else
        {
            showWorteProblem("No DB selected!");
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        wEngine.saveCurrentWdb();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Storage permissions were granted");
                } else {
                    Log.i(LOG_TAG, "Storage permissions were NOT granted");
                    Log.i(LOG_TAG, "Quit");
                    this.finish();
                }
            }
        }
    }

    private void ensurePermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Log.i(LOG_TAG, "Requesting storage permissions...");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(LOG_TAG, "Displaying storage permission explanation.");
            Snackbar.make(mainLayout, "Worte needs the storage access to read  its databases.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(WorteMain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
        }
    }

    private void showWorteProblem(String problem)
    {
        btnAsk.setText(problem);
        btnAnsw1.setText("-");
        btnAnsw2.setText("-");
        btnAnsw3.setText("-");
        btnAnsw4.setText("-");

        updateProgressBar(0);

        btnAsk.setEnabled(false);
        btnAnsw1.setEnabled(false);
        btnAnsw2.setEnabled(false);
        btnAnsw3.setEnabled(false);
        btnAnsw4.setEnabled(false);
        mainLayout.setEnabled(false);
    }

    private void enableAllButtons()
    {
        btnAsk.setEnabled(true);
        btnAnsw1.setEnabled(true);
        btnAnsw2.setEnabled(true);
        btnAnsw3.setEnabled(true);
        btnAnsw4.setEnabled(true);
        mainLayout.setEnabled(true);
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
            case R.id.menu_prev:
                Log.i(LOG_TAG, "Previous question is Selected");
                previousQuestion();
                return true;
            case R.id.menu_next:
                Log.i(LOG_TAG, "Next question is Selected");
                nextQuestion();
                return true;
            case R.id.menu_choosedb:
                Log.i(LOG_TAG, "Choose Data Base is Selected");

                Intent dbListActivity = new Intent(WorteMain.this, DbListActivity.class);
                startActivity(dbListActivity);

                return true;

            case R.id.menu_quit:
                Log.i(LOG_TAG, "Quit is Selected");
                this.finish();
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
            runGoogleTranslateApp();
        }
        else
        {
            if (isAnsweringEnabled == false)
            {
                nextQuestion();
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
                        Log.w(LOG_TAG, "Unknown button selected!");
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
                wEngine.reportCorrectAnswer();
            }
            else
            {
                Button wrongBtn = getButtonByAnswerID(answId);
                Button correctBtn = getButtonByAnswerID(correctAnswerId);

                wrongBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_wrong));
                correctBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.answ_btn_correct));
                wEngine.reportWrongAnswer();
            }
            updateProgressBar(wEngine.getCurrentKnowledge());

            isAnsweringEnabled = false;
        }
    }

    private void nextQuestion()
    {
        wEngine.moveToNextQuestion();
        UpdateActivityForNewAnswer();
    }

    private void previousQuestion()
    {
        wEngine.moveToPreviousQuestion();
        UpdateActivityForNewAnswer();
    }

    private void UpdateActivityForNewAnswer()
    {
        enableAllButtons();
        isAnsweringEnabled = true;

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

        updateProgressBar(wEngine.getCurrentKnowledge());
    }

    private void updateProgressBar(int progress)
    {
        float finalProgress;

        if (progress < WorteEngine.MIN_PROGRESS)
        {
            finalProgress = WorteEngine.MIN_PROGRESS;
        }
        else if (progress > WorteEngine.MAX_PROGRESS)
        {
            finalProgress = WorteEngine.MAX_PROGRESS;
        }
        else
        {
            finalProgress = progress;
        }

        if (finalProgress < BAD_PROGRESS_TH)
        {
            knowlegeBar.setProgressTintList(resources.getColorStateList(R.color.badProgress, context.getTheme()));
        }
        else if(finalProgress > GOOD_PROGRESS_TH)
        {
            knowlegeBar.setProgressTintList(resources.getColorStateList(R.color.goodProgress, context.getTheme()));
        }
        else
        {
            knowlegeBar.setProgressTintList(resources.getColorStateList(R.color.averageProgress, context.getTheme()));
        }

        int adaptedProgress = (int)(((finalProgress - WorteEngine.MIN_PROGRESS) /
                (WorteEngine.MAX_PROGRESS - WorteEngine.MIN_PROGRESS)) * 100);

        Log.i(LOG_TAG, "New knowlege progress: " + finalProgress);

        knowlegeBar.setProgress(adaptedProgress);
    }

    private void runGoogleTranslateApp()
    {
        try
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            Log.i(LOG_TAG, "Sending Intent to open Google translate for word: " + wEngine.getWorte(WorteTypeId.QUESTION) +
                    ", from: " +  wEngine.getLanguageToTranslateFrom() +
                    ", to: " + wEngine.getLanguageTranslateTo());
            intent.putExtra(Intent.EXTRA_TEXT, wEngine.getWorte(WorteTypeId.QUESTION));
            intent.putExtra("key_text_input", wEngine.getWorte(WorteTypeId.QUESTION));
            intent.putExtra("key_text_output", "");
            intent.putExtra("key_language_from", wEngine.getLanguageToTranslateFrom());
            intent.putExtra("key_language_to", wEngine.getLanguageTranslateTo());
            intent.putExtra("key_suggest_translation", "");
            intent.putExtra("key_from_floating_window", false);
            intent.setComponent(new ComponentName("com.google.android.apps.translate",
                    "com.google.android.apps.translate.TranslateActivity"));
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(getApplication(), "No Google Translation Installed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
