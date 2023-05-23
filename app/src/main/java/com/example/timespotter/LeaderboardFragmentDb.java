package com.example.timespotter;

import android.util.Log;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.GeneralActivities.HomeScreenActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

public class LeaderboardFragmentDb {
    private static final String TAG = LeaderboardFragmentDb.class.getSimpleName();
    private final DatabaseReference database;
    public LeaderboardFragmentDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }
}
