package DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Checklist extends BaseModelObject {

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    private String groupId;
    private String owner;
    private String checklistType;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getChecklistType() {
        return checklistType;
    }

    public void setChecklistType(String checklistType) {
        this.checklistType = checklistType;
    }

    public void setChecklistItems(ArrayList<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }


    public  ArrayList<ChecklistItem> checklistItems = new ArrayList<>();

    public Checklist(String id)
    {
        super(id);
    }

    public ArrayList<ChecklistItem> getSorted() {
//As lambda
//      Collections.sort(checklistItems, (i1, i2) -> (i2.getIndex() > i1.getIndex() ? 1 : -1));

        Collections.sort(checklistItems, new Comparator<ChecklistItem>() {
            @Override
            public int compare(ChecklistItem i1, ChecklistItem i2) {
                return (i2.getIndex() > i1.getIndex() ? 1 : -1);
            }
        });
        return checklistItems;
    }

    public ArrayList<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }


    public Checklist(String _id, String name, String description, String url, Double lastUpdate) {
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
        super.tableName="Checklist";

     }

    public void Checklists(JSONObject json) {
        try {
            name = (String) json.get("name");
            description = (String) json.get("description");
            url = (String) json.get("url");
            if (json.has("lastUpdate") && json.get("lastUpdate") != null)
                lastUpdate =  Double.parseDouble(json.get("lastUpdate").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.BaseModelObject(json);
        super.tableName="Checklist";

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

    public Checklist getInstance()
    {
        return new Checklist("--");
    }
}
