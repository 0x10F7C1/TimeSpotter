package com.example.timespotter.DbContexts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.TableItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class TableFragmentDb {
    private final DatabaseReference database;

    public TableFragmentDb() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    public void loadTableEntries() {
        database
                .child("Excluded markers")
                .child(AppData.user.getKey())
                .child("places")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> excludedPlacesKeys = new ArrayList<>((int) snapshot.getChildrenCount());
                    for (DataSnapshot data : snapshot.getChildren()) {
                        excludedPlacesKeys.add(data.getKey());
                    }
                    filterTableEntries(excludedPlacesKeys);
                });
    }

    private void filterTableEntries(List<String> excludedPlacesKeys) {
        database
                .child("Places")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Place place = snapshot.getValue(Place.class);
                        TableItem entry;
                        if (!excludedPlacesKeys.contains(snapshot.getKey())) {
                            entry = new TableItem(place.getCreatorUsername(),
                                    place.getName(),
                                    place.getPhone(),
                                    place.getWebsite(),
                                    place.getStartTime(),
                                    place.getCloseTime());

                            EventBus.getDefault().post(entry);

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
