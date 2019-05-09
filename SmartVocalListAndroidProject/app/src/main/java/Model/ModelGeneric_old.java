package Model;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;

import DataObjects.BaseModelObject;

public class ModelGeneric_old<T extends BaseModelObject> implements IModel, Serializable {

    private String tableName="";

    public ItemsLsnr<T> getItemsLsnr() {
        return ItemsLsnr;
    }

    public void setItemsLsnr(ItemsLsnr<T> itemsLsnr) {
        ItemsLsnr = itemsLsnr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public interface ItemsLsnr<E extends BaseModelObject> {
        void getItemsLsnr(ArrayList<E> items);
    }

    private ItemsLsnr<T> ItemsLsnr;

    //public static Model model;
    public  ArrayList<T> items = new ArrayList<>();

    Class<T> getType(){return type;}

    private Class<T> type;
    public ModelGeneric_old(Class<T> cls)
    {
        type= cls;
        if(items.size()==0) {
            switch (getType().getName()) {
                case "DataObjects.Checklist":
                    setTableName("Checklist");
                    break;
            }
        }

    }

    public void addItem(T chk)
    {
        //fairebase.addChild( chk.tableName  , chk);

        // myRef.setValue("Hello, World!");

        items.add(chk);

    }


    public void getItemsAsync(ItemsLsnr<BaseModelObject> lsnr) {
        Repository rep = new Repository();
        //items = (ArrayList<T>) rep.GetChecklists((Model.ItemsLsnr<BaseModelObject>) lsnr);

    }

    @TargetApi(Build.VERSION_CODES.N)
    public T getItemByID(String id)
    {
        //return items.stream().filter(o -> o.getId().equals(id)).findAny().orElse(null);
        Iterator<T> iterator = items.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

}

