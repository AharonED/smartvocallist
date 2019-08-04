package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.Model.ModelChecklists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChecklistViewModel extends ViewModel {

    MutableLiveData<List<Checklist>> allChecklists;
    ModelChecklists model;

    public ChecklistViewModel(){
        //allChecklists.setValue(ModelChecklists.getInstance().GetChecklist());
        allChecklists = new MutableLiveData<>();
        model =  ModelChecklists.getInstance();
        model.getItemsAsync(this::checkListsToDisplay);
    }

    public ModelChecklists GetModel(){
        return model;
    }

    public LiveData<List<Checklist>> getData() {
        return allChecklists;
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

        allChecklists.setValue(checklists);
    }
}
