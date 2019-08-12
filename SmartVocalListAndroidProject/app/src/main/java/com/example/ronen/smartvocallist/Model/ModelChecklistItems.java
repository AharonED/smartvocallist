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

    @Override
    public void addItem(ChecklistItem chk){
        super.addItem(chk);
    }

    @Override
    public void deleteItem(ChecklistItem chk){
        super.deleteItem(chk);
    }

}

