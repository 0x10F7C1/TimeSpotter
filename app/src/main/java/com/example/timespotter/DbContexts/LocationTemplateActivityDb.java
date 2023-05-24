package com.example.timespotter.DbContexts;

import android.net.Uri;
import android.util.Log;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.Events.LocationTemplateActivityEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class LocationTemplateActivityDb {
    private static final String TAG = LocationTemplateActivityDb.class.getSimpleName();
    private final DatabaseReference database;
    private final StorageReference storage;

    public LocationTemplateActivityDb() {
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void addPlace(Place place, Uri imageUri) {
        String imageId = UUID.randomUUID().toString();
        storage
                .child(imageId)
                .putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    getPlaceImageUrl(place, taskSnapshot);
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    private void getPlaceImageUrl(Place place, UploadTask.TaskSnapshot taskSnapshot) {
        taskSnapshot
                .getStorage()
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    place.setImageUri(uri.toString());
                    uploadPlace(place);
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }

    private void uploadPlace(Place place) {
        String placeKey = database
                .child("Places")
                .push()
                .getKey();
        place.setKey(placeKey);
        database.child("Places")
                .child(place.getKey())
                .setValue(place)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new LocationTemplateActivityEvent.PlaceAdded());
                })
                .addOnFailureListener(error -> {
                    Log.d(TAG, error.getMessage());
                });
    }
}
