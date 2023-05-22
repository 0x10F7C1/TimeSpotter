package com.example.timespotter.Repositories;
import com.example.timespotter.CustomQueue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.PlaceMarker;
import com.example.timespotter.DataModels.Result;
import com.example.timespotter.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PlaceRepository {
    private MutableLiveData<Result<Place>> _Place;
    private MutableLiveData<Result<Void>> excludeMarker;
    private DatabaseReference database;
    private MutableLiveData<Result<CustomQueue>> _Red = new MutableLiveData<>();
    private CustomQueue red = new CustomQueue();

    public PlaceRepository() {
        excludeMarker = new MutableLiveData<>();
        _Place = new MutableLiveData<>();
        database = FirebaseDatabase.getInstance().getReference();
    }
    public MutableLiveData<Result<Place>> getPlace() {
        return _Place;
    }
    public MutableLiveData<Result<Void>> getExcludeMarker() {
        return excludeMarker;
    }
    public MutableLiveData<Result<CustomQueue>> getRed() {return _Red;}
    public void excludeUserMarker(String username, String placeKey) {
        final Result<Void> result = new Result<>();
        database
                .child("Excluded markers")
                .child(username)
                .child("places")
                .child(placeKey)
                .setValue(true)
                .addOnSuccessListener(unused -> {
                    result.setOperationSuccess(Result.OPERATION_SUCCESS);
                    excludeMarker.postValue(result);
                })
                .addOnFailureListener(e -> {
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(e);
                    excludeMarker.postValue(result);
                });
    }
    public void loadMarkers(String username) {
        database
                .child("Excluded markers")
                .child(username)
                .child("places")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> excludedPlacesKeys = new ArrayList<>((int) snapshot.getChildrenCount());
                    for (DataSnapshot data : snapshot.getChildren()) {
                        excludedPlacesKeys.add(data.getKey());
                    }
                    filterMarkers(username, excludedPlacesKeys);
                })
                .addOnFailureListener(e -> {
                    final Result<List<Place>> result = new Result<>();
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(e);
                });
    }
    private void filterMarkers(String username, List<String> excludedPlacesKeys) {
        final Result<Place> result = new Result<>();
        final Result<CustomQueue> proba = new Result<>();
        database
                .child("Places")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Place place = snapshot.getValue(Place.class);
                        if (!place.getCreator().equals(username) && !excludedPlacesKeys.contains(snapshot.getKey())) {
                            //result.setOperationSuccess(Result.OPERATION_SUCCESS).setValue(place);
                            proba.setOperationSuccess(Result.OPERATION_SUCCESS);
                            Log.d("Ide na mapu", "Marker ide");
                            //_Place.getValue().getValue().
                            red.addValue(place);
                            proba.setValue(red);
                            _Red.postValue(proba);
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
