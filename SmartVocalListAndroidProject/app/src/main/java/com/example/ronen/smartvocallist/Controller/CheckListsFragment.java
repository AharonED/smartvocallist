package com.example.ronen.smartvocallist.Controller;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.Model.ModelChecklists;
import com.example.ronen.smartvocallist.R;
import com.example.ronen.smartvocallist.ViewModel.ChecklistViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckListsFragment extends Fragment {
    ChecklistViewModel modelView;
    RecyclerView checkListsRecyclerView;

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

        modelView = ViewModelProviders.of(this).get(ChecklistViewModel.class);
        modelView.getData().observe(this, data -> displayNewData(data));

        checkListsRecyclerView = view.findViewById(R.id.checkListsRecyclerView);
        checkListsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        checkListsRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        modelView.displayLocalChecklists();
    }

    private void displayNewData(List<Checklist> data) {
        ArrayList<Checklist> dataArrayList = new ArrayList<>();
        dataArrayList.addAll(data);
        CheckListsAdapter adapter = new CheckListsAdapter(dataArrayList);
        checkListsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CheckListsAdapter.OnItemClickedListener() {
            @Override
            public void onClick(int index) {
                Checklist clickedCheckList = dataArrayList.get(index);

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

//

    private void startCheckListPlay(Checklist checkList){
        Checklist newChk = checkList.CopyChecklist();
        newChk.setChecklistType("Reported");
        Date d = new Date();
        String formatedDate = "";
        // Make a new Date object. It will be initialized to the current time.
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
        //Date d = dfm.parse("2011-09-07 00:00:00");
        formatedDate = dfm.format(d).toString();

        newChk.setLastUpdate((double)d.getTime());
        newChk.setDescription("Reported at: " + formatedDate + " By:" + modelView.GetModel().getOwnerName());
        newChk.setOwner(modelView.GetModel().getOwnerID());
        modelView.GetModel().addItem(newChk);
        Intent myIntent = new Intent(getContext(), PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", newChk.getId());
        startActivity(myIntent);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemPosition = item.getGroupId();

        Checklist checkList = modelView.getData().getValue().get(itemPosition);

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
        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("You are about to delete checklist:\n" +
                            "\"" + checkList.getName() + "\"\n" +
                            "Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        modelView.getData().getValue().remove(checkList);
                        displayNewData(modelView.getData().getValue());
                        modelView.GetModel().deleteItem(checkList);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void editOptionSelected(Checklist checkList) {
        Intent myIntent = new Intent(getActivity(),AddListActivity.class);
        myIntent.putExtra("Checklist",checkList);

        CheckListsFragment.this.startActivity(myIntent);
    }
}
