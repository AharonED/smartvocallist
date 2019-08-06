package com.example.ronen.smartvocallist.DataObjects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TablesLastSync {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tableName")
    public String tableName = "";
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
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

}
