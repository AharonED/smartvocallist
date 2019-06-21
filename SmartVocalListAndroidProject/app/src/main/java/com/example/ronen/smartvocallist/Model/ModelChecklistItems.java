package com.example.ronen.smartvocallist.Model;


import java.io.Serializable;

import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
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
        items =  rep.GetChecklistItems(lsnr);
    }

    @Override
    public void addItem(ChecklistItem chk){
        super.addItem(chk);
    }

    @Override
    public void deleteItem(ChecklistItem chk){
        super.deleteItem(chk);
    }

}

