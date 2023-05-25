package com.example.timespotter.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DbContexts.LocationTemplateActivityDb;
import com.example.timespotter.Events.LocationTemplateActivityEvent.PlaceAdded;
import com.example.timespotter.Events.LocationTemplateActivityEvent.PlaceAddedPointsUpdated;
import com.example.timespotter.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private TextInputLayout _PlaceName, _PlaceWebsite, _PlacePhoneNum;
    private ImageButton _StartDateBtn, _CloseDateBtn;
    private TextView _StartDateText, _CloseDateText;
    private MaterialButton _CreateBtn, _CancelBtn;
    private AutoCompleteTextView _MaterialSpinner;
    private int selectedSpinnerItem = -1;
    private MaterialTimePicker _TimePicker;
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
        _PlaceName = findViewById(R.id.place_name);
        _PlaceWebsite = findViewById(R.id.place_website);
        _PlacePhoneNum = findViewById(R.id.place_phone);
        _StartDateBtn = findViewById(R.id.start_date);
        _CloseDateBtn = findViewById(R.id.close_date);
        _CreateBtn = findViewById(R.id.create);
        _CancelBtn = findViewById(R.id.cancel);
        _StartDateText = findViewById(R.id.start_date_text);
        _CloseDateText = findViewById(R.id.close_date_text);
        _MaterialSpinner = findViewById(R.id.material_spinner);
        String[] spinnerItems = getResources().getStringArray(R.array.place_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                spinnerItems
        );

        _MaterialSpinner.setAdapter(adapter);
    }

    private void registerCallbackListeners() {
        _CreateBtn.setOnClickListener(this::createBtnOnClick);
        _CancelBtn.setOnClickListener(this::cancelBtnOnClick);
        _StartDateBtn.setOnClickListener(this::selectTimeOnClick);
        _CloseDateBtn.setOnClickListener(this::selectTimeOnClick);
        _MaterialSpinner.setOnItemClickListener((adapterView, view, i, l) -> selectedSpinnerItem = i);
    }

    private void createBtnOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.progress_bar_dialog, null));
        builder.setCancelable(false);

        progressDialog = builder.create();
        progressDialog.show();

        String name, type, website, phone, startTime, closeTime;
        name = _PlaceName.getEditText().getText().toString();
        type = getResources().getStringArray(R.array.place_types)[selectedSpinnerItem];
        website = _PlaceWebsite.getEditText().getText().toString();
        phone = _PlacePhoneNum.getEditText().getText().toString();
        startTime = _StartDateText.getText().toString();
        closeTime = _CloseDateText.getText().toString();

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
            timeText = _StartDateText;
        } else {
            titleText = "Select closing time";
            hour = 20;
            timeText = _CloseDateText;
        }
        _TimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(0)
                .setTitleText(titleText)
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build();
        _TimePicker.addOnPositiveButtonClickListener(view2 -> {
            String hourText, minuteText;
            if (_TimePicker.getHour() < 10) {
                hourText = "0" + _TimePicker.getHour();
            } else {
                hourText = "" + _TimePicker.getHour();
            }
            if (_TimePicker.getMinute() < 10) {
                minuteText = "0" + _TimePicker.getMinute();
            } else {
                minuteText = "" + _TimePicker.getMinute();
            }
            timeText.setText(hourText + ":" + minuteText);
        });
        _TimePicker.show(getSupportFragmentManager(), "TAG");
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
    @Override
    protected void onDestroy() {
        System.out.println(TAG + " " + "Brisem se!");
        super.onDestroy();
    }
}