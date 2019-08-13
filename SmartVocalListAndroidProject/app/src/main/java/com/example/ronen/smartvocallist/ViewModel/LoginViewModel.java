package com.example.ronen.smartvocallist.ViewModel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.ronen.smartvocallist.Controller.MyApplication;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

public class LoginViewModel extends ViewModel {

    LoginViewModel(){
        try {
            FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                    .setApplicationId("1:0123456789012:android:0123456789abcdef")
                    .setApiKey("AIzaSyDMIVUTpX7i0L__KDhiQb4zfzvMxmjwNec ")
                    .setDatabaseUrl("https://smartvocallist1.firebaseio.com/")
                    .setStorageBucket("smartvocallist1.appspot.com");
            FirebaseApp.initializeApp(MyApplication.getContext(), builder.build());
        }
        catch (Exception ex)
        {
            Log.d(TAG, "Value is: " + ex.getMessage());
        }

    }
    public FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
}
