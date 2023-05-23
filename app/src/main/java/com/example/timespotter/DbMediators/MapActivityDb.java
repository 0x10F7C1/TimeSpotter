package com.example.timespotter.DbMediators;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.Events.MapActivityEvent;
import com.example.timespotter.GeneralActivities.HomeScreenActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MapActivityDb {
    private static final String TAG = MapActivityDb.class.getSimpleName();
    private final DatabaseReference database;

    public MapActivityDb() {
        database = FirebaseDatabase.getInstance().getReference();

    }

    public void loadMarkers(User user) {
        database
                .child("Excluded markers")
                .child(user.getKey())
                .child("places")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> excludedPlacesKeys = new ArrayList<>((int) snapshot.getChildrenCount());
                    for (DataSnapshot data : snapshot.getChildren()) {
                        excludedPlacesKeys.add(data.getKey());
                    }
                    filterMarkers(user, excludedPlacesKeys);
                });
    }

    private void filterMarkers(User user, List<String> excludedPlacesKeys) {
        database
                .child("Places")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Place place = snapshot.getValue(Place.class);
                        if (!place.getCreatorKey().equals(user.getKey()) && !excludedPlacesKeys.contains(snapshot.getKey())) {
                            EventBus.getDefault().post(place);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void excludeUserMarker(String placeKey) {
        database
                .child("Excluded markers")
                .child(HomeScreenActivity.user.getKey())
                .child("places")
                .child(placeKey)
                .setValue(true)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MapActivityEvent.UserMarkerExcluded());
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, e.getMessage());
                });
    }

    public void updateUserPoints(long pts) {
        HomeScreenActivity.user.setPoints(HomeScreenActivity.user.getPoints() + pts);
        database
                .child("Leaderboards")
                .child(HomeScreenActivity.user.getKey())
                .child("points")
                .setValue(HomeScreenActivity.user.getPoints())
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MapActivityEvent.UserPointsUpdate());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
        database
                .child("Users")
                .child(HomeScreenActivity.user.getKey())
                .child("points")
                .setValue(HomeScreenActivity.user.getPoints())
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MapActivityEvent.UserPointsUpdate());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    public void updatePlaceCreatorPoints(String userKey, long pts) {
        database
                .child("Users")
                .child(userKey)
                .child("points")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Long points = dataSnapshot.getValue(Long.class);
                    points += pts;
                    updateCreatorPoints(userKey, points);
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    private void updateCreatorPoints(String userKey, long pts) {
        database
                .child("Users")
                .child(userKey)
                .child("points")
                .setValue(pts)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MapActivityEvent.CreatorPointsUpdate());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
        database
                .child("Leaderboards")
                .child(userKey)
                .child("points")
                .setValue(pts)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MapActivityEvent.CreatorPointsUpdate());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
