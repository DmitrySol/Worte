package com.app.worte.worte;

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

    public int questionIdInDict;

    WorteUnit()
    {
        question = "";
        answer1 = "";
        answer2 = "";
        answer3 = "";
        answer4 = "";

        correscAnswerId = 1;
        questionIdInDict = 0;
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
    private List<WdbEntry> dict;
    private int dictSize;

    private List<Integer> dictSortedIndexes;
    private List<Integer> indexesToBeShownInCurrentCycle;

    private List<WorteUnit> worteUnitList;
    private WorteUnit currentWorteUnit;

    private int currentWorteUnitId;

    private Boolean[] isShown;

    public final static int MIN_PROGRESS = -5;
    public final static int MAX_PROGRESS = 5;

    private final static int WORTE_PER_CYCLE_DEF = 12;
    private int wortePerCycle;

    private final static int NUM_OF_ADDITIONAL_RANDOM_WORDS = 3;

    public WorteEngine(List<String> preferedDbList)
    {
        fsOperator = new FileSystemOperator();

        dict = fsOperator.getDictByDbNames(preferedDbList);
        dictSize = dict.size();

        wortePerCycle = dictSize > WORTE_PER_CYCLE_DEF ? WORTE_PER_CYCLE_DEF : dictSize;

        dictSortedIndexes = new ArrayList<Integer>(dictSize);
        indexesToBeShownInCurrentCycle = new ArrayList<Integer>();

        worteUnitList = new ArrayList<WorteUnit>();
        currentWorteUnit = new WorteUnit();
        currentWorteUnitId = -1;

        isShown = new Boolean[dictSize];
        Arrays.fill(isShown, Boolean.FALSE);

        Log.i(LOG_TAG, "Size of received dict = " + String.valueOf(dictSize));

        initSortedIndexesList();
    }

    private void initSortedIndexesList()
    {
        Boolean[] isPicked = new Boolean[dictSize];
        Arrays.fill(isPicked, Boolean.FALSE);

        for(int i = 0; i < dictSize; i++)
        {
            int minKnowlege = MAX_PROGRESS + 1;
            int indexOfMinKnowlege = -1;

            for(int j = 0; j < dictSize; j++)
            {
                if((isPicked[j] == false) && (dict.get(j).knowledge < minKnowlege))
                {
                    minKnowlege = dict.get(j).knowledge;
                    indexOfMinKnowlege = j;
                }
            }
            isPicked[indexOfMinKnowlege] = true;
            dictSortedIndexes.add(indexOfMinKnowlege);
        }
    }

    private void fillArayOfIndexesForCurrentCycle()
    {
        Log.i(LOG_TAG, "New Cycle has been started!");
        for(int i = 0; i < wortePerCycle; i++)
        {
            indexesToBeShownInCurrentCycle.add(dictSortedIndexes.get(0));
            dictSortedIndexes.remove(0);
        }

        if((wortePerCycle + NUM_OF_ADDITIONAL_RANDOM_WORDS) < dictSize)
        {
            for(int i = 0; i < NUM_OF_ADDITIONAL_RANDOM_WORDS; i++)
            {
                Random r = new Random(System.currentTimeMillis());
                int randomIndex = r.nextInt(dictSortedIndexes.size());

                indexesToBeShownInCurrentCycle.add(dictSortedIndexes.get(randomIndex));
                dictSortedIndexes.remove(randomIndex);
            }
        }
    }

    private int generateAskIndex()
    {
        if(indexesToBeShownInCurrentCycle.isEmpty())
        {
            fillArayOfIndexesForCurrentCycle();
        }

        Random r = new Random(System.currentTimeMillis());
        int randomIndex = r.nextInt(indexesToBeShownInCurrentCycle.size());
        int generatedAskIndex = indexesToBeShownInCurrentCycle.get(randomIndex);
        indexesToBeShownInCurrentCycle.remove(randomIndex);

        return generatedAskIndex;
    }

    private void updateSortedIndexesArray()
    {
        int i;
        boolean isElementShallBeInTheEnd = true;
        for(i = 0; i < dictSortedIndexes.size(); i++)
        {
            if(dict.get(currentWorteUnit.questionIdInDict).knowledge < dict.get(dictSortedIndexes.get(i)).knowledge)
            {
                dictSortedIndexes.add(i, currentWorteUnit.questionIdInDict);
                isElementShallBeInTheEnd = false;
                break;
            }
        }
        if(true == isElementShallBeInTheEnd)
        {
            dictSortedIndexes.add(currentWorteUnit.questionIdInDict);
        }
    }

    private int generateWrongAnswerId(Vector<Integer> occupiedIndexes)
    {
        int answerId = 0;

        Vector<String> occupiedAnswers = new Vector<String>();

        for(int index: occupiedIndexes)
        {
            occupiedAnswers.add(dict.get(index).translation);
        }

        Random r = new Random(System.currentTimeMillis());
        int preAnswer = r.nextInt(dictSize);

        boolean isFoundPropperIndex = false;
        for(int i = 0; i < dictSize; i++)
        {
            int rightInd = preAnswer + i;
            int leftInd = preAnswer - i;

            if((rightInd < dictSize) && (!occupiedIndexes.contains(rightInd)) &&
                    (!occupiedAnswers.contains(dict.get(rightInd).translation)))
            {
                answerId = rightInd;
                isFoundPropperIndex = true;
                break;
            }
            if((leftInd >= 0) && (!occupiedIndexes.contains(leftInd)) &&
                    (!occupiedAnswers.contains(dict.get(leftInd).translation)))
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

        tempWorteUnit.questionIdInDict = generateAskIndex();

        tempWorteUnit.setUnitById(dict.get(tempWorteUnit.questionIdInDict).original, WorteTypeId.QUESTION);
        tempWorteUnit.setCorrectAnswerId(r.nextInt(WorteTypeId.ANS_4) + 1);

        Vector<Integer> occupiedIndexes = new Vector<Integer>();
        occupiedIndexes.add(tempWorteUnit.questionIdInDict);

        for(int i = WorteTypeId.ANS_1; i <= WorteTypeId.ANS_4; i++)
        {
            if(i == tempWorteUnit.getCorrectAnswerId())
            {
                tempWorteUnit.setUnitById(dict.get(tempWorteUnit.questionIdInDict).translation, i);
            }
            else
            {
                int curWrongAnswerId = generateWrongAnswerId(occupiedIndexes);
                occupiedIndexes.add(curWrongAnswerId);
                tempWorteUnit.setUnitById(dict.get(curWrongAnswerId).translation, i);
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
            /* Taking the Worte unit from the history, when "Forward" item has been called */
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

    public void saveCurrentWdb()
    {
        Log.i(LOG_TAG, "Updating current WDBs");
        fsOperator.updateWdbFilesFromDict(dict);
    }

    public void reportCorrectAnswer()
    {
        dict.get(currentWorteUnit.questionIdInDict).knowledge += 1;

        if(dict.get(currentWorteUnit.questionIdInDict).knowledge > MAX_PROGRESS)
        {
            dict.get(currentWorteUnit.questionIdInDict).knowledge = MAX_PROGRESS;
        }
        updateSortedIndexesArray();
    }

    public void reportWrongAnswer()
    {
        dict.get(currentWorteUnit.questionIdInDict).knowledge -= 2;

        if(dict.get(currentWorteUnit.questionIdInDict).knowledge < MIN_PROGRESS)
        {
            dict.get(currentWorteUnit.questionIdInDict).knowledge = MIN_PROGRESS;
        }
        updateSortedIndexesArray();
    }

    public int getCurrentKnowledge()
    {
        return  dict.get(currentWorteUnit.questionIdInDict).knowledge;
    }

    public String getLanguageToTranslateFrom()
    {
        String languageToLearn;
        String[] parsedFileName = dict.get(currentWorteUnit.questionIdInDict).fileName.split("_");
        if(parsedFileName.length > 2)
        {
            languageToLearn = parsedFileName[0];
        }
        else
        {
            languageToLearn = "";
        }

        return languageToLearn;
    }

    public String getLanguageTranslateTo()
    {
        String nativeLanguage;
        String[] parsedFileName = dict.get(currentWorteUnit.questionIdInDict).fileName.split("_");
        if(parsedFileName.length > 2)
        {
            nativeLanguage = parsedFileName[1];
        }
        else
        {
            nativeLanguage = "";
        }

        return nativeLanguage;
    }
}
