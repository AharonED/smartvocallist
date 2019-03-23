package edu.cmu.pocketsphinx.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import DataObjects.Checklist;
import DataObjects.ChecklistItem;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Scanner;
import java.io.File;


import java.util.Date;


//import edu.cmu.sphinx.pocketsphinx.R;

public class addListItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView twname   = (TextView)findViewById(R.id.name);
                TextView twdesc   = (TextView)findViewById(R.id.description);
                TextView twType  =  (TextView)findViewById(R.id.item_type);
                String name = twname.getText().toString();
                String description = twdesc.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();
                int id = 0;
                try {
                    Scanner reader = new Scanner(new File("edu/cmu/pocketsphinx/demo/listItemCount.txt"));
                    id = reader.nextInt();
                }
                catch (FileNotFoundException ex)
                {

                }
                StringBuilder str
                        = new StringBuilder();

                str.append(twType.getText().toString() + "_");
                str.append(id);

                ChecklistItem item =new ChecklistItem(str.toString(),id,name,description, "",tmp);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Item", item.toJson().toString());
                setResult(0, resultIntent);
                finish();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
