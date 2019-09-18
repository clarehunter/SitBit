package com.example.sitbit;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

public class Globals extends Application {

    private static Globals instance;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private Globals() {}

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static synchronized Globals getInstance() {
        if (instance == null)
            instance = new Globals();
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }
}
