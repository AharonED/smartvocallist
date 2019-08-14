
//
//  BaseModelObject.Java
//  SmartListIOS
//
//  Created by admin on 24/12/2018.
//  Copyright © 2018 Aharon.Garada. All rights reserved.
//

package com.example.ronen.smartvocallist.DataObjects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Entity
public class BaseModelObject implements Serializable {


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @ColumnInfo(name = "lastUpdate")
    public Double lastUpdate;
    public Double getLastUpdate() {
        if(lastUpdate==null) {
            lastUpdate = 0.0;
        }
        return lastUpdate;
    }
    public void setLastUpdate(Double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public String tableName = "BaseModelObject";
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public BaseModelObject(String _id)
    {


        JSONObject json=null;

        try {
            json = new JSONObject();
            json.put("id",_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        BaseModelObject(json);
    }


    public void BaseModelObject(JSONObject json) {

        try {
            if (json.get("id") != null) {
                id = (String) json.get("id");
            }
            else
            {
                throw new JSONException("Missing ID");
            }

            if (json.has("lastUpdate") && json.get("lastUpdate") != null)
               // if (json.get("lastUpdate") instanceof Long)
                    lastUpdate =  Double.parseDouble(json.get("lastUpdate").toString());


            } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject toJson()  {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public Map<String, Object> toMap() {
        JSONObject json = toJson();
        Map<String, Object> childUpdates = new HashMap<>();



        for (Iterator<String> it = json.keys(); it.hasNext(); ) {
            String key = it.next();

            try {
                childUpdates.put( key, json.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return  childUpdates;

    }

}
