package com.example.timespotter.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.Repositories.UserLoginRepository;

public class MainActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<Result<User>> _UserLoginState;
    private final UserLoginRepository _UserLoginRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        _UserLoginRepository = new UserLoginRepository();
        _UserLoginState = _UserLoginRepository.getUserLoginState();
    }

    public void userLogin(String username, String password) {
        _UserLoginRepository.userLogin(username, password);
    }

    public MutableLiveData<Result<User>> getUserLoginState() {
        return _UserLoginState;
    }
}
