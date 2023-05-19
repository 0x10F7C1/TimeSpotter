package com.example.timespotter.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegisterRepository {
    private final MutableLiveData<Result<User>> _UserRegisterState;
    private final DatabaseReference database;

    public UserRegisterRepository() {
        _UserRegisterState = new MutableLiveData<>();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<Result<User>> getUserRegisterState() {
        return _UserRegisterState;
    }

    public void userSignup(User user) {
        final Result<User> result = new Result<>();
        database
                .child("Users")
                .child(user.getUsername())
                .setValue(user)
                .addOnSuccessListener(unused -> {
                    result.setOperationSuccess(Result.OPERATION_SUCCESS).setValue(user);
                    _UserRegisterState.postValue(result);
                })
                .addOnFailureListener(e -> {
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(e);
                    _UserRegisterState.postValue(result);
                });
    }
}
