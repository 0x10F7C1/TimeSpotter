package com.example.timespotter.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.timespotter.Place;
import com.example.timespotter.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private View _Window;
    private Context _Context;

    public MarkerInfoAdapter(Context context) {
        _Context = context;
        _Window = LayoutInflater.from(_Context).inflate(R.layout.marker_info_window, null);
    }
    private void renderWindowInfo(Marker marker, View view) {
        TextView name, website, phone, startTime, closeTime, username;
        ImageView photo;
        Button rate;
        name = (TextView) view.findViewById(R.id.window_place_name);
        website = (TextView) view.findViewById(R.id.window_place_website);
        phone = (TextView) view.findViewById(R.id.window_place_phone);
        startTime = (TextView) view.findViewById(R.id.window_place_start_time);
        closeTime = (TextView) view.findViewById(R.id.window_place_close_time);
        username = (TextView) view.findViewById(R.id.window_place_metadata);
        photo = (ImageView) view.findViewById(R.id.window_place_image);

        Place place = (Place)marker.getTag();
        Picasso.get().load(place.getImageUri()).into(photo);
        Log.d("Place phone number", place.getPhone());
        phone.setText(place.getPhone());
        name.setText(place.getName());
        website.setText(place.getWebsite());
        startTime.setText(place.getStartTime());
        closeTime.setText(place.getCloseTime());
        username.setText(place.getCreator());
        Log.d("Place image URL", place.getImageUri());

    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        //Log.d("getInfoContents", "inflating!");
        //renderWindowInfo(marker, _Window);
        //return _Window;
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        Log.d("getInfoWindow", "inflating!");
        renderWindowInfo(marker, _Window);
        return _Window;
    }
}
