package com.example.ronen.smartvocallist;


import android.content.Intent;
import android.os.Bundle;

import DataObjects.Checklist;
import Model.ModelChecklists;
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

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckListsFragment extends Fragment {
    RecyclerView checkListsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    CheckListsAdapter adapter;
    ArrayList<Checklist> mData = new ArrayList<>();
    //private ListView checkListsView;
    //private ArrayAdapter<String> adapter;
    ArrayList<String> checkListsDisplay;
    //private HashMap<String, Checklist> checkListsHashMap;

    ModelChecklists model=null;

    public CheckListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_lists, container, false);

        //This Firebase init code should be moved to global place....
        try {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId("1:0123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDMIVUTpX7i0L__KDhiQb4zfzvMxmjwNec ")
                    .setDatabaseUrl("https://smartvocallist1.firebaseio.com/")
                    .setStorageBucket("smartvocallist1.appspot.com");
            FirebaseApp.initializeApp(view.getContext(), builder.build());

        }
        catch (Exception ex)
        {
            Log.d(TAG, "Value is: " + ex.getMessage());
        }

        FloatingActionButton AddCheckListButton = view.findViewById(R.id.fab);
        AddCheckListButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(v.getContext(), AddListActivity.class);
            startActivity(myIntent);
        });

        checkListsRecyclerView = view.findViewById(R.id.checkListsRecyclerView);
        checkListsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
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
                    Toast.makeText(getContext(),
                            "No Items to display for " + clickedCheckList.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCheckListPlay(Checklist checkList){
        Intent myIntent = new Intent(getContext(), PocketSphinxActivity.class);
        myIntent.putExtra("checkListId", checkList.getId());
        //myIntent.putExtra("model", model);
        startActivity(myIntent);
    }
}
