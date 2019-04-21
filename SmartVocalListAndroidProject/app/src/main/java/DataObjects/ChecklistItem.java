package DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChecklistItem extends BaseModelObject implements Serializable {

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

    private String name;
    private String description;
    private String url;
    private int index;
    private ItemType itemType=ItemType.Boolean;

    //Parent Checklist ID
    public String getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(String checklistId) {
        this.checklistId = checklistId;
    }

    //Owner user
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    private String checklistId;
    private String owner;
    private String result;


    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
        String [] seperated = attributes.split(";|,|/");
        options.addAll(Arrays.asList(seperated));

    }

    private String attributes;


    public ArrayList<String> getOptions() {
        return options;
    }

    public final ArrayList<String> options = new ArrayList<>();

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public ChecklistItem(String _id, int index, String name, String description, String url, Double lastUpdate) {
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
            //index = (int) json.get("index");
            name = (String) json.get("name");
            description = (String) json.get("description");
            if (json.has("url") && json.get("url") != null)
                url = (String) json.get("url");
//
            if (json.has("attributes") && json.get("attributes") != null)
                this.setAttributes( (String) json.get("attributes"));
            if (json.has("checklistId") && json.get("checklistId") != null)
                this.setChecklistId( (String) json.get("checklistId"));
            if (json.has("itemType") && json.get("itemType") != null)
                this.setItemType(ItemType.Text );//(String) json.get("itemType")
            if (json.has("owner") && json.get("owner") != null)
                this.setOwner( (String) json.get("owner"));
            if (json.has("result") && json.get("result") != null)
                this.setResult( (String) json.get("result"));

            if (json.has("itemIndex") && json.get("itemIndex") != null)
                this.setIndex( Integer.parseInt(  json.get("itemIndex").toString()));

            if (json.has("lastUpdate") && json.get("lastUpdate") != null)
                lastUpdate =  Double.parseDouble(json.get("lastUpdate").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.BaseModelObject(json);
        super.tableName="ChecklistItems";

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
    public String toGrammar() {
        String Gram="#JSGF V1.0;\n" +
                "\n" +
                "grammar menu;\n" +
                "\n" +
                "public <item> = back | next | set | skip | read |  ";

        String opt=null;

        for (int i=0;i<options.size();i++) {
            opt=options.get(i);
            Gram = Gram + opt ;
            if(i<options.size()-1)
               Gram = Gram + " | ";
        }
        Gram = Gram + ";\n";

        return Gram;
    }

    public String toKeywords(){
        StringBuilder textForFile = new StringBuilder();

        for (String keyWord : options) {
            textForFile.append(keyWord + "/1e-1/\n");
        }

        return textForFile.toString();
    }
}
