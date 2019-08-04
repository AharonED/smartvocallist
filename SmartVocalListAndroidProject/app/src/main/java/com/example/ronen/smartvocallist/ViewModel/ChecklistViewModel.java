package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.Model.ModelChecklists;

import java.util.List;

public class ChecklistViewModel extends ViewModel {

    private   LiveData<List<Checklist>>  allChecklists;

    public ChecklistViewModel(){
        allChecklists = ModelChecklists.getInstance().GetChecklist();
    }

    public LiveData<List<Checklist>> getData() {
        return allChecklists;
    }


}
