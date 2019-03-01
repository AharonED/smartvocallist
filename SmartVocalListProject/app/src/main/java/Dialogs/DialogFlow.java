package Dialogs;

import java.util.ArrayList;

import DataObjects.ChecklistItems;

public class DialogFlow {
    public final ArrayList<String> items = new ArrayList<>();
    public int step=0;

    public DialogFlow()
    {

    }

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

//-------------------------------------------//
    //Navigation//

    public int start()
    {
        step=0;
        execute();
        return step;
    }

    public int next()
    {
        if(step<items.size())
        {
            step++;
            execute();
        }
        else if(step>=items.size())
        {
            step=-1;
            EOF();
        }
        return step;
    }

    public int previous()
    {
        if(step>0)
        {
            step--;
            execute();
        }
        else if(step<=0)
        {
            step=-1;
            SOF();
        }
        return step;
    }

    public int skip()
    {
        return next();
    }


    public void setCommand(String command, String value)
    {
        switch (command)
        {
            case "start":
                start();
                break;
            case "previous":
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
                set(value);
                break;
            case "read":
            case "option":
                readOptions();
                break;
            default:
                    start();

        }
    }
}
