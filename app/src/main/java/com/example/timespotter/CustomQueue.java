package com.example.timespotter;

import com.example.timespotter.DataModels.Place;

import java.util.LinkedList;
import java.util.List;

public class CustomQueue {
    private List<Place> red = new LinkedList<>();
    public int len = 0;

    public void addValue(Place place) {
        red.add(place);
        len++;
    }

    public Place getPlace() {
        return red.remove(0);
    }

}
