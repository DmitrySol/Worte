package com.app.worte.worte;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileSystemOperator
{
    private List<String> dbFilesList;
    private List<WdbEntry> finalDict;

    private static final String LOG_TAG = "FileSystemOperator";
    private static final String WORTE_FOLDER = "/WorteDb";

    private static final int FIELDS_NUM_IN_WDB_FILE = 3;

    private String dbDirName;

    private File[] dbFiles;
    private File worteDbFolder;

    private void parseDbFile(File dbFile)
    {
        String fileName = dbFile.getName();
        Log.i(LOG_TAG, "Parsing DB file: " + fileName);

        try
        {
            FileInputStream is = new FileInputStream(dbFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null)
            {
                String[] splited = line.split(" \\| ");

                if(splited.length == FIELDS_NUM_IN_WDB_FILE)
                {
                    String newLanguage = splited[0];
                    String origLanguage = splited[1];

                    int knowledge = 0;

                    try
                    {
                        knowledge = Integer.parseInt(splited[2]);
                    }
                    catch (NumberFormatException e)
                    {
                        Log.e(LOG_TAG, "Unable to parse the WDB knowledge: " + splited[2]);
                    }

                    finalDict.add(new WdbEntry(newLanguage, origLanguage, knowledge, fileName));
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
        finalDict = new ArrayList<WdbEntry>();

        dbDirName = Environment.getExternalStorageDirectory().toString() + WORTE_FOLDER;
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
                Collections.sort(dbFilesList, String.CASE_INSENSITIVE_ORDER);
            }
        }
    }

    public List<String> getDbNamesList()
    {
        return dbFilesList;
    }

    public List<WdbEntry> getDictByDbNames(List<String> dbNames)
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

    public void updateWdbFilesFromDict(List<WdbEntry> dict)
    {
        Map<String, List<String>> fileContentDict = new HashMap<String, List<String>>();

        for(WdbEntry wdbEntry : dict)
        {
            if(!fileContentDict.containsKey(wdbEntry.fileName))
            {
                fileContentDict.put(wdbEntry.fileName, new ArrayList<String>());
            }
            List<String> wdbLines = fileContentDict.get(wdbEntry.fileName);

            String curLine = wdbEntry.original + " | " + wdbEntry.translation + " | " + wdbEntry.knowledge;
            wdbLines.add(curLine);
        }

        Set<String> fileNames = fileContentDict.keySet();

        for(String fileName : fileNames)
        {
            try
            {
                File outputWdbFile = new File(dbDirName, fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(outputWdbFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                for(String wdbLine : fileContentDict.get(fileName))
                {
                    outputStreamWriter.write(wdbLine + "\n");
                }
                outputStreamWriter.close();
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "File write failed: " + e.toString());
            }
        }
    }

    public List<WdbEntry> getAllDictionaries()
    {
        for (File file : dbFiles)
        {
            parseDbFile(file);
            Log.i(LOG_TAG, "Added file to DB: " + file.getName());
        }

        return finalDict;
    }
}
