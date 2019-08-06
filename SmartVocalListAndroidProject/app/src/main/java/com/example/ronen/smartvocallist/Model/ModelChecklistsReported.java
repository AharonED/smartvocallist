package com.example.ronen.smartvocallist.Model;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;

public class ModelChecklistsReported extends Model<Checklist> implements Serializable {

    private static ModelChecklistsReported instance ;
    public static ModelChecklistsReported getInstance()
    {
        if (instance == null)
        {
            instance=new ModelChecklistsReported();
        }
        return instance;
    }

    private ModelChecklistsReported()
    {
        super();
        setTableName("ChecklistItems");
    }


    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void getItemsAsync(ItemsLsnr<Checklist> lsnr) {
        //Repository rep = new Repository();
        //items =  rep.GetChecklists(lsnr);

        items =  rep.GetChecklistsReported(new ItemsLsnr<Checklist>() {
                                       @Override
                                       public void OnDataChangeItemsLsnr(ArrayList<Checklist> items) {
                                            //Get All Checklists
                                           ArrayList <Checklist> chks = items;
                                           //For each ChecklistReported get its all ChecklistItems
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


    }

    public void getLocalChecklistAsync(ItemsLsnr<Checklist> lsnr)
    {
        rep.GetReportedCheckListsLocal(lsnr);
    }


    @Override
    public void addItem(Checklist chk){
        if(chk.id=="-1") {
            chk.id = java.util.UUID.randomUUID().toString();
        }

        for (ChecklistItem itm: chk.checklistItems) {
           // itm.id = java.util.UUID.randomUUID().toString();
           // itm.setChecklistId(chk.id);
            ModelChecklistItems.getInstance().addItem(itm);
        }
        super.addItem(chk);
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

