package com.example.ronen.smartvocallist.ViewModel;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class SignupViewModel extends ViewModel{

    public FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }
}
