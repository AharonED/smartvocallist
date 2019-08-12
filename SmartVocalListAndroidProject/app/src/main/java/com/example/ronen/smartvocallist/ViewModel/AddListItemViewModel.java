package com.example.ronen.smartvocallist.ViewModel;

import android.content.res.AssetManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.Model.Model;
import com.example.ronen.smartvocallist.Model.ModelChecklistItems;
import com.example.ronen.smartvocallist.Model.ModelChecklists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AddListItemViewModel  extends ViewModel {
    public String Checklist_id;
    public int Index;
    public static Set<String> availableWords;
    public StringBuilder all_Props;
    public int ammount = 0;
    MutableLiveData<List<ChecklistItem>> allChecklistItemss;
    public static String[] toIgnore = {"next","back","start","options","read"};
    ModelChecklistItems model;

    public AddListItemViewModel(){
        allChecklistItemss = new MutableLiveData<>();
        model = ModelChecklistItems.getInstance();
    }

    public ModelChecklistItems getModel(){
        return model;
    }

    public MutableLiveData<List<ChecklistItem>> getData() {
        return allChecklistItemss;
    }


    private void checkListItemsToDisplay(ArrayList<ChecklistItem> checklists) {
        Collections.sort(checklists, new Comparator<ChecklistItem>() {
            @Override
            public int compare(ChecklistItem checkList1, ChecklistItem checkList2) {
                int chk1LastUpdate = checkList1.getIndex();
                Integer  chk2LastUpdate = checkList2.getIndex();
                return chk2LastUpdate.compareTo(chk1LastUpdate);
            }
        });

        allChecklistItemss.setValue(checklists);
    }
}
