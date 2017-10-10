package com.app.worte.worte;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class WorteDefDb
{
    private List<Pair<String, String>> dict;

    public WorteDefDb()
    {
        dict = new ArrayList<Pair<String, String>>();

        dict.add(Pair.create("eins", "one"));
        dict.add(Pair.create("zwei", "two"));
        dict.add(Pair.create("drei", "three"));
        dict.add(Pair.create("vier", "four"));
        dict.add(Pair.create("fünf", "five"));
        dict.add(Pair.create("sechs", "six"));
        dict.add(Pair.create("sieben", "seven"));
        dict.add(Pair.create("acht", "eight"));
        dict.add(Pair.create("neun", "nine"));
        dict.add(Pair.create("zehn", "ten"));
        dict.add(Pair.create("elf", "eleven"));
        dict.add(Pair.create("zwölf", "twelve"));
        dict.add(Pair.create("dreizehn", "thirteen"));
        dict.add(Pair.create("vierzehn", "fourteen"));
        dict.add(Pair.create("fünfzehn", "fifteen"));
        dict.add(Pair.create("sechzehn", "sixteen"));
        dict.add(Pair.create("siebzehn", "seventeen"));
        dict.add(Pair.create("achtzehn", "eighteen"));
        dict.add(Pair.create("neunzehn", "nineteen"));
        dict.add(Pair.create("zwanzig", "twenty"));
        dict.add(Pair.create("dreißig", "thirty"));
        dict.add(Pair.create("vierzig", "forty"));
        dict.add(Pair.create("fünfzig", "fifty"));
        dict.add(Pair.create("sechzig", "sixty"));
        dict.add(Pair.create("siebzig", "seventy"));
        dict.add(Pair.create("achtzig", "eighty"));
        dict.add(Pair.create("neunzig", "ninety"));
        dict.add(Pair.create("einundzwanzig", "21"));
        dict.add(Pair.create("siebenunddreißig", "37"));
        dict.add(Pair.create("sechsundvierzig", "46"));
        dict.add(Pair.create("vierundsechzig", "64"));
        dict.add(Pair.create("neunundneunzig", "99"));
        dict.add(Pair.create("hundert", "100"));
        dict.add(Pair.create("tausend", "1.000"));
        dict.add(Pair.create("Million", "1.000.000"));
        dict.add(Pair.create("Milliarde", "1.000.000.000"));
        dict.add(Pair.create("Billion", "1.000.000.000.000"));
        dict.add(Pair.create("Billiarde", "1.000.000.000.000.000"));
    }

    public List<Pair<String, String>> getDefaultDictionary()
    {
        return dict;
    }
}
