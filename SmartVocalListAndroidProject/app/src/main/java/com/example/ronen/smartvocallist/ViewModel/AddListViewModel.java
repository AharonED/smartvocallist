package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.Model.ModelChecklists;
import com.example.ronen.smartvocallist.Model.ModelChecklistsReported;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddListViewModel extends ViewModel {
    MutableLiveData<List<Checklist>> allChecklists;
    ModelChecklists model;

    public AddListViewModel(){
        allChecklists = new MutableLiveData<>();
        model =  ModelChecklists.getInstance();
        model.getItemsAsync(this::checkListsToDisplay);
    }

    public ModelChecklists getModel(){
        return model;
    }

    public MutableLiveData<List<Checklist>> getData() {
        return allChecklists;
    }

    public void displayLocalReportedChecklists() {
        model.getLocalChecklistAsync(ModelChecklists -> {
            checkListsToDisplay(ModelChecklists);
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

        allChecklists.setValue(checklists);
    }
}
