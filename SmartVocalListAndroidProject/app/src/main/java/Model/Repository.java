package Model;

import android.util.Log;

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
import DataObjects.ChecklistReported;

import static android.content.ContentValues.TAG;

public class Repository {


    //public Model.ItemsLsnr<Checklist> OnDataChangeItemsLsnr;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public Repository(){

    }

        public  ArrayList<Checklist> GetChecklists(Model.ItemsLsnr itemsLsnr) {
            DatabaseReference myRef = database.getReference("/Checklists");
            Query query = myRef.orderByChild("checklistType").startAt("Template");
            return  GetChecklistsByQuery(itemsLsnr,query);
        }

        public  ArrayList<Checklist> GetChecklistsReported(Model.ItemsLsnr itemsLsnr) {
            DatabaseReference myRef = database.getReference("/Checklists");
            Query query = myRef.orderByChild("checklistType").endAt("Reported");
            return  GetChecklistsByQuery(itemsLsnr,query);
        }

        public  ArrayList GetChecklistsByQuery(Model.ItemsLsnr<BaseModelObject> itemsLsnr, Query query ) {
        ArrayList items = new ArrayList<>();

        try {

            if(query==null) {
                DatabaseReference myRef = database.getReference("/Checklists");
                query = myRef.orderByChild("checklistType");
            }

            query.addValueEventListener(new ValueEventListener() {
//            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Log.w(TAG, "onDataChange---" + snapshot.getChildrenCount() );

                    Checklist chk = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chk = new Checklist("-");
                        chk.Checklists(convertSnapshot2Json(dataSnapshot));
                        items.add(chk);
                    }
                    itemsLsnr.OnDataChangeItemsLsnr(items);

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

    public  ArrayList<ChecklistItem> GetChecklistItems(Model.ItemsLsnr<ChecklistItem> getItemsLsnr) {

        ArrayList<ChecklistItem> items = new ArrayList<>();

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
                    getItemsLsnr.OnDataChangeItemsLsnr(items);
 //
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
}
