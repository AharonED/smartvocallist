package com.example.ronen.smartvocallist;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class CheckListsActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_lists);

        navController = Navigation.findNavController(this, R.id.main_navigation);
        BottomNavigationView nav = findViewById(R.id.main_bottonNavigation);
        NavigationUI.setupWithNavController(nav, navController);
    }

}
