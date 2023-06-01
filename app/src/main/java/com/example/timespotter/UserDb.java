package com.example.timespotter;

import android.net.Uri;
import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

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

    public void userLogin(String username, String password) {
        final Result result = new Result();
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
                        //EventBus.getDefault().post(new MainActivityEvent.UserLoginSuccess(user));
                        result.operationStatus = Result.SUCCESS;
                        result.event = EventType.USER_LOGIN;
                        result.result = user;
                        EventBus.getDefault().post(result);
                    } else {
                        result.operationStatus = Result.FAILURE;
                        result.event = EventType.USER_LOGIN;
                        result.errMsg = "Username/Password is invalid!";
                        EventBus.getDefault().post(result);
                        //Log.d(TAG, "Username/Password is invalid!");
                    }
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    //public void userSignup(User user, Uri avatarUri)
}
