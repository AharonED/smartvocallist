package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    int GET_FROM_GALLERY = 17;
    LinearLayout ll;
    Bitmap bitmap = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_list);
        ll = (LinearLayout) findViewById(R.id.llItem);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        AddListItemActivity.am = getApplicationContext().getAssets();
        Thread tmp = new Thread(new Runnable() {
            @Override
            public void run() {
                AddListItemActivity.fill_dict();
            }
        });



        String FilePath = "C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";
        Items_count = 0;
        Checklist_id = java.util.UUID.randomUUID().toString();

        super.onCreate(savedInstanceState);
        this.Items = new ArrayList<ChecklistItem>();

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

        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }

        });
        tmp.run();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode!=GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Items_count ++;
            ChecklistItem result = (ChecklistItem) data.getSerializableExtra("Item");




            Button newItem = new Button(getApplicationContext());
            newItem.setTextSize(20);
            newItem.setText(result.getName());
            newItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll.removeView(v);
                    Items_count--;
                    Items.remove(result.getIndex());
                }
            });


            ll.addView(newItem, Items_count);
            Items_count++;
            ll.invalidate();




            //TextView twitems = (TextView) findViewById(R.id.items);
            //StringBuilder stb = new StringBuilder();
            //stb.append(twitems.getText().toString());
            //stb.append("\n");
            //twitems.setText(stb);
            Items.add(result);
        }
        else  if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ImageView iv = (ImageView) findViewById(R.id.listImage);
            iv.setImageBitmap(bitmap);

        }
    }

}
