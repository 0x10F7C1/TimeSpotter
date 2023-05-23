package com.example.timespotter.DbMediators;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.Events.MyProfileEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

//Za sve komunikacije sa bazom za ProfileFragment(Repository u MVVM patternu)
public class ProfileFragmentDb {
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void updateUserProfile(User oldUser, User newUser) {
        if (oldUser.getUsername().equals(newUser.getUsername())) {
            appendUserNode(newUser);
            EventBus.getDefault().post(new MyProfileEvent.RemoveUser());
        } else {
            appendUserNode(newUser);
            removeOldUserNode(oldUser.getUsername());
        }
    }

    private void appendUserNode(User newUser) {
        database
                .child("Users")
                .child(newUser.getUsername())
                .setValue(newUser)
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MyProfileEvent.AppendUser());
                });
    }

    private void removeOldUserNode(String oldUsername) {
        database
                .child("Users")
                .child(oldUsername)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    EventBus.getDefault().post(new MyProfileEvent.RemoveUser());
                });
    }
}
