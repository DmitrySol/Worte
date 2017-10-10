package com.app.worte.worte;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class FileSystemOperator
{
    WorteDefDb defDb;
    static String LOG_TAG = "FileSystemOperator";

    public FileSystemOperator()
    {
        defDb = new WorteDefDb();
    }

    public List<String>  getDbNamesList()
    {
        List<String> dbList = new ArrayList<>();

        // TODO: Implement

        return dbList;
    }

    public List<Pair<String, String>> getWholeDictionary()
    {
        List<Pair<String, String>> dict = defDb.getDefaultDictionary();

        return dict;
    }
}
