package Model;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import DataObjects.BaseModelObject;

public  abstract class Model <T extends BaseModelObject> implements IModel, Serializable {

    private String tableName="";

    public Model.ItemsLsnr<T> getItemsLsnr() {
        return ItemsLsnr;
    }
    public interface ItemsLsnr<E extends BaseModelObject> {
        void getItemsLsnr(ArrayList<E> items);
    }

    private ItemsLsnr<T> ItemsLsnr;

    public void setItemsLsnr(Model.ItemsLsnr<T> itemsLsnr) {
        ItemsLsnr = itemsLsnr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    //public static Model model;
    public  ArrayList<T> items = new ArrayList<>();


    public Model()
    {

    }

    public void addItem(T chk)
    {
        //fairebase.addChild( chk.tableName  , chk);
        items.add(chk);

    }


    public void getItemsAsync(Model.ItemsLsnr<T> lsnr) {
        Repository rep = new Repository();
        ////items = (ArrayList<T>) rep.GetChecklists(lsnr);

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

