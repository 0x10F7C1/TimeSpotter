package com.example.timespotter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final float DEFAULT_ZOOM = 15f;
    private boolean _LocationEnabled;
    private GoogleMap _GoogleMap;
    private FusedLocationProviderClient _FusedClient;
    private EditText search;
    private ImageView magnify;
    private ImageView gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        search = findViewById(R.id.search);
        magnify = findViewById(R.id.magnify);
        gps = findViewById(R.id.add_place);

        magnify.setOnClickListener(view -> {
            geoLocate();
        });

        //ovo ce biti add button posle
        gps.setOnClickListener(view -> {
            addPlace();
        });

        getLocationPermission();
    }

    private void addPlace() {
        /*Task<Location> locationTask = _FusedClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Location currentLocation = locationTask.getResult();
           }
        });*/
        Intent intent = new Intent(MapActivity.this, LocationTemplate.class);
        startActivity(intent);
    }
    private void geoLocate() {
        Log.d(TAG, "Geo locating..");

        String searchString = search.getText().toString();

        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e) {
            Log.d(TAG, "Exception " + e.getMessage());
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "Found location: " + address.toString());

            LatLng loc = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(loc, DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }
    private void initMap() {
        Log.d(TAG, "Setting up a mapFragment");
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d(TAG, "Getting map object");
                _GoogleMap = googleMap;
                Log.d(TAG, "Map object acquired");

                if (_LocationEnabled) {
                    getDeviceLocation();
                    _GoogleMap.setMyLocationEnabled(true);
                }
            }
        });
    }
    private void getDeviceLocation() {
        Log.d(TAG, "Getting devices current location");
        _FusedClient = LocationServices.getFusedLocationProviderClient(this);
        if (_FusedClient != null) {
            Log.d(TAG, "FusedClient acquired");
        }
        if (_LocationEnabled) {
            Task<Location> location = _FusedClient.getLastLocation();
            location.addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Log.d(TAG, "Current location found");
                   Location currentLocation = task.getResult();

                   LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                   moveCamera(latLng, DEFAULT_ZOOM, "My location");
               }
               else {
                   Log.d(TAG, "Current location is null");
               }
            });
        }

    }
    private void moveCamera(LatLng latLng, float zoom, String title) {
        _GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions marker = new MarkerOptions().position(latLng)
                .title(title);
        _GoogleMap.addMarker(marker);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            _LocationEnabled = true;
            initMap();
        }
        else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        _LocationEnabled = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            _LocationEnabled = false;
                            return;
                        }
                    }
                    _LocationEnabled = true;
                    initMap();
                }
        }
    }
}