package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;

import DataObjects.ChecklistItem;
import DataObjects.ItemType;
import edu.cmu.pocketsphinx.Assets;

import android.widget.TextView;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;


import java.util.Date;
import java.util.Set;


//import edu.cmu.sphinx.pocketsphinx.R;

public class AddListItemActivity extends Activity {
    public static String Checklist_id;
    public static int Index;
    public static Set<String> availableWords;
    public static AssetManager am;
    public static void fill_dict()
    {
        availableWords = new HashSet<String>() ;
        try {
            InputStream assetDir = am.open("sync/cmudict-en-us.dict");
            //String theString = (inputStream, encoding);
            BufferedReader r = new BufferedReader(new InputStreamReader(assetDir));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                String tmp = line.split(" ")[0].toLowerCase();
                if(!availableWords.contains(tmp)) {
                    availableWords.add(tmp);
                }
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
            int a =0;
             a = 1/a;
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
    protected void onCreate(Bundle savedInstanceState) {
        fill_dict();
        Intent Data = getIntent();
        Index = Data.getIntExtra("Index",0);
        Checklist_id = Data.getStringExtra("Checklist_id");

        String FILE_PATH ="C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isViewValid = true;

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

                ChecklistItem item =new ChecklistItem(java.util.UUID.randomUUID().toString(),Index,name,description, "",tmp);

                for (int a =0; a< seperated.length ; a++)
                {
                    if (seperated[a].contains(" ")) {
                        TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                        isViewValid = false;
                        ErrorMessage.setVisibility(View.VISIBLE);
                        StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                        ErrorMessageText.append(seperated[a]);
                        ErrorMessageText.append(" Can only recieve single words");
                        ErrorMessage.setText(ErrorMessageText.toString());
                        break;
                    }
                    else if (!isAlpha(seperated[a])) {
                        TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                        isViewValid = false;
                        ErrorMessage.setVisibility(View.VISIBLE);
                        StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                        ErrorMessageText.append(seperated[a]);
                        ErrorMessageText.append(" Invalid Symbols");
                        ErrorMessage.setText(ErrorMessageText.toString());
                        break;
                    }
                    else if (!WordExistsInDict(seperated[a]))
                    {
                        TextView ErrorMessage= (TextView) findViewById(R.id.ErrorMessage);
                        isViewValid = false;
                        ErrorMessage.setVisibility(View.VISIBLE);
                        StringBuilder ErrorMessageText = new StringBuilder("Error Accured in : ");
                        ErrorMessageText.append(seperated[a]);
                        ErrorMessageText.append(" Word doesn't exists in our dictionary");
                        ErrorMessage.setText(ErrorMessageText.toString());
                        break;
                    }

                    item.options.add(seperated[a]);
            }
                if(isViewValid) {
                    item.setIndex(Index);


                    //use item.setAttributes(atributes); instead of  above loop...
                    item.setAttributes(atributes);

                    String type = twType.getText().toString();
                    if (type.toLowerCase().equals("boolean")) {
                        item.setItemType(ItemType.Boolean);
                    } else if (type.toLowerCase().equals("text")) {
                        item.setItemType(ItemType.Text);
                    } else {
                        item.setItemType(ItemType.Numeric);
                    }

                    Intent resultIntent = new Intent();
                    item.setChecklistId(Checklist_id);
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
