package com.example.timespotter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private final View _Window;
    private final Context _Context;

    public MarkerInfoAdapter(Context context) {
        _Context = context;
        _Window = LayoutInflater.from(_Context).inflate(R.layout.marker_info_window, null);
    }

    private void renderWindowInfo(Marker marker, View view) {
        TextView name, website, phone, startTime, closeTime, username;
        name = view.findViewById(R.id.marker_name);
        website = view.findViewById(R.id.marker_website);
        phone = view.findViewById(R.id.marker_phone);
        startTime = view.findViewById(R.id.marker_start_time);
        closeTime = view.findViewById(R.id.marker_close_time);
        username = view.findViewById(R.id.marker_creator);


        Place place = (Place) marker.getTag();
        phone.setText(place.getPhone());
        name.setText(place.getName());
        website.setText(place.getWebsite());
        startTime.setText(place.getStartTime());
        closeTime.setText(place.getCloseTime());
        username.setText("Added by:  " + place.getCreatorUsername());

    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        //return null;
        renderWindowInfo(marker, _Window);
        return _Window;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        Log.d("getInfoWindow", "inflating!");
        renderWindowInfo(marker, _Window);
        return _Window;
    }
}
