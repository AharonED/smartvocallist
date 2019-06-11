package com.example.ronen.smartvocallist;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import DataObjects.ChecklistItem;
import DataObjects.ItemType;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashSet;


import java.util.Date;
import java.util.Set;


//import edu.cmu.sphinx.pocketsphinx.R;

public class AddListItemActivity extends Activity {
    public static String Checklist_id;
    public static int Index;
    public static Set<String> availableWords;
    private static StringBuilder all_Props;
    public static AssetManager am;
    private boolean is_checked =false;
    private static int ammount = 0;
    LinearLayout ll;
    CoordinatorLayout CL;

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
            availableWords.remove("next");
            availableWords.remove("back");
            availableWords.remove("start");
            availableWords.remove("options");
            availableWords.remove("read");


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


        //Detects request codes

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list_item);
        ammount = 0;

        all_Props = new StringBuilder();
        //ll = (LinearLayout) findViewById(R.id.item);
        ll = (LinearLayout) findViewById(R.id.llItem);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        ll.setOrientation(LinearLayout.VERTICAL);

        Intent Data = getIntent();
        Index = Data.getIntExtra("Index",0);
        Checklist_id = Data.getStringExtra("Checklist_id");

        String FILE_PATH ="C:\\Project\\end project\\smartvocallist\\SmartVocalListAndroidProject\\app\\src\\main\\java\\com\\example\\ronen\\smartvocallist\\checklistsCount.txt";

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
                            ll.removeView(v);
                            String tmp = all_Props.toString().replace(((Button) v).getText() + ";", "");
                            all_Props = new StringBuilder();
                            all_Props.append(tmp);
                            ammount--;
                        }
                    });
                    ll.addView(newItem, ammount);
                    twtypes.setText("");
                    ammount++;
                    ll.invalidate();
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


                    //use item.setAttributes(atributes); instead of  above loop...
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
