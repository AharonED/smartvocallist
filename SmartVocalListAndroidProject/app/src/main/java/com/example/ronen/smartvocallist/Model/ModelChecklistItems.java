package com.example.ronen.smartvocallist.Model;


import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;

import java.io.Serializable;
//
public class  ModelChecklistItems extends Model<ChecklistItem> implements Serializable {

    private static ModelChecklistItems instance ;
    public static ModelChecklistItems getInstance()
    {
        if (instance == null)
        {
            instance=new ModelChecklistItems();
        }
        return instance;
    }

    private ModelChecklistItems()
    {
        super();
        setTableName("ChecklistItems");
    }


    @Override
    public void getItemsAsync(ItemsLsnr<ChecklistItem> lsnr) {
        items =  rep.GetChecklistItems(lsnr, 0.0);
    }

    public void getItemsByChecklistAsync(ItemsLsnr<ChecklistItem> lsnr, String checklistID) {
        items =  rep.GetChecklistItems(lsnr, 0.0, checklistID);
    }

    @Override
    public void addItem(ChecklistItem chk){
        super.addItem(chk);
    }

    @Override
    public void deleteItem(ItemsLsnr<ChecklistItem> lsnr, ChecklistItem chk){
        super.deleteItem(lsnr, chk);
    }

}

