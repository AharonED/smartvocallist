package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.Model.ModelChecklistItems;
import com.example.ronen.smartvocallist.Model.ModelChecklists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddListItemViewModel  extends ViewModel {
   /* MutableLiveData<List<ChecklistItem>> allChecklistItemss;
    ModelChecklistItems model;

    public AddListItemViewModel(){
        allChecklistItemss = new MutableLiveData<>();
        model = ModelChecklistItems.getInstance();
        model.getItemsAsync(this);
    }

    public ModelChecklistItems getModel(){
        return model;
    }

    public MutableLiveData<List<ChecklistItem>> getData() {
        return allChecklistItemss;
    }

    public void displayLocalReportedChecklistItemss() {
        model.get(ModelChecklistItems -> {
            checkListsToDisplay(ModelChecklistItems);
        });
    }

    private void checkListsToDisplay(ArrayList<ChecklistItem> checklists) {
        Collections.sort(checklists, new Comparator<ChecklistItem>() {
            @Override
            public int compare(ChecklistItem checkList1, ChecklistItem checkList2) {
                Double chk1LastUpdate = checkList1.getLastUpdate();
                Double chk2LastUpdate = checkList2.getLastUpdate();
                return chk2LastUpdate.compareTo(chk1LastUpdate);
            }
        });

        allChecklistItemss.setValue(checklists);
    }*/
}
