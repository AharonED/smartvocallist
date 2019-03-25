package Model;

import java.util.ArrayList;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;

public class Repository {

    public static ArrayList<Checklist> GetChecklists() {
        ArrayList<Checklist> items = new ArrayList<>();
        items.add(getChecklistByID("Ch1"));
        items.add(getChecklistByID("Ch2"));
        return  items;
    }

    public static Checklist getChecklistByID(String ID)
    {
        Checklist chk = new Checklist("options_" + ID,"Checklist #" + ID,"You should perform this checklist","",null);
        ChecklistItem item =new ChecklistItem("item_1",1,"Say \"easy\", \"medium\" or \"hard\"","", "",null);
        item.getOptions().add("easy");
        item.getOptions().add("medium");
        item.getOptions().add("hard");
        chk.getChecklistItems().add(item);

        item =new ChecklistItem("boolean",2,"Do you wear pants? (True or False)","", "",null);
        item.getOptions().add("true");
        item.getOptions().add("false");
        chk.getChecklistItems().add(item);

        item =new ChecklistItem("digit",3,"What is your age?","", "",null);
        item.getOptions().add("one");
        item.getOptions().add("two");
        item.getOptions().add("three");
        item.getOptions().add("four");
        chk.getChecklistItems().add(item);

        return chk;
    }
}