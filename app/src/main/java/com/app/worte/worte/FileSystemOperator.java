package com.app.worte.worte;

import android.os.Environment;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileSystemOperator
{
    private List<String> dbFilesList;
    private List<Pair<String, String>> finalDict;

    private static final String LOG_TAG = "FileSystemOperator";
    private static final String WORTE_FOLDER = "/WorteDb";

    private File[] dbFiles;
    private File worteDbFolder;

    private void parseDbFile(File dbFile)
    {
        Log.i(LOG_TAG, "Parsing DB file: " + dbFile.getName());

        try
        {
            FileInputStream is = new FileInputStream(dbFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null)
            {
                String[] splited = line.split(" - ");

                if(splited.length == 2)
                {
                    String newLanguage = splited[0];
                    String nativeLanguage = splited[1];

                    finalDict.add(Pair.create(newLanguage, nativeLanguage));
                }
                else
                {
                    Log.w(LOG_TAG, "Found not DB line: " + line);
                }

                line = reader.readLine();
            }
        }

        catch(FileNotFoundException e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public FileSystemOperator()
    {
        dbFilesList = new ArrayList<>();
        finalDict = new ArrayList<Pair<String, String>>();

        String dbDirName = Environment.getExternalStorageDirectory().toString() + WORTE_FOLDER;
        Log.i(LOG_TAG, "DB path " + dbDirName);

        worteDbFolder = new File(dbDirName);
        boolean isDbFolderCreated = true;

        if (!worteDbFolder.exists())
        {
            isDbFolderCreated = worteDbFolder.mkdirs();
        }

        if(isDbFolderCreated == false)
        {
            Log.e(LOG_TAG, "Worte DB doesn't exists and unable to create new one");
        }
        else
        {
            dbFiles = worteDbFolder.listFiles();

            for (File file : dbFiles)
            {
                if(!file.isDirectory() && file.getName().endsWith(".wdb"))
                {
                    dbFilesList.add(file.getName());
                }
            }
        }
    }

    public List<String> getDbNamesList()
    {
        return dbFilesList;
    }

    public List<Pair<String, String>> getDictByDbNames(List<String> dbNames)
    {
        finalDict.clear();

        for (File file : dbFiles)
        {
            if( dbNames.contains(file.getName()) )
            {
                parseDbFile(file);
                Log.i(LOG_TAG, "Added file to DB: " + file.getName());
            }
        }

        return finalDict;
    }

    public List<Pair<String, String>> getAllDictionaries()
    {
        for (File file : dbFiles)
        {
            parseDbFile(file);
            Log.i(LOG_TAG, "Added file to DB: " + file.getName());
        }

        return finalDict;
    }
}
