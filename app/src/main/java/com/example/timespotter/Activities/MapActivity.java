package com.example.timespotter.Activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.Adapters.MarkerInfoAdapter;
import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.PlaceMarker;
import com.example.timespotter.DbContexts.MapActivityDb;
import com.example.timespotter.Events.LeaderboardFragmentEvent;
import com.example.timespotter.Events.MapActivityEvent;
import com.example.timespotter.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.permissionx.guolindev.PermissionX;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int EMPTY_STAR = 0;
    private static final int FILLED_STAR = 1;
    private static final float DEFAULT_ZOOM = 15f;
    private static final boolean FILTER_ON = true;
    private static final boolean FILTER_OFF = false;
    private static final int START_DATE_PICKER = 0;
    private static final int END_DATE_PICKER = 1;
    private static final int USER_POINTS_UPDATE = 2;
    private static final int CREATOR_POINTS_UPDATE = 2;
    private static int DATE_PICKER;
    private final List<Marker> _ActiveMarkers = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance();
    private final MapActivityDb mapActivityDb = new MapActivityDb();
    private boolean _LocationEnabled;
    private GoogleMap _GoogleMap;
    private FusedLocationProviderClient _FusedClient;
    private ImageView _AddPlace;
    private Marker _Marker;
    private ImageView _Rate;
    private AlertDialog _RateDialog;
    private ImageButton _PlaceNameStar, _PlaceTypeStar, _PlaceWebsiteStar, _PlacePhoneStar, _PlaceTimeStar;
    private Button _RateDialogBtn, _CloseDialogBtn;
    private AlertDialog _FilterDialog;
    private ImageView _FilterButton;
    private MaterialDatePicker<Long> _DatePicker;
    private int startDay, startMonth, startYear, endDay, endMonth, endYear;
    private int userPointUpdate = 0, creatorPointsUpdate = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
        getLocationPermission();
        mapActivityDb.loadMarkers(AppData.user);
    }

    private void bindViews() {
        _AddPlace = findViewById(R.id.add_place);
        _FilterButton = findViewById(R.id.filter_button);
        _Rate = findViewById(R.id.rate);
    }

    private void registerCallbackListeners() {
        _AddPlace.setOnClickListener(this::addPlace);
        _FilterButton.setOnClickListener(view -> {
            initFilterDialog();
            _FilterDialog.show();
        });
        _Rate.setOnClickListener(this::rateBtnOnClick);
    }

    private void rateBtnOnClick(View view) {
        if (_Marker != null && _Marker.isInfoWindowShown()) {
            initRateDialog();
            _RateDialog.show();
        } else {
            Toast.makeText(MapActivity.this, "Select a marker", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFilterDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_filter_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(customDialog);

        _FilterDialog = dialogBuilder.create();
        _FilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        CheckBox usernameCheckBox, typeCheckBox, radiusCheckBox, dateCheckBox;
        EditText usernameText, typeText, radiusText, startDateText, endDateText;
        Button filterBtn;

        startDateText = customDialog.findViewById(R.id.start_date_filter_text);
        endDateText = customDialog.findViewById(R.id.end_date_filter_text);
        usernameCheckBox = customDialog.findViewById(R.id.username_filter_checkbox);
        typeCheckBox = customDialog.findViewById(R.id.type_filter_checkbox);
        radiusCheckBox = customDialog.findViewById(R.id.radius_filter_checkbox);
        usernameText = customDialog.findViewById(R.id.username_filter_text);
        typeText = customDialog.findViewById(R.id.type_filter_text);
        radiusText = customDialog.findViewById(R.id.radius_filter_text);
        dateCheckBox = customDialog.findViewById(R.id.date_filter_checkbox);
        _DatePicker = MaterialDatePicker.Builder.datePicker().build();

        filterBtn = customDialog.findViewById(R.id.filter_button);

        startDateText.setOnClickListener(view -> {
            DATE_PICKER = START_DATE_PICKER;
            _DatePicker.show(getSupportFragmentManager(), "DatePicker");
        });
        endDateText.setOnClickListener(view -> {
            DATE_PICKER = END_DATE_PICKER;
            _DatePicker.show(getSupportFragmentManager(), "Different fragment");
        });
        _DatePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            if (DATE_PICKER == START_DATE_PICKER) {
                startDay = calendar.get(Calendar.DAY_OF_MONTH);
                startMonth = calendar.get(Calendar.MONTH) + 1;
                startYear = calendar.get(Calendar.YEAR);
                startDateText.setText(startDay + "/" + startMonth + "/" + startYear);
            } else {
                endDay = calendar.get(Calendar.DAY_OF_MONTH);
                endMonth = calendar.get(Calendar.MONTH) + 1;
                endYear = calendar.get(Calendar.YEAR);
                endDateText.setText(endDay + "/" + endMonth + "/" + endYear);
            }
        });
        usernameCheckBox.setOnCheckedChangeListener((compoundButton, b) -> usernameText.setEnabled(compoundButton.isChecked()));
        typeCheckBox.setOnCheckedChangeListener((compoundButton, b) -> typeText.setEnabled(compoundButton.isChecked()));
        radiusCheckBox.setOnCheckedChangeListener((compoundButton, b) -> radiusText.setEnabled(compoundButton.isChecked()));
        dateCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            startDateText.setEnabled(compoundButton.isChecked());
            endDateText.setEnabled(compoundButton.isChecked());
        });
        filterBtn.setOnClickListener(view -> {
            boolean usernameFilter, typeFilter, radiusFilter, dateRangeFilter;
            usernameFilter = usernameCheckBox.isChecked();
            typeFilter = typeCheckBox.isChecked();
            radiusFilter = radiusCheckBox.isChecked();
            dateRangeFilter = dateCheckBox.isChecked();
            double radius = radiusFilter ? Double.parseDouble(radiusText.getText().toString()) : 0;
            filter(usernameFilter, typeFilter, radiusFilter, dateRangeFilter, usernameText.getText().toString(), typeText.getText().toString(), radius);
        });
    }

    private void filter(boolean usernameFilter, boolean typeFilter, boolean radiusFilter, boolean dateRangeFilter, String username, String type, double radius) {
        _FusedClient.getLastLocation().addOnSuccessListener(location -> {
            Marker marker;
            LatLng currLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("Trenutan broj markera na mapi je: ", String.valueOf(_ActiveMarkers.size()));
            for (int i = 0; i < _ActiveMarkers.size(); i++) {
                marker = _ActiveMarkers.get(i);
                System.out.println("Broj markera je " + _ActiveMarkers.size());
                boolean showMarker = toFilterByUsername(marker, username, usernameFilter)
                        && toFilterByType(marker, type, typeFilter)
                        && toFilterByRadius(currLatLng, marker.getPosition(), radiusFilter, radius)
                        && toFilterByDateRange(marker, dateRangeFilter);
                System.out.println("Show marker vraca: " + showMarker);
                marker.setVisible(showMarker);
            }
        });
    }

    private boolean toFilterByUsername(Marker marker, String creatorFilter, boolean filter) {
        boolean showMarker = false;
        String creator = ((Place) marker.getTag()).getCreatorUsername();
        if (filter == FILTER_ON) {
            if (creator.equals(creatorFilter)) {
                showMarker = true;
            }
        } else {
            showMarker = true;
        }
        return showMarker;
    }

    private boolean toFilterByType(Marker marker, String typeFilter, boolean filter) {
        boolean showMarker = false;
        String type = ((Place) marker.getTag()).getType();
        if (filter == FILTER_ON) {
            if (type.equals(typeFilter)) {
                showMarker = true;
            }
        } else {
            showMarker = true;
        }
        return showMarker;
    }

    private boolean toFilterByRadius(LatLng currLocation, LatLng marker, boolean filter, double radius) {
        boolean showMarker = false;
        if (filter == FILTER_ON) {
            if (calculateDistance(currLocation, marker) < radius) {
                showMarker = true;
            }
        } else {
            showMarker = true;
        }
        return showMarker;
    }

    private boolean toFilterByDateRange(Marker marker, boolean filter) {
        boolean showMarker = false;
        Place place = (Place) marker.getTag();
        int day = place.getDay(), month = place.getMonth(), year = place.getYear();
        int startNumOfMonths = (startYear - 1) * 12 + startMonth;
        int endNumOfMonths = (endYear - 1) * 12 + endMonth;
        int placeNumOfMonths = (year - 1) * 12 + month;

        if (filter == FILTER_ON) {
            if (placeNumOfMonths > startNumOfMonths && placeNumOfMonths < endNumOfMonths) {
                showMarker = true;
            } else if (placeNumOfMonths == startNumOfMonths && placeNumOfMonths == endNumOfMonths && day > startDay && day < endDay) {
                showMarker = true;
            } else if (placeNumOfMonths > startNumOfMonths && placeNumOfMonths == endNumOfMonths && day < endDay) {
                showMarker = true;
            } else if (placeNumOfMonths < endNumOfMonths && placeNumOfMonths == startNumOfMonths && day > startDay) {
                showMarker = true;
            }
        } else {
            showMarker = true;
        }
        return showMarker;
    }

    private double calculateDistance(LatLng currLocation, LatLng otherLocation) {
        double currLatRad = Math.toRadians(currLocation.latitude);
        double currLonRad = Math.toRadians(currLocation.longitude);
        double otherLatRad = Math.toRadians(otherLocation.latitude);
        double otherLonRad = Math.toRadians(otherLocation.longitude);

        final double earthRadius = 6371000;

        double dLat = otherLatRad - currLatRad;
        double dLon = otherLonRad - currLonRad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(currLatRad) * Math.cos(otherLatRad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private void initRateDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_rate_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(customDialog);

        _RateDialog = dialogBuilder.create();
        _RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        _PlaceNameStar = customDialog.findViewById(R.id.place_name_dialog_star);
        _PlaceNameStar.setTag(EMPTY_STAR);
        _PlaceTypeStar = customDialog.findViewById(R.id.place_type_dialog_star);
        _PlaceTypeStar.setTag(EMPTY_STAR);
        _PlaceWebsiteStar = customDialog.findViewById(R.id.place_website_dialog_star);
        _PlaceWebsiteStar.setTag(EMPTY_STAR);
        _PlacePhoneStar = customDialog.findViewById(R.id.place_phone_dialog_star);
        _PlacePhoneStar.setTag(EMPTY_STAR);
        _PlaceTimeStar = customDialog.findViewById(R.id.place_time_dialog_star);
        _PlaceTimeStar.setTag(EMPTY_STAR);
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

        Place place = (Place) _Marker.getTag();
        //_Marker.nullifyMarker();
        _Marker = null;

        mapActivityDb.updateUserPoints(2);
        mapActivityDb.updatePlaceCreatorPoints(place.getCreatorKey(), starCount);
        mapActivityDb.excludeUserMarker(place.getKey());
    }

    private void addPlace(View view) {
        Task<Location> locationTask = _FusedClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location currentLocation = locationTask.getResult();
                Intent intent = new Intent(MapActivity.this, LocationTemplateActivity.class);
                //intent.putExtra("user", user);
                intent.putExtra("longitude", currentLocation.getLongitude());
                intent.putExtra("latitude", currentLocation.getLatitude());
                //intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
            }
        });
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            Log.d(TAG, "Getting map object");
            _GoogleMap = googleMap;
            _GoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            _GoogleMap.setInfoWindowAdapter(new MarkerInfoAdapter(MapActivity.this));
            _GoogleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                _Marker = marker;
                return true;
            });
            Log.d(TAG, "Map object acquired");

            if (_LocationEnabled) {
                getDeviceLocation();
                _GoogleMap.setMyLocationEnabled(true);
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
        if (PermissionX.isGranted(this, FINE_LOCATION) && PermissionX.isGranted(this, COARSE_LOCATION)) {
            _LocationEnabled = true;
            initMap();
        } else {
            PermissionX.init(this)
                    .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            _LocationEnabled = true;
                            initMap();
                        }
                    });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMarkerAddEvent(Place place) {
        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
        /*BitmapDescriptor markerIcon = null;
        if (place.getType().equals("Library")) {
            markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.library_marker);
        } else if (place.getType().equals("Caffe")) {
            markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.cafe_marker);
        } else if (place.getType().equals("Hospital")) {
            System.out.println("ovo je ok");
            markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.hospital_marker);
        } else if (place.getType().equals("Pizzeria")) {
            markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.pizzeria_marker);
        }*/
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(place.getName())
                .icon(null);

        Marker marker = _GoogleMap.addMarker(markerOptions);
        marker.setTag(place);
        _Marker = marker;
        _ActiveMarkers.add(marker);
        Log.d("Marker dodat", "Marker id " + place.getKey());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserPointsUpdate(LeaderboardFragmentEvent.UserPointsUpdate result) {
        userPointUpdate++;
        if (userPointUpdate == USER_POINTS_UPDATE) {
            userPointUpdate = 0;
            if (creatorPointsUpdate == CREATOR_POINTS_UPDATE) {
                creatorPointsUpdate = 0;
                Log.d(TAG, "Points are updated for User and Creator");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCreatorPointsUpdate(LeaderboardFragmentEvent.UserPointsUpdate result) {
        creatorPointsUpdate++;
        if (creatorPointsUpdate == CREATOR_POINTS_UPDATE) {
            creatorPointsUpdate = 0;
            if (userPointUpdate == USER_POINTS_UPDATE) {
                userPointUpdate = 0;
                Log.d(TAG, "Points are updated for User and Creator");
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserMarkerExcluded(MapActivityEvent.UserMarkerExcluded result) {
        Log.d(TAG, "Marker excluded for user");
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println(TAG + " " + "Brisem se!");
        super.onDestroy();
    }
}