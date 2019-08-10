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
    public Checklist CurrentChecklist;

    public AddListViewModel(){
    }

    public Checklist getCurrentChecklist()
    {
        return CurrentChecklist;
    }

    public void setCurrentChecklist(Checklist newChecklist)
    {
        this.CurrentChecklist = newChecklist;
    }
}
