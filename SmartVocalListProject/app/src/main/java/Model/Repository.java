package Model;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DataObjects.BaseModelObject;
import DataObjects.Checklist;
import DataObjects.ChecklistItem;

import static android.content.ContentValues.TAG;

public class Repository extends AppCompatActivity {


    //public Model.ItemsLsnr<Checklist> getItemsLsnr;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public Repository(){
        /*
        try {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId("1:0123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDMIVUTpX7i0L__KDhiQb4zfzvMxmjwNec ")
                    .setDatabaseUrl("https://smartvocallist1.firebaseio.com/")
                    .setStorageBucket("smartvocallist1.appspot.com");
            FirebaseApp.initializeApp(this, builder.build());

        }
        catch (Exception ex)
        {
            Log.d(TAG, "Value is: " + ex.getMessage());

        }
*/
    }

    public  ArrayList<Checklist> GetChecklists(Model.ItemsLsnr<Checklist> itemsLsnr) {

        ArrayList<Checklist> items = new ArrayList<>();
        items.add(CreateTempCheckList("Ch1"));
        items.add(CreateTempCheckList("Ch2"));


        try {
            DatabaseReference myRef = database.getReference("/Checklists");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Log.w(TAG, "onDataChange---" + snapshot.getChildrenCount() );

                    Checklist chk = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chk = new Checklist("-");
                        chk.Checklists(convertSnapshot2Json(dataSnapshot));
                        items.add(chk);
                    }
                    itemsLsnr.getItemsLsnr(items);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }

            });

        } catch (Exception ex) {
            Log.d(TAG, "Value is: " + ex.getMessage());

        }

        return items;
    }

    public  ArrayList<BaseModelObject> GetChecklistItems(Model.ItemsLsnr<BaseModelObject> getItemsLsnr) {

        ArrayList<BaseModelObject> items = new ArrayList<>();

        try {
            DatabaseReference myRef = database.getReference("/ChecklistItems");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Log.w(TAG, "onDataChange---" + snapshot.getChildrenCount() );

                    ChecklistItem chk = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chk = new ChecklistItem("-",0," "," ","", (double) 0);
                        chk.ChecklistItems(convertSnapshot2Json(dataSnapshot));
                        items.add(chk);
                    }
                    getItemsLsnr.getItemsLsnr(items);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }

            });

        } catch (Exception ex) {
            Log.d(TAG, "Value is: " + ex.getMessage());

        }

        return items;
    }

    private static JSONObject convertSnapshot2Json(DataSnapshot dataSnapshot ){


        JSONObject json;
        json = new JSONObject();


        for (DataSnapshot child : dataSnapshot.getChildren()) {
            if (child.getKey() != null && child.getValue() != null) {
                Object value =  child.getValue();
                String key = (String) child.getKey();
                Log.w(TAG, key + "-" + value.toString());

                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.w(TAG, e.getMessage());

                }
            }

        }

        return json;
    }

    public static Checklist CreateTempCheckList(String ID)
    {
        Checklist chk = new Checklist("options_" + ID,"Checklist #" + ID,"You should perform this checklist","",null);
        ChecklistItem item =new ChecklistItem("item_1",1,"Say \"easy\", \"medium\" or \"hard\"","", "",null);
        item.getOptions().add("easy");
        item.getOptions().add("medium");
        item.getOptions().add("hard");
        chk.getChecklistItems().add(item);

        item =new ChecklistItem("boolean",2,"Do you wear pants? (True or False)","", "",null);
        item.getOptions().add("true");
        item.getOptions().add("false");
        chk.getChecklistItems().add(item);

        item =new ChecklistItem("digit",3,"What is your age?","", "",null);
        item.getOptions().add("one");
        item.getOptions().add("two");
        item.getOptions().add("three");
        item.getOptions().add("four");
        chk.getChecklistItems().add(item);

        return chk;
    }
}
