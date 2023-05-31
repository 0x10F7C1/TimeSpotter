package com.example.timespotter;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDb {
    private static final String TAG = UserDb.class.getSimpleName();
    private final DatabaseReference database;
    public UserDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void updatePlaceCreatorSubmissions() {
        database
                .child("Users")
                .child(AppData.user.getKey())
                .child("submissions")
                .setValue(AppData.user.getSubmissions() + 1)
                .addOnSuccessListener(unused -> {
                    AppData.user.setSubmissions(AppData.user.getSubmissions() + 1);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());
                });
    }
}
