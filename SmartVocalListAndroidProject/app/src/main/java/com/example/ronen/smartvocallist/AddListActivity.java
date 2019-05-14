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
    private ArrayList<ChecklistItem> Items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String FilePath = "C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";


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
                int id = 0;
                try {
                    File read_write = new File(FilePath);

                    Scanner reader = new Scanner(read_write);
                    id = reader.nextInt();
                    reader.close();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(FilePath));
                    id ++;
                    writer.write(id);
                    writer.close();
                }
                catch (FileNotFoundException ex)
                {
                    id = 3344;
                }
                catch (IOException ex)
                {

                }
                StringBuilder str
                        = new StringBuilder();

                str.append("_");
                str.append(id);

                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();
                Checklist temp = new Checklist(str.toString(),ListName,twDescripton.getText().toString(),"", tmp);
                temp.setChecklistType("Template");
                temp.setOwner("10");

                for (ChecklistItem currItem:Items) {
                    currItem.setChecklistId(str.toString());
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

                AddListActivity.this.startActivityForResult(myIntent,1);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        ChecklistItem result = (ChecklistItem)data.getSerializableExtra("Item");

        TextView twitems = (TextView)findViewById(R.id.items);
        StringBuilder stb = new StringBuilder();
        stb.append(twitems.getText().toString());
        stb.append("\n");
        stb.append(result.getName());
        twitems.setText(stb);
        Items.add(result);
    }

}
