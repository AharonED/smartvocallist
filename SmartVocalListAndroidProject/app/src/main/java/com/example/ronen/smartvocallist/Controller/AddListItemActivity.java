package com.example.ronen.smartvocallist.Controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.example.ronen.smartvocallist.DataObjects.ItemType;
import com.example.ronen.smartvocallist.R;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;


import java.util.Date;
import java.util.Set;


//import edu.cmu.sphinx.pocketsphinx.R;

public class AddListItemActivity extends AppCompatActivity {
    public static String Checklist_id;
    public static int Index;
    public static Set<String> availableWords;
    private static StringBuilder all_Props;
    public static AssetManager am;
    private static int ammount = 0;
    LinearLayout ll_items;
    private static String[] toIgnore = {"next","back","start","options","read"};

    public static void fill_dict()
    {
        availableWords = new HashSet<String>() ;
        try {
            InputStream assetDir = am.open("sync/cmudict-en-us.dict");
            BufferedReader r = new BufferedReader(new InputStreamReader(assetDir));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                String tmp = line.split(" ")[0].toLowerCase();
                if(!availableWords.contains(tmp)) {
                    availableWords.add(tmp);
                }
            }
            for (String word:toIgnore) {
                availableWords.remove(word);
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private boolean WordExistsInDict(String word)
    {
        if (availableWords == null)
        {
            availableWords = new HashSet<String>();
        }
        return availableWords.contains(word.toLowerCase());

    }
    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);
        ammount = 0;

        all_Props = new StringBuilder();
        //ll = (LinearLayout) findViewById(R.id.item);
        ll_items = (LinearLayout) findViewById(R.id.llItem);
        ll_items.setGravity(Gravity.CENTER_VERTICAL);
        ll_items.setOrientation(LinearLayout.VERTICAL);

        Intent Data = getIntent();
        Index = Data.getIntExtra("Index",0);
        Checklist_id = Data.getStringExtra("Checklist_id");

        Button AddProperty = (Button) findViewById(R.id.fab2);
        AddProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView twtypes = (TextView)findViewById(R.id.atributes);

                String seperated = twtypes.getText().toString();

                if (seperated.contains(" ")) {
                    TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                    ErrorMessage.setVisibility(View.VISIBLE);
                    StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                    ErrorMessageText.append(seperated);
                    ErrorMessageText.append(" Can only recieve single words");
                    ErrorMessage.setText(ErrorMessageText.toString());
                }
                else if (!isAlpha(seperated)) {
                    TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                    ErrorMessage.setVisibility(View.VISIBLE);
                    StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                    ErrorMessageText.append(seperated);
                    ErrorMessageText.append(" Invalid Symbols");
                    ErrorMessage.setText(ErrorMessageText.toString());
                }
                else if (!WordExistsInDict(seperated))
                {
                    TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                    ErrorMessage.setVisibility(View.VISIBLE);
                    StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                    ErrorMessageText.append(seperated);
                    ErrorMessageText.append(" Word doesn't exists in our dictionary");
                    ErrorMessage.setText(ErrorMessageText.toString());
                }
                else {
                    TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                    ErrorMessage.setVisibility(View.INVISIBLE);
                    all_Props.append(seperated);
                    all_Props.append(";");

                    Button newItem = new Button(getApplicationContext());
                    newItem.setTextSize(20);
                    newItem.setText(seperated);
                    newItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ll_items.removeView(v);
                            String tmp = all_Props.toString().replace(((Button) v).getText() + ";", "");
                            all_Props = new StringBuilder();
                            all_Props.append(tmp);
                            ammount--;
                        }
                    });
                    ll_items.addView(newItem, ammount);
                    twtypes.setText("");
                    ammount++;
                    ll_items.invalidate();
                }

            }
        });

        Button fab = findViewById(R.id.save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isViewValid = true;

                TextView twname   = (TextView)findViewById(R.id.title_tv);
                CheckBox isRequired = (CheckBox)findViewById(R.id.Required);

                String name = twname.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();

                ChecklistItem item =new ChecklistItem(java.util.UUID.randomUUID().toString(),Index,name,"", "",tmp);


                if(isViewValid) {
                    item.setIndex(Index);
                    item.setAttributes(all_Props.toString());

                    item.setItemType(ItemType.Text);
                    Intent resultIntent = new Intent();
                    item.setChecklistId(Checklist_id);
                    item.setIsReq(isRequired.isChecked() ? 1 : 0);
                    resultIntent.putExtra("Item", item);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                else
                {
                    item.options.clear();
                }

            }
        });


    }

}
