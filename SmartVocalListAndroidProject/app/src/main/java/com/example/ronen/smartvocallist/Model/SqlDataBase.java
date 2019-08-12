package com.example.ronen.smartvocallist.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ronen.smartvocallist.Controller.MyApplication;
import com.example.ronen.smartvocallist.DataObjects.Checklist;
import com.example.ronen.smartvocallist.DataObjects.TablesLastSync;

@Database(entities = {Checklist.class, TablesLastSync.class},
            version = 14    )
@TypeConverters({Converters.class})
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract ChecklistDao checklistDao();
    public abstract  TablesLastSyncDao tablesLastSyncDao() ;
}

public class SqlDataBase{
        static public AppLocalDbRepository db =
                Room.databaseBuilder(MyApplication.getContext(),
                        AppLocalDbRepository.class,
                        "Local-Database.db")
                        .fallbackToDestructiveMigration()
                        .build();
}
