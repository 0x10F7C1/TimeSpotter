package com.example.timespotter.GeneralActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.timespotter.Adapters.MarkerInfoAdapter;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.PlaceMarker;
import com.example.timespotter.DataModels.Result;
import com.example.timespotter.R;
import com.example.timespotter.ViewModels.MapActivityViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import java.util.ArrayList;
import java.util.List;
public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int EMPTY_STAR = 0;
    private static final int FILLED_STAR = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final float DEFAULT_ZOOM = 15f;
    private static final boolean FILTER_ON = true;
    private static final boolean FILTER_OFF = false;
    private boolean _LocationEnabled;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private GoogleMap _GoogleMap;
    private FusedLocationProviderClient _FusedClient;
    private ImageView _AddPlace;
    private PlaceMarker _Marker;
    private ImageView _Rate;
    private AlertDialog _RateDialog;
    private ImageButton _PlaceNameStar, _PlaceTypeStar, _PlaceWebsiteStar, _PlacePhoneStar, _PlaceTimeStar;
    private Button _RateDialogBtn, _CloseDialogBtn;
    private String _Username;
    private AlertDialog _FilterDialog;
    private ImageView _FilterButton;
    private final List<PlaceMarker> _ActiveMarkers = new ArrayList<>();
    private MapActivityViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        _Username = getIntent().getStringExtra("username");
        _AddPlace = findViewById(R.id.add_place);
        _FilterButton = findViewById(R.id.filter_button);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MapActivityViewModel.class);
        viewModel.getUserPoints().observe(this, new Observer<Result<Integer>>() {
            @Override
            public void onChanged(Result<Integer> integerResult) {
                if (integerResult.getStatus() == Result.OPERATION_SUCCESS) {
                    Toast.makeText(MapActivity.this, "Poeni ubaci u bazi", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MapActivity.this, integerResult.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        initFilterDialog();

        _FilterButton.setOnClickListener(view -> {
            _FilterDialog.show();
        });

        _AddPlace.setOnClickListener(view -> {
            addPlace();
        });


        _Rate = findViewById(R.id.rate);
        _Rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_Marker != null && _Marker.getMarker().isInfoWindowShown()) {
                    initRateDialog();
                    _RateDialog.show();
                } else {
                    Toast.makeText(MapActivity.this, "Select a marker", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getLocationPermission();
        loadMarkers();
    }
    private void initFilterDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_filter_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(customDialog);

        _FilterDialog = dialogBuilder.create();
        _FilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CheckBox usernameCheckBox, typeCheckBox;
        EditText usernameText, typeText;
        Button filterBtn;

        usernameCheckBox = customDialog.findViewById(R.id.username_filter_checkbox);
        typeCheckBox = customDialog.findViewById(R.id.type_filter_checkbox);
        usernameText = customDialog.findViewById(R.id.username_filter_text);
        typeText = customDialog.findViewById(R.id.type_filter_text);
        filterBtn = customDialog.findViewById(R.id.filter_button);
        usernameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    usernameText.setEnabled(true);
                }
                else {
                    usernameText.setEnabled(false);
                }
            }
        });
        typeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    typeText.setEnabled(true);
                }
                else {
                    typeText.setEnabled(false);
                }
            }
        });
        filterBtn.setOnClickListener(view -> {
            boolean usernameFilter = FILTER_OFF, typeFilter = FILTER_OFF;
            if (typeCheckBox.isChecked()) {
                Log.d("Type checkbox", typeText.getText().toString());
                typeFilter = FILTER_ON;
            }
            else {
                Log.d("Type checkbox", "off");
            }
            if (usernameCheckBox.isChecked()) {
                Log.d("username checkbox", usernameText.getText().toString());
                usernameFilter = FILTER_ON;
            }
            else {
                Log.d("username checkbox", "off");
            }
            filter(usernameFilter, typeFilter, usernameText.getText().toString(), typeText.getText().toString());
        });
    }

    private void filter(boolean usernameFilter, boolean typeFilter, String username, String type) {
        Marker marker;
        for (int i = 0; i < _ActiveMarkers.size(); i++) {
            marker = _ActiveMarkers.get(i).getMarker();
            marker.setVisible(toFilterByUsername(marker, username, usernameFilter)
            && toFilterByType(marker, type, typeFilter));
        }
    }
    private boolean toFilterByUsername(Marker marker, String creatorFilter, boolean filter) {
        boolean showMarker = false;
        String creator = ((Place)marker.getTag()).getCreator();
        if (filter == FILTER_ON) {
            if (creator.equals(creatorFilter)) {
                showMarker = true;
            }
        }
        else {
            showMarker = true;
        }
        return showMarker;
    }
    private boolean toFilterByType(Marker marker, String typeFilter, boolean filter) {
        boolean showMarker = false;
        String type = ((Place)marker.getTag()).getType();
        if (filter == FILTER_ON) {
            if (type.equals(typeFilter)) {
                showMarker = true;
            }
        }
        else {
            showMarker = true;
        }
        return showMarker;
    }
    private void initRateDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_rate_dialog, null);
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
        _CloseDialogBtn.setOnClickListener(view -> {
            _RateDialog.cancel();
        });

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
        } else {
            btn.setImageResource(R.drawable.empty_star);
            btn.setTag(EMPTY_STAR);
        }
    }
    private void rateDialogOnClick(View view) {
        int starCount = 0;
        starCount += (Integer) _PlaceNameStar.getTag();
        starCount += (Integer) _PlaceTypeStar.getTag();
        starCount += (Integer) _PlaceWebsiteStar.getTag();
        starCount += (Integer) _PlacePhoneStar.getTag();
        starCount += (Integer) _PlaceTimeStar.getTag();

        final int stars = starCount;

        Place place = (Place) _Marker.getMarker().getTag();
        _Marker.nullifyMarker();
        _Marker = null;

        /*database.child("Users")
                .child(_Username)
                .child("points")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        long result = task.getResult().getValue(Long.class);
                        result += 2;
                        database.child("Users")
                                .child(_Username)
                                .child("points")
                                .setValue(result);
                        database.child("Leaderboards")
                                .child(_Username)
                                .child("points")
                                .setValue(result);
                    }
                });*/
        viewModel.updateUserPoints(_Username, 2);
        viewModel.updateUserPoints(place.getCreator(), stars);
        /*database.child("Users")
                .child(place.getCreator())
                .child("points")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        long result = task.getResult().getValue(Long.class);
                        result += stars;
                        database.child("Users")
                                .child(place.getCreator())
                                .child("points")
                                .setValue(result);
                        database.child("Leaderboards")
                                .child(place.getCreator())
                                .child("points")
                                .setValue(result);
                    }
                });*/
        database.child("Excluded markers")
                .child(_Username)
                .child("places")
                .child(place.getKey())
                .setValue(true);
    }
    private void addPlace() {
        Task<Location> locationTask = _FusedClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location currentLocation = locationTask.getResult();
                Intent intent = new Intent(MapActivity.this, LocationTemplateActivity.class);
                intent.putExtra("longitude", currentLocation.getLongitude());
                intent.putExtra("latitude", currentLocation.getLatitude());
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
            }
        });
    }
    private void loadMarkers() {
        List<String> placesIds = new ArrayList<>();
        database.child("Excluded markers").child(_Username).child("places").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot snapshot = task.getResult();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            placesIds.add(data.getKey());
                        }
                        database.child("Places")
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        Place place = snapshot.getValue(Place.class);
                                        if (!place.getCreator().equals(_Username) && !placesIds.contains(snapshot.getKey())) {
                                            LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                                            BitmapDescriptor markerIcon = null;
                                            if (place.getType().equals("restoran")) {
                                                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.restaurant48px);
                                            }
                                            else if (place.getType().equals("biblioteka")) {
                                                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.library48px);
                                            }
                                            MarkerOptions markerOptions = new MarkerOptions()
                                                    .position(latLng)
                                                    .title(place.getName())
                                                    .icon(markerIcon);

                                            Marker marker = _GoogleMap.addMarker(markerOptions);
                                            marker.setTag(place);
                                            PlaceMarker placeMarker = new PlaceMarker(marker, _ActiveMarkers.size());
                                            _Marker = placeMarker;
                                            _ActiveMarkers.add(placeMarker);
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
                });
    }
    private void initMap() {
        Log.d(TAG, "Setting up a mapFragment");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d(TAG, "Getting map object");
                _GoogleMap = googleMap;
                _GoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                _GoogleMap.setInfoWindowAdapter(new MarkerInfoAdapter(MapActivity.this));
                _GoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        marker.showInfoWindow();
                        _Marker.setMarker(marker);
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
        if (_LocationEnabled) {
            Task<Location> location = _FusedClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Current location found");
                    Location currentLocation = task.getResult();

                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    _GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    //moveCamera(latLng, DEFAULT_ZOOM, "My location");
                } else {
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
        } else {
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