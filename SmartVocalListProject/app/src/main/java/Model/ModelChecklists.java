package Model;


import java.io.Serializable;
import java.util.ArrayList;

import DataObjects.BaseModelObject;
import DataObjects.Checklist;

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
        setTableName("Checklist");
    }


    @Override
    public void getItemsAsync(ItemsLsnr<Checklist> lsnr) {
        Repository rep = new Repository();
        items =  rep.GetChecklists(lsnr);

    }

}

