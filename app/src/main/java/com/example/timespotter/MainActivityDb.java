package com.example.timespotter;

import com.example.timespotter.DataModels.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class MainActivityDb {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public void userLogin(String username, String password) {
        database
                .child("Users")
                .child(username)
                .get()
                .addOnSuccessListener(snapshot -> {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        EventBus.getDefault().post(new UserLoginEvent(user, UserLoginEvent.NO_ERROR, UserLoginEvent.OPERATION_SUCCESS));
                    }
                    else {
                        EventBus.getDefault().post(new UserLoginEvent(UserLoginEvent.NO_VALUE, "Username/Password is invalid", UserLoginEvent.OPERATION_FAILURE));
                    }
                });
        //..greske obraditi kasnije
    }

    public void userSignup(User user) {
        String userKey = UUID.randomUUID().toString();
        user.setKey(userKey);
        database
                .child("Users")
                .child(user.getUsername())
                .setValue(user)
                .addOnSuccessListener(unused -> {
                   EventBus.getDefault().post(new UserSignupEvent(user, UserSignupEvent.NO_ERROR, UserSignupEvent.OPERATION_SUCCESS));
                });
    }


}
