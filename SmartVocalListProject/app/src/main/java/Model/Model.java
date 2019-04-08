package Model;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;

import java.util.ArrayList;

import DataObjects.BaseModelObject;
import DataObjects.Checklist;

public class Model<T extends BaseModelObject> implements IModel, Serializable {

    private String tableName="";

    public Model.ItemsLsnr<T> getItemsLsnr() {
        return ItemsLsnr;
    }

    public void setItemsLsnr(Model.ItemsLsnr<T> itemsLsnr) {
        ItemsLsnr = itemsLsnr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public interface ItemsLsnr<E extends BaseModelObject> {
        public void getItemsLsnr(ArrayList<E> items );
    }

    private ItemsLsnr<T> ItemsLsnr;

    //public static Model model;
    public  ArrayList<T> items = new ArrayList<>();

/*
    public static Model getInstance()
    {
          if(model == null)
              model = new Model();
          return  model;
    }
*/

    Class<T> getType(){return type;}

    private Class<T> type;
    public Model(Class<T> cls)
    {
        type= cls;
        if(items.size()==0) {
            switch (getType().getName()) {
                case "DataObjects.Checklist":
                    //items = (ArrayList<T>) rep.GetChecklists((ItemsLsnr<Checklist>) getItemsLsnr);
                    setTableName("Checklist");
                    break;
            }
        }

    }

    public void addItem(T chk)
    {
        //fairebase.addChild( chk.tableName  , chk);
        items.add(chk);

    }


    public ArrayList<T> getItemsAsync(Model.ItemsLsnr<BaseModelObject> lsnr) {
        Repository rep = new Repository();
        items = (ArrayList<T>) rep.GetChecklists(lsnr);

        return items;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public T getItemByID(String id)
    {
        return items.stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
    }

}

