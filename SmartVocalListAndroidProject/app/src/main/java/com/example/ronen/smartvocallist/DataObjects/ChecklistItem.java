package com.example.ronen.smartvocallist.DataObjects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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

    public int getIsReq() {
        return IsReq;
    }

    public void setIsReq(int isReq) {
        IsReq = isReq;
    }

    private int IsReq;

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
        attributes = attributes.toLowerCase();
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
            json.put("IsReq",getIsReq());

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


            if (json.has("IsReq") && json.get("IsReq") != null)
                this.setIsReq(Integer.parseInt(  json.get("IsReq").toString()));

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
            json.put("id", id);
            json.put("itemIndex",index);
            json.put("name",name);
            json.put("description",description);
            json.put("url",url);
            json.put("lastUpdate",lastUpdate);
            json.put("attributes",attributes);
            json.put("checklistId",checklistId);

            json.put("IsReq",IsReq);


            json.put("itemIndex", getIndex());
            json.put("itemType",itemType);
            json.put("owner",owner);
            json.put("result",result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }




        public String toKeywords(){
        StringBuilder textForFile = new StringBuilder();

        for (String keyWord : options) {
            textForFile.append(keyWord + "/1e-1/\n");
        }

        return textForFile.toString().toLowerCase();
    }
}
