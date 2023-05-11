package com.example.timespotter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.xml.transform.OutputKeys;

public class LocationTemplate extends AppCompatActivity {
    private static final int PHOTO_PICKER_REQUEST_CODE = 2;
    private EditText _PlaceName, _PlaceWebsite, _PlacePhoneNum;
    private ImageButton _StartDateBtn, _CloseDateBtn;
    private TextView _StartDateText, _CloseDateText;
    private Button _UploadBtn, _CreateBtn, _CancelBtn;
    private Spinner _PlaceTypeSpinner;
    private MaterialTimePicker _TimePicker;

    private Uri placeImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_template);

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
        //dodati kao progress indikator zbog baze? mozda?
        String name, type, website, phone, startTime, closeTime;
        final String[] imageURL = new String[1];

        name = _PlaceName.getText().toString();
        type = _PlaceTypeSpinner.getSelectedItem().toString();
        website = _PlaceWebsite.getText().toString();
        phone = _PlacePhoneNum.getText().toString();
        startTime = _StartDateText.getText().toString();
        closeTime = _CloseDateText.getText().toString();

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("Place photos")
                .putFile(placeImageUri)
                .addOnCompleteListener(taskUploadImage -> {
                   if (taskUploadImage.isSuccessful()) {
                       taskUploadImage.getResult()
                               .getStorage()
                               .getDownloadUrl()
                               .addOnCompleteListener(taskImageUri -> {
                                   if (taskImageUri.isSuccessful()) {
                                       imageURL[0] = taskImageUri.getResult().toString();
                                       Place place = new Place(name, type, website, phone, startTime, closeTime, imageURL[0]);
                                       DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                       database.child("Places")
                                               .push()
                                               .setValue(place)
                                               .addOnCompleteListener(task -> {
                                                   if (task.isSuccessful()) {
                                                       Toast.makeText(this, "Place uspesno dodat!", Toast.LENGTH_LONG).show();
                                                   }
                                                   else {
                                                       //obrada greske
                                                   }
                                               });
                                   }
                                   else {
                                       //obrada greske
                                   }
                               });
                   }
                   else {
                       //obrada greske
                   }
                });
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
        }
        else {
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
            }
            else {
                hourText = "" + _TimePicker.getHour();
            }
            if (_TimePicker.getMinute() < 10) {
                minuteText = "0" + _TimePicker.getMinute();
            }
            else {
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
                placeImageUri = data.getData();
            }
        }
    }
}