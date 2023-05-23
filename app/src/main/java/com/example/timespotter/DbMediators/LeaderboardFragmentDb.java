package com.example.timespotter.DbMediators;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LeaderboardFragmentDb {
    private static final String TAG = LeaderboardFragmentDb.class.getSimpleName();
    private final DatabaseReference database;

    public LeaderboardFragmentDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }
}
