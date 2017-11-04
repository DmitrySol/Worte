package com.app.worte.worte;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class WorteTypeId
{
    public final static int QUESTION = 0;

    public final static int ANS_1 = 1;
    public final static int ANS_2 = 2;
    public final static int ANS_3 = 3;
    public final static int ANS_4 = 4;
}

class WorteUnit
{
    private final String LOG_TAG = "WorteUnit";
    private String question;

    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    private int correscAnswerId;

    WorteUnit()
    {
        question = "";
        answer1 = "";
        answer2 = "";
        answer3 = "";
        answer4 = "";

        correscAnswerId = 1;
    }

    public void setUnitById(String unit, int typeId)
    {
        switch (typeId)
        {
            case WorteTypeId.QUESTION:
                question = unit;
                break;
            case WorteTypeId.ANS_1:
                answer1 = unit;
                break;
            case WorteTypeId.ANS_2:
                answer2 = unit;
                break;
            case WorteTypeId.ANS_3:
                answer3 = unit;
                break;
            case WorteTypeId.ANS_4:
                answer4 = unit;
                break;
            default:
                Log.e(LOG_TAG, "Unknown type ID");
                break;
        }
    }

    public void setCorrectAnswerId(int answerId)
    {
        this.correscAnswerId = answerId;
    }

    public String getUnitById(int typeId)
    {
        String unitName = "";

        switch (typeId)
        {
            case WorteTypeId.QUESTION:
                unitName = question;
                break;
            case WorteTypeId.ANS_1:
                unitName = answer1;
                break;
            case WorteTypeId.ANS_2:
                unitName = answer2;
                break;
            case WorteTypeId.ANS_3:
                unitName = answer3;
                break;
            case WorteTypeId.ANS_4:
                unitName = answer4;
                break;
            default:
                Log.e(LOG_TAG, "Unknown type ID");
                break;
        }

        return unitName;
    }

    public int getCorrectAnswerId()
    {
        return correscAnswerId;
    }
}

public class WorteEngine
{
    private final String LOG_TAG = "WorteEngine";
//    private final int MIN_DICT_SIZE = 4;

    private boolean isQuestionGenerated = false;

    private FileSystemOperator fsOperator;
    private List<Pair<String, String>> dict;
    private int dictSize;

    private List<WorteUnit> worteUnitList;
    private WorteUnit currentWorteUnit;

    private int currentWorteUnitId;

    public WorteEngine(List<String> preferedDbList)
    {
        fsOperator = new FileSystemOperator();

        dict = fsOperator.getDictByDbNames(preferedDbList);

//        if (dict.size() < MIN_DICT_SIZE)
//        {
//            dict = new WorteDefDb().getDefaultDictionary();
//            Log.w(LOG_TAG, "Database is empty, taken default");
//        }

        dictSize = dict.size();

        worteUnitList = new ArrayList<WorteUnit>();
        currentWorteUnit = new WorteUnit();
        currentWorteUnitId = -1;

        Log.i(LOG_TAG, "Size of received dict = " + String.valueOf(dictSize));
    }

    private void generateNewWorteUnit()
    {
        isQuestionGenerated = true;

        WorteUnit tempWorteUnit = new WorteUnit();

        Random r = new Random(System.currentTimeMillis());

        int ackInd = r.nextInt(dictSize);

        tempWorteUnit.setUnitById(dict.get(ackInd).first, WorteTypeId.QUESTION);
        tempWorteUnit.setCorrectAnswerId(r.nextInt(WorteTypeId.ANS_4) + 1);

        for(int i = WorteTypeId.ANS_1; i <= WorteTypeId.ANS_4; i++)
        {
            if(i == tempWorteUnit.getCorrectAnswerId())
            {
                tempWorteUnit.setUnitById(dict.get(ackInd).second, i);
            }
            else
            {
                tempWorteUnit.setUnitById(dict.get(r.nextInt(dictSize)).second, i);
            }
        }

        worteUnitList.add(tempWorteUnit);

        ++currentWorteUnitId;
        currentWorteUnit = worteUnitList.get(currentWorteUnitId);
    }

    public void moveToNextQuestion()
    {
        int worteUnitSize = worteUnitList.size();

        if(currentWorteUnitId == (worteUnitSize - 1))
        {
            generateNewWorteUnit();
        }
        else if(currentWorteUnitId < (worteUnitSize - 1))
        {
            currentWorteUnit = worteUnitList.get(++currentWorteUnitId);
        }
        else
        {
            Log.e(LOG_TAG, "currentWorteUnitId cannot be more than (worteUnitSize - 1)");
        }
    }

    public void moveToPreviousQuestion()
    {
        if(currentWorteUnitId > 0)
        {
            currentWorteUnit = worteUnitList.get(--currentWorteUnitId);
        }
    }

    public String getWorte(int id)
    {
        String worte;

        if(isQuestionGenerated == false)
        {
            this.generateNewWorteUnit();
        }

        worte = currentWorteUnit.getUnitById(id);

        return worte;
    }

    public int getCorrectId()
    {
        return currentWorteUnit.getCorrectAnswerId();
    }

    public int getDictSize()
    {
        return this.dictSize;
    }
}
