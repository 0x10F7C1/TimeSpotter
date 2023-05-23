package com.example.timespotter.DataModels;

public class UserLeaderboard implements Comparable<UserLeaderboard> {
    private String username;
    private String name;
    private String imageUri;
    private Long points;

    public UserLeaderboard(String username, String name, String imageUri, Long points) {
        this.username = username;
        this.name = name;
        this.imageUri = imageUri;
        this.points = points;
    }
    public UserLeaderboard() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Override
    public int compareTo(UserLeaderboard userLeaderboard) {
        return points.compareTo(userLeaderboard.points);
    }
}
