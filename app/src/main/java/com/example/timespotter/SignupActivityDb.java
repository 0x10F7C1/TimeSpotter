package com.example.timespotter;

import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.DataModels.UserLeaderboard;
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
                .child(user.getUsername())
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
