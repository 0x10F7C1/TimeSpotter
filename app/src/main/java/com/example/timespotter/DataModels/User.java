package com.example.timespotter.DataModels;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String username;
    private String email;
    private String password;
    private String phone;
    private Long points;
    private String key;

    public User() {
    }

    public User(String name, String username, String email, String password, String phone, Long points, String key) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.points = points;
        this.key = key;
    }

    public static User copyUser(User oldUser) {
        User newUser = new User();
        newUser.setUsername(oldUser.username);
        newUser.setName(oldUser.getName());
        newUser.setPassword(oldUser.getPassword());
        newUser.setEmail(oldUser.getEmail());
        newUser.setPhone(oldUser.getPhone());
        newUser.setPoints(oldUser.getPoints());
        newUser.setKey(oldUser.getKey());
        return newUser;
    }

    public static boolean areUsersEqual(User u1, User u2) {
        return u1.username.equals(u2.username)
                && u1.email.equals(u2.email)
                && u1.phone.equals(u2.phone)
                && u1.password.equals(u2.password)
                && u1.name.equals(u2.name)
                && u1.points.equals(u2.points)
                && u1.key.equals(u2.key);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + "\n" + "Username: " + username + "\n" + "Email: " + email + "\n"
                + "Password: " + password + "\n" + "Phone: " + phone + "\n" + "Points: " + points;
    }
}
