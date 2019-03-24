package Model;

import android.annotation.TargetApi;
import android.os.Build;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Optional;

import DataObjects.BaseModelObject;
import DataObjects.Checklist;
import DataObjects.ChecklistItem;

public class Model<T extends BaseModelObject> implements IModel {

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
                    items = (ArrayList<T>) Repository.GetChecklists();
                    break;
            }
        }

    }

    public void addItem(T chk)
    {
        //fairebase.addChild( chk.tableName  , chk);
        items.add(chk);

    }


    public ArrayList<T> getItems() {
        return items;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public T getItemByID(String id)
    {
        if( items.stream().filter(o -> o.getId().equals(id)).findFirst().isPresent())
        {
            Optional<T> item =  items.stream().filter(o -> o.getId().equals(id)).findFirst();
            return item.get();
        }
        else {
            return null;
        }
    }

}

