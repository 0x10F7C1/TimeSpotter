package com.example.timespotter.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLoginRepository {
    private final MutableLiveData<Result<User>> _UserLoginState;
    private final DatabaseReference database;

    public UserLoginRepository() {
        _UserLoginState = new MutableLiveData<>();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public MutableLiveData<Result<User>> getUserLoginState() {
        return _UserLoginState;
    }
    public void userLogin(String username, String password) {
        database
                .child("Users")
                .child(username)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Result<User> result = new Result<>();
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user.getPassword().equals(password)) {
                            result.setOperationSuccess(Result.OPERATION_SUCCESS).setValue(user);
                        } else {
                            result.setOperationSuccess(Result.OPERATION_FAILURE).setError(new Exception("Username/Password nisu tacni"));
                        }
                        _UserLoginState.postValue(result);
                    }
                })
                .addOnFailureListener(exception -> {
                    Result<User> result = new Result<>();
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(exception);
                    _UserLoginState.postValue(result);
                });
    }
}
