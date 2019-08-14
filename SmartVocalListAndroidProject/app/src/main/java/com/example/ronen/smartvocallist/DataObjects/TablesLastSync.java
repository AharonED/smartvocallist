package com.example.ronen.smartvocallist.DataObjects;

import androidx.room.Entity;

import java.io.Serializable;

@Entity
public class TablesLastSync extends BaseModelObject implements Serializable {

    public TablesLastSync(String id) {
        super(id);
    }


}
