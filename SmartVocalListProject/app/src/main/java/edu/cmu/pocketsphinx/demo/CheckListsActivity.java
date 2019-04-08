package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import DataObjects.Checklist;
import Model.Model;

import static android.content.ContentValues.TAG;

public class CheckListsActivity extends AppCompatActivity {
    RecyclerView checkListsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CheckListsAdapter adapter;
    ArrayList<Checklist> mData = new ArrayList<>();
    //private ListView checkListsView;
    //private ArrayAdapter<String> adapter;
    ArrayList<String> checkListsDisplay;
    //private HashMap<String, Checklist> checkListsHashMap;

    Model model=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//This Firebase init code should be moved to global place....
        try {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId("1:0123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDMIVUTpX7i0L__KDhiQb4zfzvMxmjwNec ")
                    .setDatabaseUrl("https://smartvocallist1.firebaseio.com/")
                    .setStorageBucket("smartvocallist1.appspot.com");
            FirebaseApp.initializeApp(this, builder.build());

        }
        catch (Exception ex)
        {
            Log.d(TAG, "Value is: " + ex.getMessage());
        }


        setContentView(R.layout.activity_check_lists);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent myIntent = new Intent(CheckListsActivity.this, Add_list_activity.class);
            startActivity(myIntent);
        });

        checkListsRecyclerView = findViewById(R.id.checkListsRecyclerView);
        checkListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        checkListsRecyclerView.setLayoutManager(layoutManager);

//        checkListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String checklistName = checkListsDisplay.get(position);
//                Toast.makeText(getApplicationContext(), checklistName, Toast.LENGTH_SHORT).show();
//                Checklist checkList = checkListsHashMap.get(checklistName);
//                if(checkList.getChecklistItems().size()>0) {
//                    startCheckListPlay(checkList);
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), "No Items to display for " + checklistName, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();

       model = new Model<>(Checklist.class);
        //Get data Async.
        //When data returned from Firebase, it will rise event onDataChange
        // - which execute the injected method-checkListsToDisplay
        model.getItemsAsync(this::checkListsToDisplay);

//        if (checkListsDisplay != null) {
//             adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, checkListsDisplay);
//             checkListsView.setAdapter(adapter);
//        }
    }

    private void checkListsToDisplay(ArrayList<Checklist> checkLists) {
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
                    Toast.makeText(getApplicationContext(),
                            "No Items to display for " + clickedCheckList.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCheckListPlay(Checklist checkList){
        Intent myIntent = new Intent(CheckListsActivity.this, PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", checkList.getId());
        //myIntent.putex
        startActivity(myIntent);
    }
}
