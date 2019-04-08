package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


public class AddListActivity extends AppCompatActivity {
    private Checklist NewChecklist;
    private ArrayList<ChecklistItem> Items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


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
                    File read_write = new File("edu/cmu/pocketsphinx/demo/checklistsCount.txt");

                    Scanner reader = new Scanner(read_write);
                    id = reader.nextInt();
                    reader.close();
                    BufferedWriter writer = new BufferedWriter(new FileWriter("edu/cmu/pocketsphinx/demo/checklistsCount.txt"));
                    id ++;
                    writer.write(id);
                    writer.close();
                }
                catch (FileNotFoundException ex)
                {

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
                temp.checklistItems.addAll(Items);
                Model.ModelChecklists mod =  ModelChecklists.getInstance();
                mod.addItem(temp);
            }
        });

        ImageButton btn = (ImageButton)findViewById(R.id.additem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddListActivity.this, AddListItemActivity.class);

                AddListActivity.this.startActivity(myIntent);
                String result = myIntent.getStringArrayExtra("Item")[0];

                try {
                    JSONObject tmp = new JSONObject(result);
                    ChecklistItem itm = new ChecklistItem("",0,"","","",0.0);
                    itm.ChecklistItems(tmp);
                    TextView twitems = (TextView)findViewById(R.id.items);
                    StringBuilder stb = new StringBuilder();
                    stb.append(twitems.getText().toString());
                    stb.append("\n");
                    stb.append(itm.getName());
                    twitems.setText(stb);
                    Items.add(itm);
                }
                catch (JSONException ex)
                {

                }
            }
        });
    }

}
