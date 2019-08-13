package com.example.ronen.smartvocallist.Model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ronen.smartvocallist.DataObjects.BaseModelObject;
import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Repository {


    //public Model.ItemsLsnr<Checklist> OnDataChangeItemsLsnr;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    AppLocalDbRepository localDataBase = SqlDataBase.db;

    public Repository(){
    }

        public  ArrayList<Checklist> GetChecklists(Model.ItemsLsnr itemsLsnr, Double lastUpdate) {
            DatabaseReference myRef = database.getReference("/Checklists");
            Query query = myRef
                    //.orderByChild("checklistType").startAt("Template")
                    .orderByChild("lastUpdate").startAt(lastUpdate);
            return  GetChecklistsByQuery(itemsLsnr,query,"Template");
        }

        public  ArrayList<Checklist> GetChecklistsReported(Model.ItemsLsnr itemsLsnr, Double lastUpdate) {
            DatabaseReference myRef = database.getReference("/Checklists");
            Query query = myRef
                    //.orderByChild("checklistType").endAt("Reported")
                    .orderByChild("lastUpdate").startAt(lastUpdate);
            return  GetChecklistsByQuery(itemsLsnr,query,"Reported");
        }

        public  ArrayList GetChecklistsByQuery(Model.ItemsLsnr<BaseModelObject> itemsLsnr, Query query, String checklistType ) {
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
                    items.clear();
                    Checklist chk = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chk = new Checklist("-");
                        chk.Checklists(convertSnapshot2Json(dataSnapshot));
                        if(chk.getChecklistType().equals(checklistType)) {
                            items.add(chk);
                        }
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

    public  ArrayList<ChecklistItem> GetChecklistItems(Model.ItemsLsnr<ChecklistItem> getItemsLsnr, Double lastUpdate) {
        return GetChecklistItems(getItemsLsnr,lastUpdate,null);
    }

        public  ArrayList<ChecklistItem> GetChecklistItems(Model.ItemsLsnr<ChecklistItem> getItemsLsnr, Double lastUpdate, String checklistID) {

        ArrayList<ChecklistItem> items = new ArrayList<>();

        try {

            DatabaseReference myRef = database.getReference("/ChecklistItems");
            Query query = myRef
                    //.orderByChild("lastUpdate").startAt(lastUpdate)
                    .orderByChild("checklistId").equalTo(checklistID);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //Log.w(TAG, "onDataChange---" + snapshot.getChildrenCount() );

                    items.clear();

                    ChecklistItem chk = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        chk = new ChecklistItem("-",0," "," ","", (double) 0);
                        chk.ChecklistItems(convertSnapshot2Json(dataSnapshot));
                        items.add(chk);
                    }
                    getItemsLsnr.OnDataChangeItemsLsnr(items);
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

    public void saveCheckListsInLocalDB(ArrayList<Checklist> checkLists, LocalDbUpdatedLsnr lsnr){
        AddCheckListsAsyncTask task = new AddCheckListsAsyncTask(localDataBase.checklistDao(), lsnr);
        task.execute(checkLists.toArray(new Checklist[checkLists.size()]));
    }

    public void GetCheckListsLocal(Model.ItemsLsnr<Checklist> lsnr){
        LocalGetCheckListsTask task = new LocalGetCheckListsTask(lsnr);
        task.execute();
    }

    public void GetReportedCheckListsLocal(Model.ItemsLsnr<Checklist> lsnr){
        LocalGetReportedCheckListsTask task = new LocalGetReportedCheckListsTask(lsnr);
        task.execute();
    }

    public void DeleteLocalCheckList(Model.ItemsLsnr<Checklist> lsnr,Checklist checklist){
        deleteLocalCheckListTask task = new deleteLocalCheckListTask(lsnr, checklist);
        task.execute();
    }

    private class LocalGetCheckListsTask extends AsyncTask<Void, Void, ArrayList<Checklist>> {

        Model.ItemsLsnr<Checklist> lsnr;

        public LocalGetCheckListsTask(Model.ItemsLsnr<Checklist> lsnr) {
            this.lsnr = lsnr;
        }

        @Override
        protected ArrayList<Checklist> doInBackground(Void... params) {
            List<Checklist> checkLists = localDataBase.checklistDao().getAllCheckLists();
            ArrayList<Checklist> items = new ArrayList<>(checkLists);
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<Checklist> checkLists) {
            this.lsnr.OnDataChangeItemsLsnr(checkLists);
        }
    }

    private class LocalGetReportedCheckListsTask extends AsyncTask<Void, Void, ArrayList<Checklist>> {

        Model.ItemsLsnr<Checklist> lsnr;

        public LocalGetReportedCheckListsTask(Model.ItemsLsnr<Checklist> lsnr) {
            this.lsnr = lsnr;
        }

        @Override
        protected ArrayList<Checklist> doInBackground(Void... params) {
            List<Checklist> checkLists = localDataBase.checklistDao().getAllReportedCheckLists();
            ArrayList<Checklist> items = new ArrayList<>(checkLists);
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<Checklist> checkLists) {
            this.lsnr.OnDataChangeItemsLsnr(checkLists);
        }
    }

    private class deleteLocalCheckListTask extends AsyncTask<Void, Void, Void> {
        Checklist checklist;
        Model.ItemsLsnr<Checklist> lsnr;

        deleteLocalCheckListTask( Model.ItemsLsnr<Checklist> lsnr, Checklist checklist){
            this.checklist = checklist;
            this.lsnr = lsnr;
        }

        @Override
        protected Void doInBackground(Void... params) {
            localDataBase.checklistDao().delete(checklist);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            LocalGetCheckListsTask task = new LocalGetCheckListsTask(lsnr);
            task.execute();
        }

    }

    @SuppressLint("NewApi")
    private class AddCheckListsAsyncTask extends AsyncTask<Checklist, Void, Void> {
        ChecklistDao checklistDao = null;
        LocalDbUpdatedLsnr lsnr;

        AddCheckListsAsyncTask(ChecklistDao checklistDao, LocalDbUpdatedLsnr lsnr){
            this.checklistDao = checklistDao;
            this.lsnr = lsnr;
        }


        @Override
        protected Void doInBackground(Checklist... checklists) {
            this.checklistDao.insertAll(checklists);
            return null;
        }

        @Override
        protected void onPostExecute(Void checkLists) {
            this.lsnr.OnLocalDbUpdateLsnr();
        }
    }


    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Date d = new Date();
        final StorageReference imageStorageRef = storageRef.child("image_" + d.getTime() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageStorageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri.toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
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

    public interface LocalDbUpdatedLsnr {
        void OnLocalDbUpdateLsnr();
    }
}
