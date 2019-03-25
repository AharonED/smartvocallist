package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

import DataObjects.Checklist;
import Model.Model;

public class CheckListsActivity extends AppCompatActivity {
    private ListView checkListsView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> checkListsDisplay;
    private HashMap<String, Checklist> checkListsHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lists);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent myIntent = new Intent(CheckListsActivity.this, Add_list_activity.class);
            startActivity(myIntent);
        });

        checkListsView = findViewById(R.id.checkListsView);
        checkListsDisplay = getCheckListsToDisplay();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, checkListsDisplay);
        checkListsView.setAdapter(adapter);
        checkListsView.setClickable(true);
        checkListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checklistName = checkListsDisplay.get(position);
                Toast.makeText(getApplicationContext(), checklistName, Toast.LENGTH_SHORT).show();
                Checklist checkList = checkListsHashMap.get(checklistName);
                startCheckListPlay(checkList);
            }
        });
    }

    private ArrayList<String> getCheckListsToDisplay(){
        ArrayList<String> checkListsToDisplay = new ArrayList<>();
        checkListsHashMap = new HashMap<>();
        Model model = new Model<>(Checklist.class);
        ArrayList<Checklist> checkLists = model.getItems();

        for (Checklist checkList : checkLists) {
            String checklistName = checkList.getName();
            checkListsToDisplay.add(checklistName);
            checkListsHashMap.put(checklistName, checkList);
        }

        return checkListsToDisplay;
    }

    private void startCheckListPlay(Checklist checkList){
        Intent myIntent = new Intent(CheckListsActivity.this, PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", checkList.getId());
        startActivity(myIntent);
    }
}
