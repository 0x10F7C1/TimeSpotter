package com.example.timespotter.DbContexts;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.EventType;
import com.example.timespotter.Events.MyProfileEvent;
import com.example.timespotter.PlacesDb;
import com.example.timespotter.Result;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class ProfileFragmentDb {
    private static final String TAG = ProfileFragmentDb.class.getSimpleName();
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private final StorageReference storage = FirebaseStorage.getInstance().getReference();
    private boolean avatarChanged = false, userUpdated = false;
    public void updateUserProfile(User newUser, Uri avatarUri, boolean isAvatarChanged, boolean isProfileUpdated) {
        if (isProfileUpdated) {
            updateUserNode(newUser, isAvatarChanged);
            if (!AppData.user.getUsername().equals(newUser.getUsername())) {
                new LeaderboardFragmentDb().updateUserUsernameLeaderboard(newUser.getUsername());
                new PlacesDb().updatePlacesCreatorUsername(newUser.getUsername());
            }
        }
        if (isAvatarChanged) {
            updateUserAvatar(newUser, avatarUri, isProfileUpdated);
        }
    }

    private void updateUserNode(User newUser, boolean isAvatarChanged) {
        final Result result = new Result();
        database
                .child("Users")
                .child(newUser.getKey())
                .setValue(newUser)
                .addOnSuccessListener(unused -> {
                    if (isAvatarChanged) {
                        userUpdated = true;
                        if (avatarChanged) {
                            result.operationStatus = Result.SUCCESS;
                            result.event = EventType.USER_UPDATED;
                            EventBus.getDefault().post(result);
                            userUpdated = avatarChanged = false;
                        }
                    }
                    else {
                        result.operationStatus = Result.SUCCESS;
                        result.event = EventType.USER_UPDATED;
                        EventBus.getDefault().post(result);
                    }
                })
                .addOnFailureListener(error -> {
                    result.operationStatus = Result.FAILURE;
                    result.errMsg = error.getMessage();
                    Log.d(TAG, error.getMessage());
                });
    }

    private void updateUserAvatar(User newUser, Uri avatarUri, boolean isProfileUpdated) {
        final Result result = new Result();
        String avatarKey = UUID.randomUUID().toString();
        storage
                .child("Avatars")
                .child(avatarKey)
                .putFile(avatarUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot
                            .getStorage()
                            .getDownloadUrl()
                            .addOnSuccessListener(imageUrl -> {
                                new LeaderboardFragmentDb().updateUserImageLeaderboard(imageUrl.toString());
                                database
                                        .child("Users")
                                        .child(newUser.getKey())
                                        .child("imageUri")
                                        .setValue(imageUrl.toString())
                                        .addOnSuccessListener(unused -> {
                                            if (isProfileUpdated) {
                                                avatarChanged = true;
                                                if (userUpdated) {
                                                    result.operationStatus = Result.SUCCESS;
                                                    result.event = EventType.USER_UPDATED;
                                                    EventBus.getDefault().post(result);
                                                    avatarChanged = userUpdated = false;
                                                }
                                            }
                                            else {
                                                result.operationStatus = Result.SUCCESS;
                                                result.event = EventType.USER_UPDATED;
                                                EventBus.getDefault().post(result);
                                            }
                                        })
                                        .addOnFailureListener(error -> {
                                            result.operationStatus = Result.FAILURE;
                                            result.errMsg = error.getMessage();
                                           Log.d(TAG, error.getMessage());
                                        });
                            })
                            .addOnFailureListener(error -> {
                                result.operationStatus = Result.FAILURE;
                                result.errMsg = error.getMessage();
                               Log.d(TAG, error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    result.operationStatus = Result.FAILURE;
                    result.errMsg = error.getMessage();
                   Log.d(TAG, error.getMessage());
                });
    }

}
