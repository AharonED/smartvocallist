package com.example.ronen.smartvocallist.DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ChecklistReported extends Checklist implements Serializable {
    public ChecklistReported(String id) {
        super(id);
        setChecklistType("Reported");
    }

    public ChecklistReported(String _id, String name, String description, String url, Double lastUpdate) {
        super(_id,name,description,url,lastUpdate);


        JSONObject json;
        json = super.toJson();

        try {
            json.put("checklistType","Reported");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.Checklists(json);
        super.tableName="Checklist";

    }

    @Override
    public  ChecklistReported CopyChecklist()
    {
        Checklist tmpChk = super.CopyChecklist();
        tmpChk.setChecklistType("Reported");
        return (ChecklistReported)tmpChk;
    }

}
