package com.example.timespotter.DbContexts;

import android.util.Log;

import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.Events.LocationTemplateActivityEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class LocationTemplateActivityDb {
    private static final String TAG = LocationTemplateActivityDb.class.getSimpleName();
    private final DatabaseReference database;

    public LocationTemplateActivityDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void addPlace(Place place) {
        String placeKey = database
                .child("Places")
                .push()
                .getKey();
        place.setKey(placeKey);
        database.child("Places")
                .child(place.getKey())
                .setValue(place)
                .addOnSuccessListener(unused -> {
                    updatePointsOnPlaceAdded();
                    EventBus.getDefault().post(new LocationTemplateActivityEvent.PlaceAdded());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    private void updatePointsOnPlaceAdded() {
        //Every user will get +5 pts for adding a marker on the map
        long markerAddPts = 5;
        Map<String, Object> updates = new HashMap<>();
        updates.put("Users/" + AppData.user.getKey() + "/points", ServerValue.increment(markerAddPts));
        updates.put("Leaderboards/" + AppData.user.getKey() + "/points", ServerValue.increment(markerAddPts));
        database.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    AppData.user.setPoints(AppData.user.getPoints() + markerAddPts);
                    EventBus.getDefault().post(new LocationTemplateActivityEvent.PlaceAddedPointsUpdated());
                })
                .addOnFailureListener(error -> Log.d(TAG, error.getMessage()));
    }
}
