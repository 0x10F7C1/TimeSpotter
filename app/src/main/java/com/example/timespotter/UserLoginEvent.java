package com.example.timespotter;

import com.example.timespotter.DataModels.User;

public class UserLoginEvent {
    private final User user;
    private final String error;
    private final int status;
    public static final int OPERATION_SUCCESS = 0;
    public static final int OPERATION_FAILURE = 1;
    public static final String NO_ERROR = "";
    public static final User NO_VALUE = null;

    public UserLoginEvent(User user, String error, int status) {
        this.user = user;
        this.error = error;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }
}
