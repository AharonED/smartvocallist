package com.example.ronen.smartvocallist.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.Model.Model;
import com.example.ronen.smartvocallist.Model.ModelChecklists;
import com.example.ronen.smartvocallist.R;
import com.example.ronen.smartvocallist.ViewModel.AddListViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class AddListActivity extends AppCompatActivity {
    private AddListViewModel model;

    LinearLayout ll;

    private void setCheckListImage(Checklist checkList) {
        ImageView mImage  = findViewById(R.id.listImage);
        ProgressBar mImageProgressBar = findViewById(R.id.image_progressBar);

        //default image
        mImage.setImageResource(R.drawable.default_icon);

        if(checkList.getUrl() != null && !checkList.getUrl().equals("")) {
            Picasso.get().setIndicatorsEnabled(true);
            Target target = new Target(){
                @Override
                public void onBitmapLoaded(Bitmap Bbitmap, Picasso.LoadedFrom from) {
                    if (mImage.getTag() == this) {
                        mImage.setImageBitmap(Bbitmap);mImageProgressBar.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    mImageProgressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    mImageProgressBar.setVisibility(View.VISIBLE);
                }
            };

            // Used to set a strong reference, also validity check
            mImage.setTag(target);
            RequestCreator request = Picasso.get().load(checkList.getUrl()).placeholder(R.drawable.default_icon);
            model.URL = checkList.getUrl();
            request.into(target);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_list);
        super.onCreate(savedInstanceState);

        model = ViewModelProviders.of(this).get(AddListViewModel.class);



        if (!isInternetAvailable())
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Check internet connection if you are offline your acctions won't be saved");
            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        ll = (LinearLayout) findViewById(R.id.llItem);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        ll.setOrientation(LinearLayout.VERTICAL);
        AddListItemActivity.am = getApplicationContext().getAssets();
        Thread listFillerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AddListItemActivity.fill_dict();
            }
        });

        Intent Data = getIntent();
        Checklist to_update = (Checklist)Data.getSerializableExtra("Checklist");

        model.Items_count = 0;
        if (to_update == null) {
            model.Checklist_id = java.util.UUID.randomUUID().toString();
        }
        model.setCurrentChecklist(new Checklist(model.Checklist_id,"","","", 0.0));
        model.Items = new ArrayList<ChecklistItem>();

        Button fab = (Button)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView twName = (TextView)findViewById(R.id.List_Name);
                TextView twDescripton = (TextView)findViewById(R.id.description);

                String ListName = twName.getText().toString();

                ProgressBar savingProgressBar = findViewById(R.id.save_progressBar);
                savingProgressBar.setVisibility(View.VISIBLE);

                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double UpdateTime = time.doubleValue();
                model.getCurrentChecklist().setName(ListName);
                model.getCurrentChecklist().setDescription(twDescripton.getText().toString());
                model.getCurrentChecklist().setLastUpdate(UpdateTime);
                model.getCurrentChecklist().setChecklistType("Template");
                model.getCurrentChecklist().setOwner(Model.getOwnerName());

                for (ChecklistItem currItem:model.Items) {
                    currItem.setChecklistId(model.Checklist_id);
                }
                model.getCurrentChecklist().checklistItems.addAll(model.Items);

                ModelChecklists mod =  ModelChecklists.getInstance();

                if (model.isUpdate)
                {
                    if (isInternetAvailable()) {
                        mod.deleteItem(new Model.ItemsLsnr<Checklist>() {
                            @Override
                            public void OnDataChangeItemsLsnr(ArrayList<Checklist> items) {
                                AddCheckList( mod , savingProgressBar );
                            }
                        }, model.Old);
                    }

                }
                else {

                    AddCheckList(mod, savingProgressBar);
                }
            }
        });

        ImageButton btn = (ImageButton)findViewById(R.id.addnewitem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddListActivity.this, AddListItemActivity.class);
                myIntent.putExtra("Checklist_id",model.Checklist_id);

                int maxId = 0;
                for (ChecklistItem listItem:model.getCurrentChecklist().getChecklistItems())
                {
                    if (maxId < listItem.getIndex())
                    {
                        maxId = listItem.getIndex();
                    }
                }

                maxId++;
                myIntent.putExtra("Index",maxId);

                AddListActivity.this.startActivityForResult(myIntent,1);

            }
        });

        Button fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), model.GET_FROM_GALLERY);
            }

        });


        if (to_update != null)
        {
            model.setCurrentChecklist(to_update.CopyChecklist());
            model.Old = to_update;
            TextView twtitle = (TextView)findViewById(R.id.textView3);
            twtitle.setText("Update Checklist");
            model.isUpdate = true;
            TextView twName = (TextView)findViewById(R.id.List_Name);
            TextView twDescripton = (TextView)findViewById(R.id.description);
            twName.setText(model.getCurrentChecklist().getName());
            twDescripton.setText(model.getCurrentChecklist().getDescription());
            setCheckListImage(model.getCurrentChecklist());
            Checklist templist = model.getCurrentChecklist();

            for (Iterator<ChecklistItem> iterator = templist.checklistItems.iterator(); iterator.hasNext(); ) {
                ChecklistItem item= iterator.next();
                Button newItem = new Button(getApplicationContext());
                newItem.setTextSize(10);
                newItem.setText(item.getName());
                newItem.setOnClickListener(new View.OnClickListener() {
                    private ChecklistItem itm = item;
                    @Override
                    public void onClick(View v) {
                        ll.removeView(v);
                        model.Items_count--;
                        Checklist tmp = model.getCurrentChecklist();
                        tmp.checklistItems.remove(tmp.checklistItems.indexOf(itm));
                        model.setCurrentChecklist(tmp);
                    }
                });


                ll.addView(newItem, model.Items_count);
                model.Items_count++;
                ll.invalidate();

            }
        }

        listFillerThread.run();
    }

    private void AddCheckList(ModelChecklists mod,  ProgressBar savingProgressBar  ) {
        if(model.bitmap != null){
            mod.saveImage(model.bitmap, new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    model.getCurrentChecklist().setUrl(url);
                    if (isInternetAvailable()) {
                        mod.addItem(model.getCurrentChecklist());
                    }
                    savingProgressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
            });
        }
        else{
            model.getCurrentChecklist().setUrl(model.URL);
            if (isInternetAvailable()) {
                mod.addItem(model.getCurrentChecklist());
            }
            savingProgressBar.setVisibility(View.INVISIBLE);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != model.GET_FROM_GALLERY && resultCode == RESULT_OK) {
            ChecklistItem result = (ChecklistItem) data.getSerializableExtra("Item");


            Button newItem = new Button(getApplicationContext());
            newItem.setTextSize(10);
            newItem.setText(result.getName());
            newItem.setOnClickListener(new View.OnClickListener() {
                private ChecklistItem itm = result;


                @Override
                public void onClick(View v) {
                    ll.removeView(v);
                    model.Items_count--;
                    model.getCurrentChecklist().checklistItems.remove(itm.getIndex());
                }
            });


            ll.addView(newItem, model.Items_count);
            model.Items_count++;
            ll.invalidate();

            //TextView twitems = (TextView) findViewById(R.id.items);
            //StringBuilder stb = new StringBuilder();
            //stb.append(twitems.getText().toString());
            //stb.append("\n");
            //twitems.setText(stb);
            result.setChecklistId(model.getCurrentChecklist().getId());
            result.setOwner(Model.getOwnerName());
            model.getCurrentChecklist().checklistItems.add(result);
        } else if (requestCode == model.GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            model.bitmap = null;
            try {
                model.bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ImageView iv = (ImageView) findViewById(R.id.listImage);
            iv.setImageBitmap(model.bitmap);

        }
    }

    private boolean isInternetAvailable() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.ThreadPolicy safePolicy = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(policy);

            InetAddress ipAddr = InetAddress.getByName("google.com");

            StrictMode.setThreadPolicy(safePolicy);
            //You can replace it with your name
            return !ipAddr.equals("");


        } catch (Exception e) {
            return false;
        }
    }
}
