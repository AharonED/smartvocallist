package com.example.ronen.smartvocallist;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import DataObjects.Checklist;
import Model.ModelChecklists;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckListsFragment extends Fragment {
    RecyclerView checkListsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CheckListsAdapter adapter;
    ArrayList<Checklist> mData = new ArrayList<>();
    ModelChecklists model=null;

    public CheckListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_lists, container, false);

        FloatingActionButton AddCheckListButton = view.findViewById(R.id.fab);
        AddCheckListButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), AddListActivity.class);
            startActivity(myIntent);
        });

        checkListsRecyclerView = view.findViewById(R.id.checkListsRecyclerView);
        checkListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        checkListsRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        model =  ModelChecklists.getInstance();
        //Get data Async.
        //When data returned from Firebase, it will rise event onDataChange
        // - which execute the injected method-checkListsToDisplay
        model.getItemsAsync(this::checkListsToDisplay);
    }


    private void checkListsToDisplay(ArrayList<Checklist> checkLists) {
        Collections.sort(checkLists, new Comparator<Checklist>() {
            @Override
            public int compare(Checklist checkList1, Checklist checkList2) {
                return checkList1.getName().compareTo(checkList2.getName());
            }
        });

        mData = checkLists;
        adapter = new CheckListsAdapter(mData);
        checkListsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CheckListsAdapter.OnItemClickedListener() {
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

    private void startCheckListPlay(Checklist checkList){
        Checklist newChk = checkList.CopyChecklist();
        newChk.setChecklistType("Reported");
        Date d = new Date();
        newChk.setLastUpdate((double)d.getTime());
        newChk.setDescription("Reported at: " + d.toString());
        model.addItem(newChk);
        Intent myIntent = new Intent(getContext(), PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", newChk.getId());
        startActivity(myIntent);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemPosition = item.getGroupId();
        Checklist checkList = mData.get(itemPosition);

        switch (item.getItemId()){
            case R.id.deleteOption:
                deleteOptionSelected(checkList);
                return true;
            case R.id.editOption:
                editOptionSelected(checkList);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteOptionSelected(Checklist checkList) {
        // TODO: delete code for firebase
    }

    private void editOptionSelected(Checklist checkList) {
        // TODO
    }
}
