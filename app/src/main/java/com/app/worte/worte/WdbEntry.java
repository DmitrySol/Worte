package com.app.worte.worte;

public class WdbEntry
{
    public String original;
    public String translation;
    public int knowledge;
    public String fileName;

    public WdbEntry(String original, String translation, int knowledge, String fileName)
    {
        this.original = original;
        this.translation = translation;
        this.knowledge = knowledge;
        this.fileName = fileName;
    }
}
