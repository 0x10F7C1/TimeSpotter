package com.example.timespotter.DbMediators;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.Events.LeaderboardFragmentEvent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
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
                        users.sort(Collections.reverseOrder());
                        EventBus.getDefault().post(new LeaderboardFragmentEvent.LeaderboardLoaded());
                    }
                    else {
                        Log.d(TAG, "Leaderboard is empty");
                    }
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
