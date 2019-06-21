package com.example.ronen.smartvocallist.Controller;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.ronen.smartvocallist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import static android.content.ContentValues.TAG;

public class CheckListsActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lists);

        //Grant storage writing for saving images
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            final int REQUEST_WRITE_STORAGE_CODE = 1;
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_CODE);
        }

        //This Firebase init code should be moved to global place....
        try {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId("1:0123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDMIVUTpX7i0L__KDhiQb4zfzvMxmjwNec ")
                    .setDatabaseUrl("https://smartvocallist1.firebaseio.com/")
                    .setStorageBucket("smartvocallist1.appspot.com");
            FirebaseApp.initializeApp(this.getBaseContext(), builder.build());

        }
        catch (Exception ex)
        {
            Log.d(TAG, "Value is: " + ex.getMessage());
        }

        navController = Navigation.findNavController(this, R.id.main_navigation);
        BottomNavigationView nav = findViewById(R.id.main_bottonNavigation);
        NavigationUI.setupWithNavController(nav, navController);

    }

}
