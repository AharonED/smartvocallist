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

    public  ArrayList<Checklist> GetChecklists(Model.ItemsLsnr<Checklist> getItemsLsnr) {

        ArrayList<Checklist> items = new ArrayList<>();
        items.add(getChecklistByID("Ch1"));
        items.add(getChecklistByID("Ch2"));


        try {
    // Write a message to the database
    //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("/Checklists");
   // myRef.setValue("Hello, World!");


            Log.w(TAG, "GetChecklists-"  );

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            Log.w(TAG, "onDataChange---" + snapshot.getChildrenCount() );


            int i = 2;
            for (DataSnapshot dataSnapshot : snapshot.getChildren())
            {
                Checklist chk = convertSnapshot2Json(dataSnapshot);
                if(chk!=null)
                items.add(chk);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey()!=null && child.getValue() != null) {
                        String value = (String) child.getValue().toString();
                        String key = (String) child.getKey();
                       // Log.w(TAG, key + "-" + value);
                    }
                }

                i++;

            }
            //items.add(getChecklistByID("Ch3"));

            getItemsLsnr.getItemsLsnr(items);

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }

        });

}
catch (Exception ex)
{
    Log.d(TAG, "Value is: " + ex.getMessage());

}

        return  items;
    }


    private static Checklist convertSnapshot2Json(DataSnapshot dataSnapshot ){

        Checklist chk =null;

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
                chk = new Checklist("-");
                chk.Checklists(json);
            }

        }

        return chk;
    }

    public static Checklist getChecklistByID(String ID)
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
