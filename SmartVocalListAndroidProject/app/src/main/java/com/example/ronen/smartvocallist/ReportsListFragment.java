package com.example.ronen.smartvocallist;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;
import Model.ModelChecklistsReported;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsListFragment extends Fragment {
    RecyclerView checkListsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CheckListsReportedAdapter adapter;
    ArrayList<Checklist> mData = new ArrayList<>();
    ModelChecklistsReported model=null;

    public ReportsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports_list, container, false);

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

        Collections.sort(mData, new Comparator<Checklist>() {
            @Override
            public int compare(Checklist checkList1, Checklist checkList2) {
                Double chk1LastUpdate = checkList1.getLastUpdate();
                Double chk2LastUpdate = checkList2.getLastUpdate();
                return chk2LastUpdate.compareTo(chk1LastUpdate);
            }
        });

        adapter = new CheckListsReportedAdapter(mData);
        checkListsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CheckListsReportedAdapter.OnItemClickedListener() {
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
        ReportsListFragmentDirections.ActionReportsListFragmentToReportedCheckListFragment action =
                ReportsListFragmentDirections.actionReportsListFragmentToReportedCheckListFragment();
        String textToDisplay = createReportCheckListDisplayText(checkList);
        action.setTextToDisplay(textToDisplay);
        Navigation.findNavController(getView()).navigate(action);
    }

    private String createReportCheckListDisplayText(Checklist checkList) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Report for checkList \""+ checkList.getName() + "\".\n");

        for (ChecklistItem item : checkList.getChecklistItems()) {
            textBuilder.append("\n");
            textBuilder.append("Question:\n");
            textBuilder.append(item.getName() + "\n");
            textBuilder.append("Answer: " + item.getResult() + "\n");
        }

        textBuilder.append("\n");
        textBuilder.append("Created at " + convertTime(checkList.getLastUpdate()));
        return textBuilder.toString();
    }

    private String convertTime(Double t){
        long time = Math.round(t);
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemPosition = item.getGroupId();
        Checklist reportedCheckList = mData.get(itemPosition);

        switch (item.getItemId()){
            case R.id.reportedDeleteOption:
                deleteOptionSelected(reportedCheckList);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteOptionSelected(Checklist reportedCheckList) {
        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("You are about to delete report:\n" +
                        "\"" + reportedCheckList.getName() + "\"\n" +
                        "Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Delete not working....
                        //model.deleteItem(reportedCheckList);


                        model.deleteItem(reportedCheckList);
                        mData.remove(reportedCheckList);
                        checkListsToDisplay(mData);

                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
