package Model;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import DataObjects.BaseModelObject;

public  abstract class Model <T extends BaseModelObject> implements IModel, Serializable {

    private String tableName="";
    protected Repository rep = new Repository();

    private static String ownerID;
    private static String ownerName;

    public static String getOwnerID() {
        return ownerID;
    }

    public static void setOwnerID(String ownerID) {
        Model.ownerID = ownerID;
    }

    public static String getOwnerName() {
        return ownerName;
    }

    public static void setOwnerName(String ownerName) {
        Model.ownerName = ownerName;
    }


    public Model.ItemsLsnr<T> getItemsLsnr() {
        return ItemsLsnr;
    }

    public void deleteItem(T chk){
        DatabaseReference myRef = rep.database.getReference("/" + getTableName());
        try {
            String key = chk.id;
            myRef.child(key).removeValue();
        }
        catch (Exception ex)
        {
            Log.println(1,"t","Data could not be deleted: " + ex.getMessage());
        }
    }

    public interface ItemsLsnr<E extends BaseModelObject> {
        void OnDataChangeItemsLsnr(ArrayList<E> items);
    }

    private ItemsLsnr<T> ItemsLsnr;

    public void setItemsLsnr(Model.ItemsLsnr<T> itemsLsnr) {
        ItemsLsnr = itemsLsnr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    //public static Model model;
    public  ArrayList<T> items = new ArrayList<>();


    public Model()
    {

    }

    public void addItem(T chk)
    {
        if(chk.id=="-1") {
            chk.id = java.util.UUID.randomUUID().toString();
        }

        //HashMap<String, Object> timestampCreated = new HashMap<>();
        //timestampCreated.put("timestamp", ServerValue.TIMESTAMP);

        //////chk.lastUpdate =  (double)timestampCreated.get("timestamp");

        DatabaseReference myRef = rep.database.getReference("/" + getTableName());

        //myRef.child(chk.getId()).setValue(chk.toJson());

try {
        //String key = myRef.push().push().getKey();
        String key = chk.id;

        //Convert checklist to Hashmap for sending to Firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, chk.toMap());

        myRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               if(databaseError!=null)
                Log.println(1,"t","Data could not be saved: " + databaseError.getMessage());

            }
        });


}
    catch (Exception ex)
    {
        Log.println(1,"t","Data could not be saved: " + ex.getMessage());

    }
        /*
        DatabaseReference myRef = rep.database.getReference("/" + getTableName());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).getValue() == null) {
                    myRef.child(user.getUid()).setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                                                 //fairebase.addChild( chk.tableName  , chk);

                            myRef.child(collectionName).

                                                 child(instance.id).

                                                 setValue(instance.toJson())

                                                 {
                                                     (error:Error ?, ref:DatabaseReference)in
                                                     if let error = error {
                                                     print("Data could not be saved: \(error).")
                                                 } else{
                                                     print("Data saved successfully!")
                                                 }
                                                 }
                                             }
        );
        */

        items.add(chk);

    }


    public void getItemsAsync(Model.ItemsLsnr<T> lsnr) {
       // Repository rep = new Repository();
        ////items = (ArrayList<T>) rep.GetChecklists(lsnr);

    }

    @TargetApi(Build.VERSION_CODES.N)
    public T getItemByID(String id)
    {
        //return items.stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
        Iterator<T> iterator = items.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
        if (item.getId().equals(id)) {
            return item;
            }
        }
        return null;
    }

    public interface SaveImageListener{
        void onComplete(String url);
    }
    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        rep.saveImage(imageBitmap, listener);
    }

}

