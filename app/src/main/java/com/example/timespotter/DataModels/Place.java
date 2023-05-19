package com.example.timespotter.DataModels;

public class Place {
    private String key;
    private String name;
    private String type;
    private String website;
    private String phone;
    private String startTime;
    private String closeTime;
    private String imageUri;
    private double latitude;
    private double longitude;
    private int day;
    private int month;
    private int year;
    private String creator;

    public Place() {
    }

    public Place(
            String key,
            String name,
            String type,
            String website,
            String phone,
            String startTime,
            String closeTime,
            String imageUri,
            double latitude,
            double longitude,
            int day,
            int month,
            int year,
            String creator) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.website = website;
        this.phone = phone;
        this.startTime = startTime;
        this.closeTime = closeTime;
        this.imageUri = imageUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.day = day;
        this.month = month;
        this.year = year;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
