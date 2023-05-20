package com.example.timespotter.DataModels;

import com.google.android.gms.maps.model.Marker;

public class PlaceMarker {
    private Marker marker;
    private int position;

    public PlaceMarker(Marker marker, int position) {
        this.marker = marker;
        this.position = position;
    }

    public Marker getMarker() {
        return marker;
    }
    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public int getPosition() {
        return position;
    }

    public void nullifyMarker() {
        marker.remove();
        marker = null;
    }
}
