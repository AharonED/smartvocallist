package com.example.ronen.smartvocallist.Controller;


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

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.R;
import com.example.ronen.smartvocallist.ViewModel.ChecklistReportedViewModel;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsListFragment extends Fragment {
    ChecklistReportedViewModel modelView = ChecklistReportedViewModel.getInstance();

    RecyclerView checkListsRecyclerView;

    public ReportsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports_list, container, false);

       // modelView = ViewModelProviders.of(this).get(ChecklistReportedViewModel.class);
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
        modelView.displayLocalReportedChecklists();
    }

    private void displayNewData(List<Checklist> data) {
        ArrayList<Checklist> dataArrayList = new ArrayList<>();
        dataArrayList.addAll(data);
        CheckListsReportedAdapter adapter = new CheckListsReportedAdapter(dataArrayList);
        checkListsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickedListener(new CheckListsReportedAdapter.OnItemClickedListener() {
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

    private void startCheckListPlay(Checklist checkList){
        ReportsListFragmentDirections.ActionReportsListFragmentToReportedCheckListFragment action =
                ReportsListFragmentDirections.actionReportsListFragmentToReportedCheckListFragment();
        String textToDisplay = createReportCheckListDisplayText(checkList);
        action.setTextToDisplay(textToDisplay);
        Navigation.findNavController(getView()).navigate(action);
    }

    private String createReportCheckListDisplayText(Checklist checkList) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(checkList.getName() + "\n");

        for (ChecklistItem item : checkList.getChecklistItems()) {
            textBuilder.append("\n");
            textBuilder.append("Question:\n");
            textBuilder.append(item.getName() + "\n");

            if(item.getResult() == null || item.getResult().equals(""))
                textBuilder.append("Answer: *No Answer*\n");
            else
                textBuilder.append("Answer: " + item.getResult() + "\n");
        }

        textBuilder.append("\n");
        textBuilder.append(checkList.getDescription().replace(" By", "\nBy"));
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
        Checklist reportedCheckList = modelView.getData().getValue().get(itemPosition);

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
                        modelView.getData().getValue().remove(reportedCheckList);
                        displayNewData(modelView.getData().getValue());
                        modelView.getModel().deleteItem(reportedCheckList);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
