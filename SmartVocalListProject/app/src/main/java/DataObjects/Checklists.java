package DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;

public class Checklists extends BaseModelObject {

    private String name;
    private String description;
    private String url;

    public final ArrayList<ChecklistItems> checklistsItems = new ArrayList<ChecklistItems>();

    public ArrayList<ChecklistItems> getSorted() {
        Collections.sort(checklistsItems, new Comparator<ChecklistItems>() {
            @Override
            public int compare(ChecklistItems i1, ChecklistItems i2) {
                return (i2.getIndex() > i1.getIndex() ? 1 : -1);
            }
        });
        return checklistsItems;
    }

    public ArrayList<ChecklistItems> getChecklistsItems() {
        return checklistsItems;
    }


    public Checklists(String _id, String name, String description, String url, Double lastUpdate) {
        super(_id);


        JSONObject json;
        json = super.toJson();

        try {
            json.put("name",name);
            json.put("description",description);
            json.put("url",url);
            json.put("lastUpdate",lastUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Checklists(json);

     }

    public void Checklists(JSONObject json) {
        try {
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
