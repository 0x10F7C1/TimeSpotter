package com.example.timespotter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class Discover extends Fragment {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;


    private GoogleMap _GoogleMap;
    private Context _Context;
    private boolean _LocationEnabled;
    private FusedLocationProviderClient _FusedClient;
    public Discover() {

    }

    public static Discover newInstance() {
        Discover fragment = new Discover();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_discover, container, false);
        _Context = getContext();
        getLocationPermissions();
        return itemView;
    }
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getParentFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(googleMap -> {
            _GoogleMap = googleMap;

            if (_LocationEnabled) {
                getDeviceLocation();
                _GoogleMap.setMyLocationEnabled(true);
            }
        });
    }
    private void getDeviceLocation() {
        _FusedClient = LocationServices.getFusedLocationProviderClient(_Context);
        if (_LocationEnabled) {
            Task<Location> locationTask = _FusedClient.getLastLocation();
            locationTask.addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Location currentLocation = task.getResult();

                   LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                   _GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
               }
            });

        }
    }
    private void getLocationPermissions() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(_Context, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(_Context, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            _LocationEnabled = true;
            initMap();
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        _LocationEnabled = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
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