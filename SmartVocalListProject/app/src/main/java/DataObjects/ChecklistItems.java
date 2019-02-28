package DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

public class ChecklistItems extends BaseModelObject {

    private String name;
    private String description;
    private String url;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public ChecklistItems(String _id, int index, String name, String description, String url, Double lastUpdate) {
        super(_id);


        JSONObject json;
        json = super.toJson();

        try {
            json.put("index",index);
            json.put("name",name);
            json.put("description",description);
            json.put("url",url);
            json.put("lastUpdate",lastUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChecklistItems(json);

    }

    public void ChecklistItems(JSONObject json) {
        try {
            index = (int) json.get("index");
            name = (String) json.get("name");
            description = (String) json.get("description");
            url = (String) json.get("url");
            if (json.has("lastUpdate") && json.get("lastUpdate") != null)
                lastUpdate = (Double) json.get("lastUpdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.BaseModelObject(json);
        super.tableName="Checklists";

    }

    public JSONObject toJson()  {
        JSONObject json;
        json = super.toJson();
        try {
            json = new JSONObject();
            json.put("index",index);
            json.put("name",name);
            json.put("description",description);
            json.put("url",url);
            json.put("lastUpdate",lastUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
