package Model;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import DataObjects.Checklist;
import DataObjects.ChecklistItem;
import DataObjects.ChecklistReported;

public class ModelChecklistsReported extends Model<ChecklistReported> implements Serializable {

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
    public void getItemsAsync(ItemsLsnr<ChecklistReported> lsnr) {
        //Repository rep = new Repository();
        //items =  rep.GetChecklists(lsnr);

        items =  rep.GetChecklistsReported(new ItemsLsnr<ChecklistReported>() {
                                       @Override
                                       public void OnDataChangeItemsLsnr(ArrayList<ChecklistReported> items) {
                                            //Get All Checklists
                                           ArrayList <ChecklistReported> chks = items;
                                           //For each ChecklistReported get its all ChecklistItems
                                           ModelChecklistItems.getInstance().getItemsAsync(new ItemsLsnr<ChecklistItem>() {
                                               @Override
                                               public void OnDataChangeItemsLsnr(ArrayList<ChecklistItem> items) {
                                                   for (ChecklistReported chk: chks) {
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

    @Override
    public void addItem(ChecklistReported chk){
        if(chk.id=="-1") {
            chk.id = java.util.UUID.randomUUID().toString();
        }

        for (ChecklistItem itm: chk.checklistItems) {
            itm.id = java.util.UUID.randomUUID().toString();
            itm.setChecklistId(chk.id);
            ModelChecklistItems.getInstance().addItem(itm);
        }
        super.addItem(chk);
    }

}

