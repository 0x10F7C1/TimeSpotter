package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.Utils.AppData;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.Events.LeaderboardFragmentEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class LeaderboardFragmentDb {
    private static final String TAG = LeaderboardFragmentDb.class.getSimpleName();
    private final DatabaseReference database;

    public LeaderboardFragmentDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void loadLeaderboard(List<UserLeaderboard> users) {
        database
                .child("Leaderboards")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            users.add(data.getValue(UserLeaderboard.class));
                        }
                        EventBus.getDefault().post(new LeaderboardFragmentEvent.LeaderboardLoaded());
                    } else {
                        Log.d(TAG, "Leaderboard is empty");
                    }
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
    public void updateUserImageLeaderboard(String imageUrl) {
        database
                .child("Leaderboards")
                .child(AppData.user.getKey())
                .child("imageUri")
                .setValue(imageUrl);
    }
    public void updateUserUsernameLeaderboard(String username) {
        database
                .child("Leaderboards")
                .child(AppData.user.getKey())
                .child("username")
                .setValue(username);
    }
}
