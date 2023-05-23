package com.example.timespotter.Events;

import com.example.timespotter.DataModels.User;

public class MainActivityEvent {
    public static class UserLoginSuccess {
        public User result;

        public UserLoginSuccess(User result) {
            this.result = result;
        }
    }
}
