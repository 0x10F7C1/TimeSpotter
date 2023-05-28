package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.Events.MainActivityEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

public class MainActivityDb {
    private static final String TAG = MainActivityDb.class.getSimpleName();
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void userLogin(String username, String password) {
        database
                .child("Users")
                .orderByChild("username")
                .equalTo(username)
                .get()
                .addOnSuccessListener(snapshot -> {
                    User user = null;
                    for (DataSnapshot data : snapshot.getChildren()) {
                        user = data.getValue(User.class);
                    }
                    if (user != null && user.getPassword().equals(password)) {
                        EventBus.getDefault().post(new MainActivityEvent.UserLoginSuccess(user));
                    } else {
                        Log.d(TAG, "Username/Password is invalid!");
                    }
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
