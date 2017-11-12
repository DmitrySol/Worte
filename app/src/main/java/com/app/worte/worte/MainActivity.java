package com.app.worte.worte;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String LOG_TAG = "MainActivity";

    /**
     * Id to identify a storage permission request.
     */
    private static final int REQUEST_STORAGE_PERMISSIONS = 0;

    private static final String EXERCISE_MULTIPLE_SELECT = "EXERCISE_MULTIPLE_SELECT";
    private static final String EXERCISE_CARDS = "EXERCISE_CARDS";
    private static final String EXERCISE_NONE = "NONE";

    WortePreferences wPref;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button button_MultipleSelect = (Button) findViewById(R.id.button_MultipleSelect);
        button_MultipleSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startExerciseMultipleSelect();
            }
        });

        final Button button_Cards = (Button) findViewById(R.id.button_CardsSelect);
        button_Cards.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, "Not implemented yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not implemented yet", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ensurePermissions();
        wPref = new WortePreferences(getApplicationContext());

        String lastActiveExercise = wPref.getLastActiveExercise();
        Log.i(LOG_TAG, "Last exercise was: " + lastActiveExercise);
        if (Objects.equals(lastActiveExercise, EXERCISE_MULTIPLE_SELECT)) {
            startExerciseMultipleSelect();
        }
        if (Objects.equals(lastActiveExercise, EXERCISE_NONE)) {
            Log.i(LOG_TAG, "Las exercise was NONE");
        } else {
            Log.e(LOG_TAG, "Unknown last exercise");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Om resume");
        wPref.saveLastActiveExercise(EXERCISE_NONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_multiple_select) {
            startExerciseMultipleSelect();
        } else if (id == R.id.nav_cards) {

        } else if (id == R.id.nav_share) {
            Log.i(LOG_TAG, "Choose Data Base is Selected");

            Intent dbListActivity = new Intent(MainActivity.this, DbListActivity.class);
            startActivity(dbListActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void startExerciseMultipleSelect() {
        Log.i(LOG_TAG, "Starting exercise: MultipleSelect");
        Intent exerciseMultipleSelect = new Intent(MainActivity.this, MultipleSelectActivity.class);
        startActivity(exerciseMultipleSelect);
        wPref.saveLastActiveExercise(EXERCISE_MULTIPLE_SELECT);
    }

    private void startExerciseCards() {
        Log.i(LOG_TAG, "Starting exercise: Cards");
        Log.i(LOG_TAG, "Not implemented yet :(");
//        wPref.saveLastActiveExercise(EXERCISE_CARDS);
    }

    private void ensurePermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        Log.i(LOG_TAG, "Requesting storage permissions...");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
    }
}
