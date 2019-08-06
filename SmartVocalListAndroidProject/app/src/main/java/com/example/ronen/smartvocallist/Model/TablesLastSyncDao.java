package com.example.ronen.smartvocallist.Model;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.TablesLastSync;

import java.util.List;

@Dao
public interface TablesLastSyncDao {
    @Query("select * from TablesLastSync")
    List<TablesLastSync> getAll();

    @Query("SELECT * FROM TablesLastSync WHERE tableName LIKE :tableName LIMIT 1")
    Checklist findByName(String tableName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TablesLastSync... tablesLastSync);

    @Delete
    void delete(TablesLastSync tablesLastSync);

}
