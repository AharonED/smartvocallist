package com.example.ronen.smartvocallist.Model;


import android.annotation.TargetApi;
import android.os.Build;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

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
        setTableName("ChecklistsReported");
    }


    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void getItemsAsync(ItemsLsnr<Checklist> lsnr) {
        super.getItemsAsyncByLastUpdate(new SyncLsnr<Checklist>() {
            @Override
            public void OnSync(Double lastUpdate) {
                items = rep.GetChecklistsReported(new ItemsLsnr<Checklist>() {
                    @Override
                    public void OnDataChangeItemsLsnr(ArrayList<Checklist> items) {
                        ArrayList<Checklist> chks = items;
                        //For each Checklist get its all ChecklistItems
                        for (Checklist chk: chks) {
                            String checklistID = chk.getId();

                            ModelChecklistItems.getInstance().getItemsByChecklistAsync(new ItemsLsnr<ChecklistItem>() {
                                @Override
                                public void OnDataChangeItemsLsnr(ArrayList<ChecklistItem> items) {
                                    // for (Checklist chk: chks) {
                                    Iterator<ChecklistItem> iterator = items.iterator();
                                    while (iterator.hasNext()) {
                                        ChecklistItem item = iterator.next();
                                        if (item.getChecklistId() != null && item.getChecklistId().equals(chk.getId())) {
                                            chk.getChecklistItems().add(item);
                                        }
                                    }

                                    // Save in local db
                                    rep.saveCheckListsInLocalDB(chks, new Repository.LocalDbUpdatedLsnr() {
                                        @Override
                                        public void OnLocalDbUpdateLsnr() {
                                            //Call the callback function (usually rise from GUI/Controller)
                                            lsnr.OnDataChangeItemsLsnr(chks);
                                        }
                                    });
                                }
                            }, checklistID);
                        }
                    }
                },lastUpdate);

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
    public void deleteItem(ItemsLsnr<Checklist> lsnr, Checklist chk){
        rep.DeleteLocalCheckList(lsnr, chk);

        for (ChecklistItem itm: chk.checklistItems) {
            //itm.id = java.util.UUID.randomUUID().toString();
            //itm.setChecklistId(chk.id);
            ModelChecklistItems.getInstance().deleteItem(null, itm);
        }

        super.deleteItem(lsnr, chk);
    }

}

