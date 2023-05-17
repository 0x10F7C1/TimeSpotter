package com.example.timespotter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timespotter.Adapters.MarkerInfoAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int EMPTY_STAR = 0;
    private static final int FILLED_STAR = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final float DEFAULT_ZOOM = 15f;
    private boolean _LocationEnabled;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private GoogleMap _GoogleMap;
    private FusedLocationProviderClient _FusedClient;
    private ImageView _AddPlace;
    private Marker _Marker;
    private ImageView _Rate;
    private AlertDialog _RateDialog;
    private ImageButton _PlaceNameStar, _PlaceTypeStar, _PlaceWebsiteStar, _PlacePhoneStar, _PlaceTimeStar;
    private Button _RateDialogBtn, _CloseDialogBtn;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        username = getIntent().getStringExtra("username");
        initRateDialog();
        _AddPlace = findViewById(R.id.add_place);

        //ovo ce biti add button posle
        _AddPlace.setOnClickListener(view -> {
            addPlace();
        });


        _Rate = findViewById(R.id.rate);
        _Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_Marker != null && _Marker.isInfoWindowShown()) {
                    _RateDialog.show();
                }
                else {
                    Toast.makeText(MapActivity.this, "Select a marker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getLocationPermission();
        loadMarkers();
    }

    private void initRateDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(customDialog);

        _RateDialog = dialogBuilder.create();
        _RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        _PlaceNameStar = customDialog.findViewById(R.id.place_name_dialog_star);
        _PlaceNameStar.setTag(new Integer(EMPTY_STAR));
        _PlaceTypeStar = customDialog.findViewById(R.id.place_type_dialog_star);
        _PlaceTypeStar.setTag(new Integer(EMPTY_STAR));
        _PlaceWebsiteStar = customDialog.findViewById(R.id.place_website_dialog_star);
        _PlaceWebsiteStar.setTag(new Integer(EMPTY_STAR));
        _PlacePhoneStar = customDialog.findViewById(R.id.place_phone_dialog_star);
        _PlacePhoneStar.setTag(new Integer(EMPTY_STAR));
        _PlaceTimeStar = customDialog.findViewById(R.id.place_time_dialog_star);
        _PlaceTimeStar.setTag(new Integer(EMPTY_STAR));
        _RateDialogBtn = customDialog.findViewById(R.id.rate_dialog_btn);
        _CloseDialogBtn = customDialog.findViewById(R.id.close_dialog_btn);

        _RateDialogBtn.setOnClickListener(this::rateDialogOnClick);
        _CloseDialogBtn.setOnClickListener(view -> {_RateDialog.cancel();});

        _PlaceNameStar.setOnClickListener(this::dialogStarOnClick);
        _PlaceTypeStar.setOnClickListener(this::dialogStarOnClick);
        _PlaceWebsiteStar.setOnClickListener(this::dialogStarOnClick);
        _PlacePhoneStar.setOnClickListener(this::dialogStarOnClick);
        _PlaceTimeStar.setOnClickListener(this::dialogStarOnClick);
    }
    private void dialogStarOnClick(View view) {
        ImageButton btn = (ImageButton) view;
        if (btn.getTag().equals(EMPTY_STAR)) {
            btn.setImageResource(R.drawable.ic_filled_star);
            btn.setTag(FILLED_STAR);
        }
        else {
            btn.setImageResource(R.drawable.empty_star);
            btn.setTag(EMPTY_STAR);
        }
    }
    private void rateDialogOnClick(View view) {
        int starCount = 0;
        starCount += (Integer)_PlaceNameStar.getTag();
        starCount += (Integer)_PlaceTypeStar.getTag();
        starCount += (Integer)_PlaceWebsiteStar.getTag();
        starCount += (Integer)_PlacePhoneStar.getTag();
        starCount += (Integer)_PlaceTimeStar.getTag();

        final int stars = starCount;

        Place place = (Place)_Marker.getTag();
        //DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Users")
                .child(username)
                .child("points")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        long result = task.getResult().getValue(Long.class);
                        result += stars;
                        database.child("Users")
                                .child(username)
                                .child("points")
                                .setValue(result);
                        database.child("Leaderboards")
                                .child(username)
                                .child("points")
                                .setValue(result);
                    }
                });
        database.child("Users")
                .child(place.getCreator())
                .child("points")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        long result = task.getResult().getValue(Long.class);
                        result += 2;
                        database.child("Users")
                                .child(place.getCreator())
                                .child("points")
                                .setValue(result);
                        database.child("Leaderboards")
                                .child(username)
                                .child("points")
                                .setValue(result);
                    }
                });
    }

    //funkcija za dodavanje mesta na mapi u odnosu na trenutnu lokaciju korisnika
    private void addPlace() {
        Task<Location> locationTask = _FusedClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Location currentLocation = locationTask.getResult();
               Intent intent = new Intent(MapActivity.this, LocationTemplate.class);
               intent.putExtra("longitude", currentLocation.getLongitude());
               intent.putExtra("latitude", currentLocation.getLatitude());
               intent.putExtra("username", getIntent().getStringExtra("username"));
               startActivity(intent);
           }
        });
    }

    private void loadMarkers() {
        database.child("Places")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Place place = snapshot.getValue(Place.class);
                        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(place.getName());

                        Marker marker = _GoogleMap.addMarker(markerOptions);
                        marker.setTag(place);
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
    private void initMap() {
        Log.d(TAG, "Setting up a mapFragment");
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d(TAG, "Getting map object");
                _GoogleMap = googleMap;
                _GoogleMap.setInfoWindowAdapter(new MarkerInfoAdapter(MapActivity.this));
                _GoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        marker.showInfoWindow();
                        _Marker = marker;
                        return true;
                    }
                });
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
                   _GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                  //moveCamera(latLng, DEFAULT_ZOOM, "My location");
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