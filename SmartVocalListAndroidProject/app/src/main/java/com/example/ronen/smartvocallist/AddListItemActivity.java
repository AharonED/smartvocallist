package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import DataObjects.ChecklistItem;
import DataObjects.ItemType;

import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;
import java.io.File;


import java.util.Date;


//import edu.cmu.sphinx.pocketsphinx.R;

public class AddListItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String FILE_PATH ="C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView twname   = (TextView)findViewById(R.id.checkListName);
                TextView twdesc   = (TextView)findViewById(R.id.description);
                TextView twType  =  (TextView)findViewById(R.id.item_type);
                TextView twtypes = (TextView)findViewById(R.id.atributes);
                String atributes = twtypes.getText().toString();
                String [] seperated = atributes.split(";");

                String name = twname.getText().toString();
                String description = twdesc.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();
                int id = 0;
                try {
                    Scanner reader = new Scanner(new File(FILE_PATH));
                    id = reader.nextInt();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
                    id ++;
                    writer.write(id);
                    writer.close();
                }
                catch (FileNotFoundException ex)
                {
                    int aaa;
                    aaa =0;
                    aaa =1;
                    id = 1234;

                }
                catch (IOException ex)
                {
                    int aaa;
                    aaa =0;
                    aaa =1;
                }
                StringBuilder str
                        = new StringBuilder();

                    str.append(twType.getText().toString() + "_");
                    str.append(id);

                    ChecklistItem item =new ChecklistItem(str.toString(),id,name,description, "",tmp);

                    for (int a =0; a< seperated.length ; a++)
                    {
                    item.options.add(seperated[a]);
                }
                item.setIndex(id);


                //use item.setAttributes(atributes); instead of  above loop...
                item.setAttributes(atributes);

                    String type = twType.getText().toString();
                    if (type.toLowerCase().equals("boolean")) {
                        item.setItemType(ItemType.Boolean);
                    }
                    else if (type.toLowerCase().equals("text"))
                    {
                        item.setItemType(ItemType.Text);
                    }
                    else
                    {
                        item.setItemType(ItemType.Numeric);
                    }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("Item", item);
                setResult(0, resultIntent);
                finish();

            }
        });
    }

}
