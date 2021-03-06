package com.app.worte.worte;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WortePreferences
{
    final String CHOOSEN_DB_KEY = "WDB_KEY";

    private Context appCtx;
    private SharedPreferences sPref;

    public WortePreferences(Context appContext)
    {
        this.appCtx = appContext;
    }

    public void saveChoosenDbList(String dbList)
    {
        sPref = appCtx.getSharedPreferences(CHOOSEN_DB_KEY, Context.MODE_PRIVATE);

        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(CHOOSEN_DB_KEY, dbList);
        ed.commit();
    }

    public List<String> getChoosenDbList()
    {
        sPref = appCtx.getSharedPreferences(CHOOSEN_DB_KEY, Context.MODE_PRIVATE);
        String sPrefStr = sPref.getString(CHOOSEN_DB_KEY, null);

        List<String> chosenDbNamesList = null;

        if(sPrefStr != null)
        {
            chosenDbNamesList = new ArrayList<String>(Arrays.asList(sPrefStr.split("%")));
        }

        return chosenDbNamesList;
    }
}
