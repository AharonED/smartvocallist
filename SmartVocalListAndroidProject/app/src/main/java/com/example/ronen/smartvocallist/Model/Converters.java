package com.example.ronen.smartvocallist.Model;

import androidx.room.TypeConverter;

import com.example.ronen.smartvocallist.DataObjects.ChecklistItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class Converters {
    @TypeConverter
    public static ArrayList<ChecklistItem> fromString(String value) {
        Type listType = new TypeToken<ArrayList<ChecklistItem>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayList(ArrayList<ChecklistItem> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
