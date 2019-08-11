package com.example.ronen.smartvocallist.DataObjects;

import androidx.room.Entity;

import java.io.Serializable;

@Entity
public class TablesLastSync extends BaseModelObject implements Serializable {

    public TablesLastSync(String id) {
        super(id);
    }

    /*
   // @PrimaryKey
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
*/
}
