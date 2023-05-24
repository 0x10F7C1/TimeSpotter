package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.Events.MyProfileEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

public class ProfileFragmentDb {
    private static final String TAG = ProfileFragmentDb.class.getSimpleName();
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void updateUserProfile(User oldUser, User newUser) {
        if (!User.areUsersEqual(oldUser, newUser)) {
            updateUserNode(newUser);
        }
    }

    private void updateUserNode(User newUser) {
        database
                .child("Users")
                .child(newUser.getKey())
                .setValue(newUser)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MyProfileEvent.UserUpdate());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
