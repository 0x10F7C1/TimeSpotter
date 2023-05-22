package com.example.timespotter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MapActivityDb {
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
                        if (!place.getCreator().equals(user.getUsername()) && !excludedPlacesKeys.contains(snapshot.getKey())) {
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
}
