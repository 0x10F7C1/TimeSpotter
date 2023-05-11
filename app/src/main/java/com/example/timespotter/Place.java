package com.example.timespotter;

public class Place {
    private String name;
    private String type;
    private String website;
    private String phone;
    private String startTime;
    private String closeTime;
    private String imageUri;

    public Place() {}
    public Place(String name, String type, String website, String phone, String startTime, String closeTime, String imageUri) {
        this.name = name;
        this.type = type;
        this.website = website;
        this.phone = phone;
        this.startTime = startTime;
        this.closeTime = closeTime;
        this.imageUri = imageUri;
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
}
