package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;
import Model.Model;
import Model.ModelChecklists;


public class AddListActivity extends Activity {
    private Checklist NewChecklist;
    private static String Checklist_id;
    private static int Items_count;
    private ArrayList<ChecklistItem> Items;
    int GET_FROM_GALLERY = 17;
    LinearLayout ll;
    Bitmap bitmap = null;
    String URL ="";
    private boolean isUpdate = false;
    private Checklist Old;

    private void setCheckListImage(Checklist checkList) {
        ImageView mImage  = findViewById(R.id.listImage);
        ProgressBar mImageProgressBar = findViewById(R.id.image_progressBar);;


        //default image
        mImage.setImageResource(R.drawable.default_icon);

        if(checkList.getUrl() == null || checkList.getUrl().equals("")) {
            mImageProgressBar.setVisibility(View.INVISIBLE);
        }else {
            Picasso.get().setIndicatorsEnabled(true);
            Target target = new Target(){
                @Override
                public void onBitmapLoaded(Bitmap Bbitmap, Picasso.LoadedFrom from) {
                    if (mImage.getTag() == this) {
                        mImage.setImageBitmap(Bbitmap);
                        //mImageProgressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                   // mImageProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    //mImageProgressBar.setVisibility(View.VISIBLE);
                }
            };

            // Used to set a strong reference, also validity check
            mImage.setTag(target);
            RequestCreator request = Picasso.get().load(checkList.getUrl()).placeholder(R.drawable.default_icon);
            URL = checkList.getUrl();
            request.into(target);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_list);
        super.onCreate(savedInstanceState);

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

        this.Items = new ArrayList<ChecklistItem>();

        Button fab = (Button)findViewById(R.id.fab);

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

                ModelChecklists mod =  ModelChecklists.getInstance();

                if (isUpdate)
                {
                    mod.deleteItem(Old);
                }

 		if(bitmap != null){
                    mod.saveImage(bitmap, new Model.SaveImageListener() {
                        @Override
                        public void onComplete(String url) {
                            temp.setUrl(url);
                            mod.addItem(temp);
                            finish();
                        }
                    });
                }
                else{
                    temp.setUrl(URL);
                    mod.addItem(temp);
                    finish();
                }
               


            }
        });

        ImageButton btn = (ImageButton)findViewById(R.id.addnewitem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddListActivity.this, AddListItemActivity.class);
                myIntent.putExtra("Checklist_id",Checklist_id);
                myIntent.putExtra("Index",Items_count);

                AddListActivity.this.startActivityForResult(myIntent,1);

            }
        });

        Button fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }

        });

        Intent Data = getIntent();
        Checklist to_update = (Checklist)Data.getSerializableExtra("Checklist");
        if (to_update != null)
        {
            TextView twtitle = (TextView)findViewById(R.id.textView3);
            twtitle.setText("Update Checklist");
            isUpdate = true;
            Old = to_update;
            TextView twName = (TextView)findViewById(R.id.List_Name);
            TextView twDescripton = (TextView)findViewById(R.id.description);
            twName.setText(to_update.getName());
            twDescripton.setText(to_update.getDescription());
            setCheckListImage(to_update);

            for (ChecklistItem item:to_update.checklistItems) {
                Items.add(item);
                Button newItem = new Button(getApplicationContext());
                newItem.setTextSize(20);
                newItem.setText(item.getName());
                newItem.setOnClickListener(new View.OnClickListener() {
                    private ChecklistItem itm = item;
                    @Override
                    public void onClick(View v) {
                        ll.removeView(v);
                        Items_count--;
                        Items.remove(Items.indexOf(itm));
                    }
                });


                ll.addView(newItem, Items_count);
                Items_count++;
                ll.invalidate();

            }
        }

        //tmp.run();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode!=GET_FROM_GALLERY && resultCode == RESULT_OK) {
            ChecklistItem result = (ChecklistItem) data.getSerializableExtra("Item");




            Button newItem = new Button(getApplicationContext());
            newItem.setTextSize(20);
            newItem.setText(result.getName());
            newItem.setOnClickListener(new View.OnClickListener() {
                private ChecklistItem itm = result;

                @Override
                public void onClick(View v) {
                    ll.removeView(v);
                    Items_count--;
                    Items.remove(itm.getIndex());
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
