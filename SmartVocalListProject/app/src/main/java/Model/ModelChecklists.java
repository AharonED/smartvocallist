package Model;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import DataObjects.BaseModelObject;
import DataObjects.Checklist;
import DataObjects.ChecklistItem;

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
        setTableName("ChecklistItems");
    }


    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void getItemsAsync(ItemsLsnr<Checklist> lsnr) {
        Repository rep = new Repository();
        //items =  rep.GetChecklists(lsnr);

        items =  rep.GetChecklists(new ItemsLsnr<Checklist>() {
                                       @Override
                                       public void getItemsLsnr(ArrayList<Checklist> items) {

                                           ArrayList < Checklist > chks = items;
                                           ModelChecklistItems.getInstance().getItemsAsync(new ItemsLsnr<ChecklistItem>() {
                                               @Override
                                               public void getItemsLsnr(ArrayList<ChecklistItem> items) {
                                                   for (Checklist chk: chks) {
//                    ArrayList<ChecklistItem> tmp = (ArrayList<ChecklistItem>) items.stream().filter(o -> o.getChecklistId().equals(chk.getId()));

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

                                           lsnr.getItemsLsnr(items);
                                           //::items = items;
                                       }
                                   });


    }
//
}

