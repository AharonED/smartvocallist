package com.example.ronen.smartvocallist.Controller;

import android.content.Context;
import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}