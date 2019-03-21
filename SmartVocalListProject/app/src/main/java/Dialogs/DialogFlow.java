package Dialogs;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import DataObjects.ChecklistItem;

public class DialogFlow<T extends ChecklistItem> {

    public interface FunctionalInterface<T> {
        public void execute(T item);
    }

    public interface SetInterface {
        public boolean set(String value);
    }

    public  ArrayList<T> items = new ArrayList<>();
    public int step=0;
    public String keywords;
    public String Answers;

    public DialogFlow()
    {
        keywords="set/1e-1/\n" +
                "start/1e-1/\n" +
                "previous/1e-1/\n" +
                "next/1e-1/\n" +
                "back/1e-1/\n" +
                "restart/1e-1/\n" +
                "skip/1e-1/\n" +
                "options/1e-1/\n" +
                "read/1e-1/\n";

    }

    public FunctionalInterface execute;
    public FunctionalInterface read;
    public FunctionalInterface eof;
    public FunctionalInterface sof;
    public SetInterface set;
    public FunctionalInterface readOption;
    public FunctionalInterface readItem;

/*
    public void execute()
    {

    }

    public void EOF()
    {

    }

    public void SOF()
    {

    }

    public void set(String value)
    {
    }

    public void readOptions()
    {
    }
*/

//-------------------------------------------//
    //Navigation//

    public int start()
    {
        step=0;
        execute.execute(items.get(step));
        return step;
    }

    public int next()
    {
        if(step<items.size()-1)
        {
            step++;
            execute.execute(items.get(step));
        }
        else if(step>=items.size()-1)
        {
            //step=items.size()-1;
            eof.execute(items.get(step));
        }
        return step;
    }

    public int previous()
    {
        if(step>0)
        {
            step--;
            execute.execute(items.get(step));
        }
        else if(step<=0)
        {
            step=0;
            sof.execute(items.get(step));
        }
        return step;
    }

    public int skip()
    {
        return next();
    }


    @SuppressLint("NewApi")
    public void setCommand(String command, String value)
    {
        switch (command)
        {
            case "start":
                start();
                break;
            case "previous":
            case "back":
                previous();
                break;
            case "next":
                next();
                break;
            case "restart":
                start();
                break;
            case "skip":
                skip();
                break;
            case "set":
                set.set(value);
                break;
            case "read":
                readItem.execute(items.get(step));
                break;
            case "options":
                readOption.execute(items.get(step));
                break;
            default:
                    //start();

        }
    }

    // Create keywords file
    public void createKeyWordsFile(File file) {
        StringBuilder textForFile = new StringBuilder();
        textForFile.append(keywords);

        for (ChecklistItem checklistItem : items) {
            textForFile.append(checklistItem.toKeywords());
        }

        Answers = textForFile.toString();

        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(textForFile.toString().getBytes());
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
