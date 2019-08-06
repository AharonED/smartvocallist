package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.LiveData;
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
    }

    public ModelChecklistsReported getModel(){
        return model;
    }

    public LiveData<List<Checklist>> getData() {
        return allReportedChecklists;
    }

    public void displayLocalReportedChecklists() {
        model.getLocalChecklistAsync(checkListsReported -> {
            checkListsToDisplay(checkListsReported);
        });
    }

    private void checkListsToDisplay(ArrayList<Checklist> checklists) {
        Collections.sort(checklists, new Comparator<Checklist>() {
            @Override
            public int compare(Checklist checkList1, Checklist checkList2) {
                String ckl1Name = checkList1.getName().toLowerCase();
                String ckl2Name = checkList2.getName().toLowerCase();
                return ckl1Name.compareTo(ckl2Name);
            }
        });

        allReportedChecklists.setValue(checklists);
    }
}
