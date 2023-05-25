package com.example.timespotter.DataModels;

public class TableItem {
    private String username;
    private String placeName;
    private String phone;
    private String website;
    private String startTime, closeTime;

    public TableItem() {}

    public TableItem(String username, String placeName, String phone, String website, String startTime, String closeTime) {
        this.username = username;
        this.placeName = placeName;
        this.phone = phone;
        this.website = website;
        this.startTime = startTime;
        this.closeTime = closeTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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
}
