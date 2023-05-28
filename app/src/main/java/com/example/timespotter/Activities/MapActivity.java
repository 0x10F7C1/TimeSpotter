package com.example.timespotter.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.timespotter.Adapters.MarkerInfoAdapter;
import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.RateStar;
import com.example.timespotter.DbContexts.MapActivityDb;
import com.example.timespotter.Events.LeaderboardFragmentEvent;
import com.example.timespotter.Events.MapActivityEvent;
import com.example.timespotter.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
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
    private Button _RateDialogBtn, _CloseDialogBtn;
    private AlertDialog _FilterDialog;
    private ImageView _FilterButton;
    private MaterialDatePicker<Long> _DatePicker;
    private AutoCompleteTextView materialSpinner;
    private int startDay, startMonth, startYear, endDay, endMonth, endYear;
    private int userPointUpdate = 0, creatorPointsUpdate = 0;
    private RateStar nameStar, typeStar, websiteStar, phoneStar, timeStar;

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
            if (((Place)_Marker.getTag()).getCreatorUsername().equals(AppData.user.getUsername())) {
                Toast.makeText(MapActivity.this, "You cannot rate your markers", Toast.LENGTH_SHORT).show();
            }
            else {
                initRateDialog();
                _RateDialog.show();
            }
        } else {
            Toast.makeText(MapActivity.this, "Select a marker", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFilterDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.custom_filter_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(customDialog);

        _FilterDialog = dialogBuilder.create();
        _FilterDialog.setCancelable(false);
        _FilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputLayout usernameText, typeText, radiusText, startTimeText, closeTimeText;
        MaterialButton filterBtn, cancelBtn;
        materialSpinner = customDialog.findViewById(R.id.filter_material_spinner);
        String[] spinnerItems = getResources().getStringArray(R.array.filter_place_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                spinnerItems
        );

        materialSpinner.setAdapter(adapter);
        usernameText = customDialog.findViewById(R.id.filter_username_layout);
        radiusText = customDialog.findViewById(R.id.filter_radius_layout);
        startTimeText = customDialog.findViewById(R.id.filter_start_time_layout);
        closeTimeText = customDialog.findViewById(R.id.filter_close_time_layout);

        _DatePicker = MaterialDatePicker.Builder.datePicker().build();

        filterBtn = customDialog.findViewById(R.id.filter_button);
        cancelBtn = customDialog.findViewById(R.id.cancel_button);

        startTimeText.setOnClickListener(view -> {
            DATE_PICKER = START_DATE_PICKER;
            _DatePicker.show(getSupportFragmentManager(), "DatePicker");
        });
        closeTimeText.setOnClickListener(view -> {
            DATE_PICKER = END_DATE_PICKER;
            _DatePicker.show(getSupportFragmentManager(), "Different fragment");
        });

        _DatePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            if (DATE_PICKER == START_DATE_PICKER) {
                startDay = calendar.get(Calendar.DAY_OF_MONTH);
                startMonth = calendar.get(Calendar.MONTH) + 1;
                startYear = calendar.get(Calendar.YEAR);
                startTimeText.getEditText().setText(startDay + "/" + startMonth + "/" + startYear);
            } else {
                endDay = calendar.get(Calendar.DAY_OF_MONTH);
                endMonth = calendar.get(Calendar.MONTH) + 1;
                endYear = calendar.get(Calendar.YEAR);
                closeTimeText.getEditText().setText(endDay + "/" + endMonth + "/" + endYear);
            }
        });

        filterBtn.setOnClickListener(view -> {
            boolean usernameFilter, typeFilter, radiusFilter, dateRangeFilter;
            usernameFilter = !(TextUtils.isEmpty(usernameText.getEditText().getText()));
            typeFilter = !(materialSpinner.getText().toString().equals("None") ||
                    TextUtils.isEmpty(materialSpinner.getText()));
            radiusFilter = !(TextUtils.isEmpty(radiusText.getEditText().getText()));
            dateRangeFilter = !(TextUtils.isEmpty(startTimeText.getEditText().getText()))
                    && !(TextUtils.isEmpty(closeTimeText.getEditText().getText()));

            String username = usernameText.getEditText().getText().toString();
            String type = materialSpinner.getText().toString();
            double radius = radiusText.getEditText().getText().toString().isEmpty() ? 0 : Double.parseDouble(radiusText
                    .getEditText().getText().toString());

            filter(usernameFilter, typeFilter, radiusFilter, dateRangeFilter, username, type, radius);
            _FilterDialog.dismiss();
        });

        cancelBtn.setOnClickListener(view -> {
            _FilterDialog.dismiss();
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
        _RateDialog.setCancelable(false);
        _RateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        nameStar = new RateStar();
        typeStar = new RateStar();
        websiteStar = new RateStar();
        phoneStar = new RateStar();
        timeStar = new RateStar();

        ImageButton greenName, redName, greenType, redType, greenWebsite, redWebsite, greenPhone, redPhone, greenTime, redTime;

        greenName = customDialog.findViewById(R.id.name_thumbs_up);
        greenName.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redName = customDialog.findViewById(R.id.name_thumbs_down);

        greenType = customDialog.findViewById(R.id.type_thumbs_up);
        greenType.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redType = customDialog.findViewById(R.id.type_thumbs_down);

        greenWebsite = customDialog.findViewById(R.id.website_thumbs_up);
        greenWebsite.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redWebsite = customDialog.findViewById(R.id.website_thumbs_down);

        greenPhone = customDialog.findViewById(R.id.phone_thumbs_up);
        greenPhone.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redPhone = customDialog.findViewById(R.id.phone_thumbs_down);

        greenTime = customDialog.findViewById(R.id.time_thumbs_up);
        greenTime.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redTime = customDialog.findViewById(R.id.time_thumbs_down);

        //on clicks
        greenName.setOnClickListener(view -> setGreenButton(greenName, redName, nameStar));
        redName.setOnClickListener(view -> setRedButton(greenName, redName, nameStar));

        greenType.setOnClickListener(view -> setGreenButton(greenType, redType, typeStar));
        redType.setOnClickListener(view -> setRedButton(greenType, redType, typeStar));

        greenWebsite.setOnClickListener(view -> setGreenButton(greenWebsite, redWebsite, websiteStar));
        redWebsite.setOnClickListener(view -> setRedButton(greenWebsite, redWebsite, websiteStar));

        greenPhone.setOnClickListener(view -> setGreenButton(greenPhone, redPhone, phoneStar));
        redPhone.setOnClickListener(view -> setRedButton(greenPhone, redPhone, phoneStar));

        greenTime.setOnClickListener(view -> setGreenButton(greenTime, redTime, timeStar));
        redTime.setOnClickListener(view -> setRedButton(greenTime, redTime, timeStar));

        _RateDialogBtn = customDialog.findViewById(R.id.rate_dialog_btn);
        _CloseDialogBtn = customDialog.findViewById(R.id.close_dialog_btn);

        _RateDialogBtn.setOnClickListener(this::rateDialogOnClick);
        _CloseDialogBtn.setOnClickListener(view -> {
            _RateDialog.cancel();
        });
    }

    private void rateDialogOnClick(View view) {
        Place place = (Place) _Marker.getTag();
        _Marker.hideInfoWindow();
        _Marker = null;
        int starsCount = nameStar.value + typeStar.value + websiteStar.value + phoneStar.value + timeStar.value;
        Toast.makeText(this, String.valueOf(starsCount), Toast.LENGTH_SHORT).show();

        mapActivityDb.updateUserPoints(2);
        mapActivityDb.updatePlaceCreatorPoints(place.getCreatorKey(), starsCount);
        mapActivityDb.excludeUserMarker(place.getKey());
    }

    private void addPlace(View view) {
        Task<Location> locationTask = _FusedClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location currentLocation = locationTask.getResult();
                Intent intent = new Intent(MapActivity.this, LocationTemplateActivity.class);
                intent.putExtra("longitude", currentLocation.getLongitude());
                intent.putExtra("latitude", currentLocation.getLatitude());
                startActivity(intent);
                finish();
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
                } else {
                    Log.d(TAG, "Current location is null");
                }
            });
        }
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
        BitmapDescriptor markerIcon = null;
        if (place.getType().equals("Library")) {
            markerIcon = bitmapDescriptorFromVector(this, R.drawable.ic_library);
        } else if (place.getType().equals("Caffe")) {
            markerIcon = bitmapDescriptorFromVector(this, R.drawable.ic_cafe);
        } else if (place.getType().equals("Hospital")) {
            markerIcon = bitmapDescriptorFromVector(this, R.drawable.ic_hospital);
        } else if (place.getType().equals("Pizzeria")) {
            markerIcon = bitmapDescriptorFromVector(this, R.drawable.ic_pizzeria);
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(markerIcon);

        Marker marker = _GoogleMap.addMarker(markerOptions);
        System.out.println("Marker koji se dodaje ima ime " + place.getName());
        marker.setTag(place);
        _Marker = marker;
        _ActiveMarkers.add(marker);
        System.out.println("Marker phone no: " + place.getPhone());
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void setGreenButton(ImageButton greenButton, ImageButton redButton, RateStar star) {
        if (star.value == 0) star.value++;
        greenButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_green_tint));
        redButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_black_tint));
    }

    private void setRedButton(ImageButton greenButton, ImageButton redButton, RateStar star) {
        if (star.value == 1) star.value--;
        greenButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_black_tint));
        redButton.setBackgroundTintList(getResources().getColorStateList(R.color.holo_dark_red));
    }
}