package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.Model.ModelChecklistsReported;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChecklistReportedViewModel extends ViewModel {

    MutableLiveData<List<Checklist>> allReportedChecklists;
    ModelChecklistsReported model;

    public ChecklistReportedViewModel(){
        allReportedChecklists = new MutableLiveData<>();
        model =  ModelChecklistsReported.getInstance();
        model.getItemsAsync(this::displayLocalReportedChecklists);
    }

    public ModelChecklistsReported getModel(){
        return model;
    }

    public MutableLiveData<List<Checklist>> getData() {
        return allReportedChecklists;
    }

    public void displayLocalReportedChecklists(ArrayList<Checklist> checklists) {
        model.getLocalChecklistAsync(checkListsReported -> {
            checkListsToDisplay(checkListsReported);
        });
    }

    private void checkListsToDisplay(ArrayList<Checklist> checklists) {
        Collections.sort(checklists, new Comparator<Checklist>() {
            @Override
            public int compare(Checklist checkList1, Checklist checkList2) {
                Double chk1LastUpdate = checkList1.getLastUpdate();
                Double chk2LastUpdate = checkList2.getLastUpdate();
                return chk2LastUpdate.compareTo(chk1LastUpdate);
            }
        });

        allReportedChecklists.setValue(checklists);
    }
}
