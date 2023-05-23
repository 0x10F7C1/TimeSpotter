package com.example.timespotter.GeneralActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.DbMediators.LocationTemplateActivityDb;
import com.example.timespotter.Events.LocationTemplateActivityEvent;
import com.example.timespotter.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDate;

public class LocationTemplateActivity extends AppCompatActivity {
    private static final int PHOTO_PICKER_REQUEST_CODE = 2;
    private static final String TAG = LocationTemplateActivity.class.getSimpleName();
    private final String _ImageId = "";
    private final LocationTemplateActivityDb locationTemplateActivityDb = new LocationTemplateActivityDb();
    private EditText _PlaceName, _PlaceWebsite, _PlacePhoneNum;
    private ImageButton _StartDateBtn, _CloseDateBtn;
    private TextView _StartDateText, _CloseDateText;
    private Button _UploadBtn, _CreateBtn, _CancelBtn;
    private Spinner _PlaceTypeSpinner;
    private MaterialTimePicker _TimePicker;
    private Uri _PlaceImageUri;
    private String _Username;
    private String _PartyId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_template);

        user = (User) getIntent().getSerializableExtra("user");
        _Username = getIntent().getStringExtra("username");

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        _PlaceName = findViewById(R.id.place_name);
        _PlaceWebsite = findViewById(R.id.place_website);
        _PlacePhoneNum = findViewById(R.id.place_phone);
        _StartDateBtn = findViewById(R.id.start_date);
        _CloseDateBtn = findViewById(R.id.close_date);
        _UploadBtn = findViewById(R.id.upload_place);
        _CreateBtn = findViewById(R.id.create);
        _CancelBtn = findViewById(R.id.cancel);
        _StartDateText = findViewById(R.id.start_date_text);
        _CloseDateText = findViewById(R.id.close_date_text);
        _PlaceTypeSpinner = findViewById(R.id.place_type);
    }

    private void registerCallbackListeners() {
        _UploadBtn.setOnClickListener(this::uploadBtnOnClick);
        _CreateBtn.setOnClickListener(this::createBtnOnClick);
        _CancelBtn.setOnClickListener(this::cancelBtnOnClick);
        _StartDateBtn.setOnClickListener(this::selectTimeOnClick);
        _CloseDateBtn.setOnClickListener(this::selectTimeOnClick);
    }

    private void uploadBtnOnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
    }

    private void createBtnOnClick(View view) {
        String name, type, website, phone, startTime, closeTime;
        name = _PlaceName.getText().toString();
        type = _PlaceTypeSpinner.getSelectedItem().toString().toLowerCase();
        website = _PlaceWebsite.getText().toString();
        phone = _PlacePhoneNum.getText().toString();
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
                "",
                getIntent().getDoubleExtra("latitude", 0),
                getIntent().getDoubleExtra("longitude", 0),
                day,
                month,
                year,
                user.getKey(),
                user.getUsername());
        locationTemplateActivityDb.addPlace(place, _PlaceImageUri);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //za sada neka bude galerija, ali kasnije dodati da bude zapravo kamera da se koristi
        if (requestCode == PHOTO_PICKER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                _PlaceImageUri = data.getData();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlaceAdded(LocationTemplateActivityEvent.PlaceAdded result) {
        Log.d(TAG, "Place added successfully");
        //dodati neku bolju poruku posle
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