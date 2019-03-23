package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;


public class Add_list_activity extends AppCompatActivity {
    private Checklist NewChecklist;
    private ArrayList<ChecklistItem> Items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        this.Items = new ArrayList<ChecklistItem>();
                setContentView(R.layout.activity_add_list_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageButton btn = (ImageButton)findViewById(R.id.additem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Add_list_activity.this, addListItem.class);
                Add_list_activity.this.startActivity(myIntent);
                String result = myIntent.getStringArrayExtra("Item")[0];
                //JsonReader s = new JsonReader("Asd",);

                //JSONParser parser = new JSONParser(); JSONObject json = (JSONObject) parser.parse(stringToParse);
                //Read more: http://www.java67.com/2016/10/3-ways-to-convert-string-to-json-object-in-java.html#ixzz5j1f9bq5E
                try {
                    JSONObject tmp = new JSONObject(result);
                    ChecklistItem itm = new ChecklistItem("",0,"","","",0.0);
                    itm.ChecklistItems(tmp);
                    Items.add(itm);
                }
                catch (JSONException ex)
                {

                }
            }
        });
    }

}
