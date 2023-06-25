package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.Utils.AppData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PlacesDb {
    private static final String TAG = PlacesDb.class.getSimpleName();
    private final DatabaseReference database;
    public PlacesDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }
    public void updatePlacesCreatorUsername(String username) {
        final Map<String, Object> updates = new HashMap<>();
        database
                .child("Places")
                .orderByChild("creatorKey")
                .equalTo(AppData.user.getKey())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        updates.put("Places/" + snapshot.getKey() + "/creatorUsername/", username);
                    }
                    if (dataSnapshot.exists())
                        database.updateChildren(updates);
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
