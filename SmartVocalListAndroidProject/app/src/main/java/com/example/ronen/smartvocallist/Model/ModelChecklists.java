package com.example.ronen.smartvocallist.Model;


import android.annotation.TargetApi;
import android.os.Build;

import androidx.lifecycle.LiveData;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelChecklists  extends Model<Checklist> implements Serializable {

    private static ModelChecklists instance ;

    public static ModelChecklists getInstance()
    {
        if (instance == null)
        {
            instance=new ModelChecklists();
        }
        return instance;
    }

    private ModelChecklists()
    {
        super();
        setTableName("Checklists");
    }


    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void getItemsAsync(ItemsLsnr<Checklist> lsnr) {
        //Repository rep = new Repository();
        //items =  rep.GetChecklists(lsnr);

        items =  rep.GetChecklists(new ItemsLsnr<Checklist>() {
           @Override
           public void OnDataChangeItemsLsnr(ArrayList<Checklist> items) {
                //Get All Checklists
               ArrayList <Checklist> chks = items;
               //For each Checklist get its all ChecklistItems
               ModelChecklistItems.getInstance().getItemsAsync(new ItemsLsnr<ChecklistItem>() {
                   @Override
                   public void OnDataChangeItemsLsnr(ArrayList<ChecklistItem> items) {
                       for (Checklist chk: chks) {
                           Iterator<ChecklistItem> iterator = items.iterator();
                           while (iterator.hasNext()) {
                               ChecklistItem item = iterator.next();
                               if (item.getChecklistId() != null &&  item.getChecklistId().equals(chk.getId())) {
                                   chk.getChecklistItems().add(item);
                               }
                           }
                       }
                   }
               });

               //Call the callback function (usually rise from GUI/Controller)
               lsnr.OnDataChangeItemsLsnr(items);
               //::items = items;
           }
       });

        // save in local db the new data
        //LocalGetCheckListsTask task = new LocalGetCheckListsTask(lsnr);
        //task.execute();
    }


    public LiveData<List<Checklist>>  GetChecklist ()
    {
        return  rep.GetCheckListsLocal();
    }

    @Override
    public void addItem(Checklist chk){
        if(chk.id=="-1") {
            chk.id = java.util.UUID.randomUUID().toString();
        }

        for (ChecklistItem itm: chk.checklistItems) {
            //itm.id = java.util.UUID.randomUUID().toString();
            //itm.setChecklistId(chk.id);
            ModelChecklistItems.getInstance().addItem(itm);
        }
        super.addItem(chk);

        // add new checklist to local db
        //localAddCheckListsTask task = new localAddCheckListsTask();
        //task.execute(chk);
    }

    @Override
    public void deleteItem(Checklist chk){

        for (ChecklistItem itm: chk.checklistItems) {
            //itm.id = java.util.UUID.randomUUID().toString();
            //itm.setChecklistId(chk.id);
            ModelChecklistItems.getInstance().deleteItem(itm);
        }
        super.deleteItem(chk);
    }

}

