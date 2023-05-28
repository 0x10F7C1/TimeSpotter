package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.Events.SignupActivityEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class SignupActivityDb {
    private static final String TAG = SignupActivityDb.class.getSimpleName();
    private final DatabaseReference database;

    public SignupActivityDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void userSignup(User user) {
        String userKey = UUID.randomUUID().toString();
        user.setKey(userKey);
        database
                .child("Users")
                .orderByChild("username")
                .equalTo(user.getUsername())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists())
                        System.out.println("Can't register, username already exists");
                    else registerUser(user);
                }).addOnFailureListener(error -> Log.d(TAG, error.getMessage()));
    }

    private void registerUser(User user) {
        database
                .child("Users")
                .child(user.getKey())
                .setValue(user)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new SignupActivityEvent.RegisterUser());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
        UserLeaderboard userLeaderboard = new UserLeaderboard(user.getUsername(), user.getName(), "", user.getPoints());
        database
                .child("Leaderboards")
                .child(user.getKey())
                .setValue(userLeaderboard)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new SignupActivityEvent.RegisterUser());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
