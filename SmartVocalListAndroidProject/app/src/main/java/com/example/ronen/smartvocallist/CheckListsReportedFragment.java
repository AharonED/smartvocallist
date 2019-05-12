package com.example.ronen.smartvocallist;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import DataObjects.Checklist;
import DataObjects.ChecklistReported;
import Model.ModelChecklists;
import Model.ModelChecklistsReported;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckListsReportedFragment extends Fragment {
    RecyclerView checkListsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CheckListsReportedAdapter adapter;
    ArrayList<Checklist> mData = new ArrayList<>();
    //private ListView checkListsView;
    //private ArrayAdapter<String> adapter;
    ArrayList<String> checkListsDisplay;
    //private HashMap<String, ChecklistReported> checkListsHashMap;

    ModelChecklistsReported model=null;

    public CheckListsReportedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_lists_reported, container, false);

        checkListsRecyclerView = view.findViewById(R.id.checkListsRecyclerView);
        checkListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        checkListsRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        model =  ModelChecklistsReported.getInstance();
        //Get data Async.
        //When data returned from Firebase, it will rise event onDataChange
        // - which execute the injected method-checkListsToDisplay
        model.getItemsAsync(this::checkListsToDisplay);

    }


    private void checkListsToDisplay(ArrayList<Checklist> checkLists) {
        mData = checkLists;
        adapter = new CheckListsReportedAdapter(mData);
        checkListsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CheckListsReportedAdapter.OnItemClickedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(int index) {
                Checklist clickedCheckList = mData.get(index);

                if(clickedCheckList.getChecklistItems().size()>0) {
                    startCheckListPlay(clickedCheckList);
                }
                else
                {
                    Toast.makeText(getContext(),
                            "No Items to display for " + clickedCheckList.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startCheckListPlay(Checklist checkList){
        Checklist newChk = checkList.CopyChecklist();
        newChk.setChecklistType("Reported");
        newChk.setDescription("Reported at: " +LocalDateTime.now().toLocalDate().toString() );
        model.addItem(newChk);
        Intent myIntent = new Intent(getContext(), PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", newChk.getId());
        //myIntent.putExtra("model", model);
        startActivity(myIntent);
    }
}
