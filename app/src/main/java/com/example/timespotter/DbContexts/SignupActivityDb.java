package com.example.timespotter.DbContexts;

import android.net.Uri;
import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.Events.SignupActivityEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class SignupActivityDb {
    private static final String TAG = SignupActivityDb.class.getSimpleName();
    private final DatabaseReference database;
    private final StorageReference storage;

    public SignupActivityDb() {
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void userSignup(User user, Uri avatarUri) {
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
                    else uploadUserAvatar(user, avatarUri);
                }).addOnFailureListener(error -> Log.d(TAG, error.getMessage()));
    }

    private void uploadUserAvatar(User user, Uri avatarUri) {
        String avatarKey = UUID.randomUUID().toString();
        storage
                .child("Avatars")
                .child(avatarKey)
                .putFile(avatarUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot
                            .getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                user.setImageUri(uri.toString());
                                registerUser(user);
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());
                });

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
        UserLeaderboard userLeaderboard = new UserLeaderboard(user.getUsername(), user.getName(), user.getImageUri(), user.getPoints());
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
