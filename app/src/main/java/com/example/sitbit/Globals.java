package com.example.sitbit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

public class Globals {

    private static Globals instance;

    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase firebaseDatabase;

    private File sedentaryDataFile;

    private Globals() {}

    public static Globals getInstance() {
        if (instance == null)
            instance = new Globals();

        return instance;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public void setFirebaseDatabase(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    public File getSedentaryDataFile() {
        return sedentaryDataFile;
    }

    public void setSedentaryDataFile(File sedentaryDataFile) {
        this.sedentaryDataFile = sedentaryDataFile;
    }

}
