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
    @Query("select * from Checklist where checklistType not like 'Reported'")
    List<Checklist> getAllCheckLists();

    @Query("select * from Checklist where checklistType like 'Reported'")
    List<Checklist> getAllReportedCheckLists();

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    List<User> loadAllByIds(int[] userIds);
//
    @Query("SELECT * FROM Checklist WHERE id LIKE :id LIMIT 1")
    Checklist findById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Checklist... checklists);

    @Delete
    void delete(Checklist checklist);
}
