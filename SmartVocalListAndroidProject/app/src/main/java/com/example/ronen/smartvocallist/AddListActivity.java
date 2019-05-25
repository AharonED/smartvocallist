package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;
import Model.ModelChecklists;


public class AddListActivity extends Activity {
    private Checklist NewChecklist;
    private static String Checklist_id;
    private static int Items_count;
    private ArrayList<ChecklistItem> Items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String FilePath = "C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";
        Items_count = 0;
        Checklist_id = java.util.UUID.randomUUID().toString();

        super.onCreate(savedInstanceState);
        this.Items = new ArrayList<ChecklistItem>();
                setContentView(R.layout.activity_add_list);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView twName = (TextView)findViewById(R.id.List_Name);
                TextView twDescripton = (TextView)findViewById(R.id.description);

                String ListName = twName.getText().toString();


                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();
                Checklist temp = new Checklist(Checklist_id,ListName,twDescripton.getText().toString(),"", tmp);
                temp.setChecklistType("Template");
                temp.setOwner("10");

                for (ChecklistItem currItem:Items) {
                    currItem.setChecklistId(Checklist_id);
                }
                temp.checklistItems.addAll(Items);

                Model.ModelChecklists mod =  ModelChecklists.getInstance();
                mod.addItem(temp);
                finish();

            }
        });

        ImageButton btn = (ImageButton)findViewById(R.id.additem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddListActivity.this, AddListItemActivity.class);
                myIntent.putExtra("Checklist_id",Checklist_id);
                myIntent.putExtra("Index",Items_count);

                AddListActivity.this.startActivityForResult(myIntent,1);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {
            Items_count ++;
            ChecklistItem result = (ChecklistItem) data.getSerializableExtra("Item");
            TextView twitems = (TextView) findViewById(R.id.items);
            StringBuilder stb = new StringBuilder();
            stb.append(twitems.getText().toString());
            stb.append("\n");
            stb.append(result.getName());
            twitems.setText(stb);
            Items.add(result);
        }
    }

}
