package com.example.ronen.smartvocallist.Model;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ronen.smartvocallist.DataObjects.BaseModelObject;
import com.example.ronen.smartvocallist.DataObjects.TablesLastSync;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public void deleteItem(ItemsLsnr<T> lsnr, T chk){
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

    public interface SyncLsnr<E extends BaseModelObject> {
        void OnSync( Double lastUpdate);
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

    public Double lastUpdate;
    public Double getLastUpdate() {
        if(lastUpdate==null) {
            lastUpdate = 0.0;
        }
        return lastUpdate;
    }
    public void setLastUpdate(Double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public Model()
    {
        /*
        AppLocalDbRepository localDataBase = SqlDataBase.db;

        //List<TablesLastSync> tbls = localDataBase.tablesLastSyncDao().getAll();

        LocalGetTablesLastSync local = new LocalGetTablesLastSync(new ItemsLsnr<TablesLastSync>() {
            @Override
            public void OnDataChangeItemsLsnr(ArrayList<TablesLastSync> items) {
                ArrayList<TablesLastSync> tbls = items;

                Double tmpLastUpdated=0.0;

                for(int i=0; i<tbls.size();i++)
                {
                    if(tbls.get(i).tableName == tableName) {
                        tmpLastUpdated= tbls.get(i).getLastUpdate();
                    }
                }
                if(tmpLastUpdated==0.0) {

                    TablesLastSync tbl = new TablesLastSync(java.util.UUID.randomUUID().toString());
                    tbl.setTableName(tableName);

                    Date currentTime = Calendar.getInstance().getTime();
                    Long time = currentTime.getTime();
                    Double tmp = time.doubleValue();
                    tbl.setLastUpdate(tmp);

                    //localDataBase.tablesLastSyncDao().insertAll(tbl);
                    tbls.add(tbl);
                    AddTablesLastSyncsAsyncTask task = new AddTablesLastSyncsAsyncTask(localDataBase.tablesLastSyncDao());
                    task.execute(tbls.toArray(new TablesLastSync[tbls.size()]));
                }


            }
        });
        local.execute();
        */
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

try {
        //String key = myRef.push().push().getKey();
        String key = chk.id;

        //Convert TablesLastSync to Hashmap for sending to Firebase
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

        items.add(chk);

    }


    public void getItemsAsync(Model.ItemsLsnr<T> lsnr) {
        //Abstract method
    }
    public void getItemsAsyncByLastUpdate(Model.SyncLsnr<T> lsnrSync) {
        //Abstract method
        AppLocalDbRepository localDataBase = SqlDataBase.db;

        //List<TablesLastSync> tbls = localDataBase.tablesLastSyncDao().getAll();

        LocalGetTablesLastSync local = new LocalGetTablesLastSync(new ItemsLsnr<TablesLastSync>() {
            @Override
            public void OnDataChangeItemsLsnr(ArrayList<TablesLastSync> items) {
                ArrayList<TablesLastSync> tbls = items;

                Double tmpLastUpdated=0.0;

                String tableID=java.util.UUID.randomUUID().toString();
                for(int i=0; i<tbls.size();i++)
                {
                    if(tbls.get(0).tableName.equals(tableName)) {
                        tmpLastUpdated= tbls.get(i).getLastUpdate();
                        tableID = tbls.get(i).id;
                    }
                }

                TablesLastSync tbl = new TablesLastSync(tableID);
                tbl.setTableName(tableName);
                lsnrSync.OnSync(tmpLastUpdated);


                Date currentTime = Calendar.getInstance().getTime();
                Long time = currentTime.getTime();
                Double tmp = time.doubleValue();
                tbl.setLastUpdate(tmp);

                tbls.add(tbl);

                AddTablesLastSyncsAsyncTask task = new AddTablesLastSyncsAsyncTask(localDataBase.tablesLastSyncDao());
                task.execute(tbls.toArray(new TablesLastSync[tbls.size()]));
            }
        });
        local.execute();

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


    public class LocalGetTablesLastSync extends AsyncTask<Void, Void, ArrayList<TablesLastSync>> {

        Model.ItemsLsnr<TablesLastSync> lsnr;
        AppLocalDbRepository localDataBase = SqlDataBase.db;

        public LocalGetTablesLastSync(Model.ItemsLsnr<TablesLastSync> lsnr) {
            this.lsnr = lsnr;
        }

        @Override
        protected ArrayList<TablesLastSync> doInBackground(Void... params) {
            List<TablesLastSync> tbls = localDataBase.tablesLastSyncDao().getAll();
            ArrayList<TablesLastSync> items = new ArrayList<>(tbls);
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<TablesLastSync> tbls) {
            this.lsnr.OnDataChangeItemsLsnr(tbls);
        }
    }

    @SuppressLint("NewApi")
    public class AddTablesLastSyncsAsyncTask extends AsyncTask<TablesLastSync, Void, Void> {
        TablesLastSyncDao TablesLastSyncDao = null;

        AddTablesLastSyncsAsyncTask(TablesLastSyncDao TablesLastSyncDao ){
            this.TablesLastSyncDao = TablesLastSyncDao;
        }


        @Override
        protected Void doInBackground(TablesLastSync... TablesLastSyncs) {
            this.TablesLastSyncDao.insertAll(TablesLastSyncs);
            return null;
        }


    }


}

