package com.example.timespotter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DbContexts.LocationTemplateActivityDb;
import com.example.timespotter.Events.LocationTemplateActivityEvent.PlaceAdded;
import com.example.timespotter.Events.LocationTemplateActivityEvent.PlaceAddedPointsUpdated;
import com.example.timespotter.R;
import com.example.timespotter.UserDb;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDate;

public class LocationTemplateActivity extends AppCompatActivity {
    private static final String TAG = LocationTemplateActivity.class.getSimpleName();
    private final LocationTemplateActivityDb locationTemplateActivityDb = new LocationTemplateActivityDb();
    private TextInputLayout placeName, placeWebsite, placePhoneNum;
    private ImageButton startDateBtn, closeDateBtn;
    private TextView startDateText, closeDateText;
    private MaterialButton createBtn, cancelBtn;
    private AutoCompleteTextView materialSpinner;
    private int selectedSpinnerItem = -1;
    private MaterialTimePicker timePicker;
    private PlaceAddedPointsUpdated pointsUpdated;
    private PlaceAdded placeAdded;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_template);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        placeName = findViewById(R.id.place_name);
        placeWebsite = findViewById(R.id.place_website);
        placePhoneNum = findViewById(R.id.place_phone);
        startDateBtn = findViewById(R.id.start_date);
        closeDateBtn = findViewById(R.id.close_date);
        createBtn = findViewById(R.id.create);
        cancelBtn = findViewById(R.id.cancel);
        startDateText = findViewById(R.id.start_date_text);
        closeDateText = findViewById(R.id.close_date_text);
        materialSpinner = findViewById(R.id.material_spinner);
        String[] spinnerItems = getResources().getStringArray(R.array.place_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                spinnerItems
        );

        materialSpinner.setAdapter(adapter);
    }

    private void registerCallbackListeners() {
        createBtn.setOnClickListener(this::createBtnOnClick);
        cancelBtn.setOnClickListener(this::cancelBtnOnClick);
        startDateBtn.setOnClickListener(this::selectTimeOnClick);
        closeDateBtn.setOnClickListener(this::selectTimeOnClick);
        materialSpinner.setOnItemClickListener((adapterView, view, i, l) -> selectedSpinnerItem = i);
    }

    private void createBtnOnClick(View view) {
        if (areFieldsEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.progress_bar_dialog, null));
        builder.setCancelable(false);

        progressDialog = builder.create();
        progressDialog.show();

        String name, type, website, phone, startTime, closeTime;
        name = placeName.getEditText().getText().toString();
        type = getResources().getStringArray(R.array.place_types)[selectedSpinnerItem];
        website = placeWebsite.getEditText().getText().toString();
        phone = placePhoneNum.getEditText().getText().toString();
        startTime = startDateText.getText().toString();
        closeTime = closeDateText.getText().toString();

        LocalDate currentDate = LocalDate.now();
        int day = currentDate.getDayOfMonth();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        Place place = new Place("",
                name,
                type,
                website,
                phone,
                startTime,
                closeTime,
                getIntent().getDoubleExtra("latitude", 0),
                getIntent().getDoubleExtra("longitude", 0),
                day,
                month,
                year,
                AppData.user.getKey(),
                AppData.user.getUsername());
        locationTemplateActivityDb.addPlace(place);
        new UserDb().updatePlaceCreatorSubmissions();
    }

    private boolean areFieldsEmpty() {
        return selectedSpinnerItem == -1
                || TextUtils.isEmpty(placeName.getEditText().getText())
                || TextUtils.isEmpty(placeWebsite.getEditText().getText())
                || TextUtils.isEmpty(placePhoneNum.getEditText().getText())
                || startDateText.getText().toString().equals("Open time")
                || closeDateText.getText().toString().equals("Close time");
    }

    private void cancelBtnOnClick(View view) {
        finish();
    }

    private void selectTimeOnClick(View view) {
        String titleText = "";
        int hour;
        TextView timeText;
        if (view.getId() == R.id.start_date) {
            titleText = "Select opening time";
            hour = 8;
            timeText = startDateText;
        } else {
            titleText = "Select closing time";
            hour = 20;
            timeText = closeDateText;
        }
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(0)
                .setTitleText(titleText)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build();
        timePicker.addOnPositiveButtonClickListener(view2 -> {
            String hourText, minuteText;
            if (timePicker.getHour() < 10) {
                hourText = "0" + timePicker.getHour();
            } else {
                hourText = "" + timePicker.getHour();
            }
            if (timePicker.getMinute() < 10) {
                minuteText = "0" + timePicker.getMinute();
            } else {
                minuteText = "" + timePicker.getMinute();
            }
            timeText.setText(hourText + ":" + minuteText);
        });
        timePicker.show(getSupportFragmentManager(), "TAG");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaceAdded(PlaceAdded result) {
        placeAdded = result;
        if (pointsUpdated != null) {
            placeAdded = null;
            pointsUpdated = null;
            progressDialog.dismiss();
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaceAddPointsUpdated(PlaceAddedPointsUpdated result) {
        pointsUpdated = result;
        if (placeAdded != null) {
            pointsUpdated = null;
            placeAdded = null;
            progressDialog.dismiss();
            Intent intent = new Intent(LocationTemplateActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}