package com.example.timespotter.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.Repositories.UserRegisterRepository;

public class SignupActivityViewModel extends AndroidViewModel {
    private final UserRegisterRepository _UserRegisterRepository;
    private final MutableLiveData<Result<User>> _UserRegisterState;

    public SignupActivityViewModel(@NonNull Application application) {
        super(application);
        _UserRegisterRepository = new UserRegisterRepository();
        _UserRegisterState = _UserRegisterRepository.getUserRegisterState();
    }

    public MutableLiveData<Result<User>> getUserRegisterState() {
        return _UserRegisterState;
    }

    public void userSignup(User user) {
        _UserRegisterRepository.userSignup(user);
    }
}
