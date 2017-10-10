package com.app.worte.worte;

import android.support.v4.util.Pair;
import android.util.Log;

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

public class WorteEngine
{
    private final String LOG_TAG = "WorteEngine";

    private boolean isQuestionGenerated = false;
    private String question;

    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;

    private int correctAnswerId;

    private void setAnswById(int id, String answer)
    {
        switch(id)
        {
            case WorteTypeId.ANS_1:
                answer1 = answer;
                break;
            case WorteTypeId.ANS_2:
                answer2 = answer;
                break;
            case WorteTypeId.ANS_3:
                answer3 = answer;
                break;
            case WorteTypeId.ANS_4:
                answer4 = answer;
                break;
            default:
                Log.e(LOG_TAG, "Received incorrect answer ID");
                break;
        }
    }

    public void generateNextQuestion()
    {
        isQuestionGenerated = true;

        FileSystemOperator fsOperator = new FileSystemOperator();
        List<Pair<String, String>> dict = fsOperator.getWholeDictionary();

        int dictSize = dict.size();
        Log.i(LOG_TAG, "Size of received dict = " + String.valueOf(dictSize));

        Random r = new Random(System.currentTimeMillis());

        int ackInd = r.nextInt(dictSize);

        question = dict.get(ackInd).first;

        correctAnswerId = r.nextInt(WorteTypeId.ANS_4) + 1;

        for(int i = WorteTypeId.ANS_1; i <= WorteTypeId.ANS_4; i++)
        {
            if(i == correctAnswerId)
            {
                setAnswById(i, dict.get(ackInd).second);
            }
            else
            {
                setAnswById(i, dict.get(r.nextInt(dictSize)).second);
            }
        }
    }

    public String getWorte(int id)
    {
        String worte;

        if(isQuestionGenerated == false)
        {
            this.generateNextQuestion();
        }

        switch(id)
        {
            case WorteTypeId.QUESTION:
                worte = question;
                break;
            case WorteTypeId.ANS_1:
                worte = answer1;
                break;
            case WorteTypeId.ANS_2:
                worte = answer2;
                break;
            case WorteTypeId.ANS_3:
                worte = answer3;
                break;
            case WorteTypeId.ANS_4:
                worte = answer4;
                break;
            default:
                worte = "Error occurred!!!";
                break;
        }

        return worte;
    }

    public int getCorrectId()
    {
        return correctAnswerId;
    }
}
