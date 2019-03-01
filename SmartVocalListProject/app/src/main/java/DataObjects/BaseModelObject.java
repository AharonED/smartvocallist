
//
//  BaseModelObject.Java
//  SmartListIOS
//
//  Created by admin on 24/12/2018.
//  Copyright © 2018 Aharon.Garada. All rights reserved.
//

package DataObjects;

import java.lang.*;
import java.lang.Double;
import java.util.ArrayList;
import org.json.*;

public class BaseModelObject {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Double getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Double lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String id;
    public String tableName = "BaseModelObject";
    public Double lastUpdate;

    public BaseModelObject(String _id)
    {
        /*
        try {
            JSONObject json = new JSONObject(
                    "{\"id\":\"" + _id + "}"
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */

        /*
        JSONObject jo = new JSONObject();
        jo.put("name", "jon doe");
        jo.put("age", "22");
        jo.put("city", "chicago");
         */

        /*
        DemoBean demo = new DemoBean();
        demo.setId(1);
        demo.setName("lorem ipsum");
        demo.setActive(true);

        JSONObject jo = new JSONObject(demo);
         */

        /*
        JSONArray ja = new JSONArray();
        ja.put(Boolean.TRUE);
        ja.put("lorem ipsum");

        JSONObject jo = new JSONObject();
        jo.put("name", "jon doe");
        jo.put("age", "22");
        jo.put("city", "chicago");

        ja.put(jo);

         */

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
                lastUpdate = (Double) json.get("lastUpdate");

            } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //@SuppressWarnings({ "rawtypes", "unchecked" })
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

}
