package com.example.timespotter;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountLogin {
    private OnTaskCompleted inter;

    public AccountLogin(OnTaskCompleted inter) {
        this.inter = inter;
    }

    public void execute(String username, String password) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("Users")
                .child(username)
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       DataSnapshot snapshot = task.getResult();
                       if (snapshot.exists()) {
                           User user = snapshot.getValue(User.class);
                           if (user.getPassword().equals(password)) {
                               inter.onTaskComplete();
                           } else {
                               Log.d("LOG IN", "Podaci su netacni");
                           }
                       } else {
                           Log.d("LOG IN", "Podaci su netacni");
                       }
                   } else {
                       //obrada errora
                   }
                });
    }
}
