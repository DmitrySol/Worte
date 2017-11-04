package com.app.worte.worte;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

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
    private boolean isQuestionGenerated = false;

    private FileSystemOperator fsOperator;
    private List<Pair<String, String>> dict;
    private int dictSize;

    private List<WorteUnit> worteUnitList;
    private WorteUnit currentWorteUnit;

    private int currentWorteUnitId;

    private Boolean[] isShown;
    private int shownNum;

    public WorteEngine(List<String> preferedDbList)
    {
        fsOperator = new FileSystemOperator();

        dict = fsOperator.getDictByDbNames(preferedDbList);

        dictSize = dict.size();

        worteUnitList = new ArrayList<WorteUnit>();
        currentWorteUnit = new WorteUnit();
        currentWorteUnitId = -1;

        shownNum = 0;
        isShown = new Boolean[dictSize];
        Arrays.fill(isShown, Boolean.FALSE);

        Log.i(LOG_TAG, "Size of received dict = " + String.valueOf(dictSize));
    }

    private int generateAskIndex()
    {
        int askIndex = 0;

        if(shownNum == dictSize)
        {
            Arrays.fill(isShown, Boolean.FALSE);
            shownNum = 0;
        }

        Random r = new Random(System.currentTimeMillis());

        int preAskInd = r.nextInt(dictSize);

        boolean isFoundFreeSpace = false;

        for(int i = 0; i < dictSize; i++)
        {
            int rightInd = preAskInd + i;
            int leftInd = preAskInd - i;

            if((rightInd < dictSize) && (isShown[rightInd] == false))
            {
                askIndex = rightInd;
                isFoundFreeSpace = true;
                break;
            }

            if((leftInd >= 0) && (isShown[leftInd] == false))
            {
                askIndex = leftInd;
                isFoundFreeSpace = true;
                break;
            }
        }

        if(isFoundFreeSpace == false)
        {
            Log.e(LOG_TAG, "Something went wrong!");
            askIndex = preAskInd;
        }

        isShown[askIndex] = true;
        ++shownNum;

        return askIndex;
    }

    private int generateWrongAnswerId(Vector<Integer> occupiedIndexes)
    {
        int answerId = 0;

        Vector<String> occupiedAnswers = new Vector<String>();

        for(int index: occupiedIndexes)
        {
            occupiedAnswers.add(dict.get(index).second);
        }

        Random r = new Random(System.currentTimeMillis());
        int preAnswer = r.nextInt(dictSize);

        boolean isFoundPropperIndex = false;
        for(int i = 0; i < dictSize; i++)
        {
            int rightInd = preAnswer + i;
            int leftInd = preAnswer - i;

            if((rightInd < dictSize) && (!occupiedIndexes.contains(rightInd)) &&
                    (!occupiedAnswers.contains(dict.get(rightInd).second)))
            {
                answerId = rightInd;
                isFoundPropperIndex = true;
                break;
            }
            if((leftInd >= 0) && (!occupiedIndexes.contains(leftInd)) &&
                    (!occupiedAnswers.contains(dict.get(leftInd).second)))
            {
                answerId = leftInd;
                isFoundPropperIndex = true;
                break;
            }
        }

        if(isFoundPropperIndex == false)
        {
            Log.e(LOG_TAG, "Unable to find unique answer!");
            answerId = preAnswer;
        }

        return answerId;
    }

    private void generateNewWorteUnit()
    {
        isQuestionGenerated = true;

        WorteUnit tempWorteUnit = new WorteUnit();

        Random r = new Random(System.currentTimeMillis());

        int ackInd = generateAskIndex();

        tempWorteUnit.setUnitById(dict.get(ackInd).first, WorteTypeId.QUESTION);
        tempWorteUnit.setCorrectAnswerId(r.nextInt(WorteTypeId.ANS_4) + 1);

        Vector<Integer> occupiedIndexes = new Vector<Integer>();
        occupiedIndexes.add(ackInd);

        for(int i = WorteTypeId.ANS_1; i <= WorteTypeId.ANS_4; i++)
        {
            if(i == tempWorteUnit.getCorrectAnswerId())
            {
                tempWorteUnit.setUnitById(dict.get(ackInd).second, i);
            }
            else
            {
                int curWrongAnswerId = generateWrongAnswerId(occupiedIndexes);
                occupiedIndexes.add(curWrongAnswerId);
                tempWorteUnit.setUnitById(dict.get(curWrongAnswerId).second, i);
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
