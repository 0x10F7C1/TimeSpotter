package com.example.timespotter;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountCreation {

    private OnTaskCompleted inter;
    public AccountCreation(OnTaskCompleted inter) {
        this.inter = inter;
    }
    public void execute(User user) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("Users")
                .child(user.getUsername())
                .setValue(user)
                .addOnCompleteListener(this::userAddedOnComplete);
    }
    private void userAddedOnComplete(Task<Void> task) {
        Log.d("userAddedOnComplete", "OK?");
        if (task.isSuccessful()) {
            inter.onTaskComplete();
        } else {
            Log.d("User creation", "FAILURE");
        }
    }
}
