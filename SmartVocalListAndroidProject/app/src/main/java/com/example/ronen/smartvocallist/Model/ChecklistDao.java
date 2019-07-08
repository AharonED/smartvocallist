package com.example.ronen.smartvocallist.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ronen.smartvocallist.DataObjects.Checklist;

import java.util.List;

@Dao
public interface  ChecklistDao {
    @Query("select * from Checklist")
    List<Checklist> getAll();

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    User findByName(String first, String last);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Checklist... checklists);

    @Delete
    void delete(Checklist checklist);
}
