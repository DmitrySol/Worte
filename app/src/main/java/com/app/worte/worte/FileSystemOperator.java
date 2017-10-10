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
    private WorteDefDb defDb;

    private List<String> dbFilesList;
    private List<Pair<String, String>> dict;

    private static final String LOG_TAG = "FileSystemOperator";
    private static final String WORTE_FOLDER = "/WorteDb";

    private final int MIN_DICT_SIZE = 4;

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

                    dict.add(Pair.create(newLanguage, nativeLanguage));
                }
                else
                {
                    Log.w(LOG_TAG, "Found not DB line: " + line);
                }

                Log.d("Line:", line);
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
        defDb = new WorteDefDb();
        dbFilesList = new ArrayList<>();

        String dbDirName = Environment.getExternalStorageDirectory().toString() + WORTE_FOLDER;
        Log.i(LOG_TAG, "DB path " + dbDirName);

        File worteDbFolder = new File(dbDirName);
        boolean isDbFolderCreated = true;

        if (!worteDbFolder.exists())
        {
            isDbFolderCreated = worteDbFolder.mkdirs();
        }

        if(isDbFolderCreated == false)
        {
            dict = defDb.getDefaultDictionary();
            Log.e(LOG_TAG, "Cannot create worte database folder");
        }
        else
        {
            dict = new ArrayList<Pair<String, String>>();

            File[] dbFiles = worteDbFolder.listFiles();
            int dbFilesNum = dbFiles.length;

            if(dbFilesNum == 0)
            {
                dict = defDb.getDefaultDictionary();
                Log.i(LOG_TAG, "DB folder is empty, getting default dictionary");
            }
            else
            {
                for (int i = 0; i < dbFilesNum; i++)
                {
                    String fileName = dbFiles[i].getName();
                    dbFilesList.add(fileName);
                    Log.i(LOG_TAG, "Found DB file: " + fileName);
                    parseDbFile(dbFiles[i]);
                }
            }
            if(dict.size() < MIN_DICT_SIZE)
            {
                // TODO: add default dictionary
            }
        }
    }

    public List<String>  getDbNamesList()
    {
        return dbFilesList;
    }

    public List<Pair<String, String>> getWholeDictionary()
    {
        return dict;
    }
}
