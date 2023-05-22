package com.example.timespotter.GeneralActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.MainActivityDb;
import com.example.timespotter.R;
import com.example.timespotter.UserLoginEvent;
import com.example.timespotter.ViewModels.MainActivityViewModel;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    //TODO -> Leaderboards: UI + baza
    //TODO -> Filter: poboljsati UI dijalog
    //TODO -> Switch(Mapa, Tabela) ili pak u bottom navigaciji dodati jos jedan fragmenat za priakaz tabele
    //TODO -> LocationTemplate poboljsati UI
    //TODO -> MyProfile: UI + baza
    //TODO -> Remember me implementirati
    //TODO -> Progres inidikator na stari koje cekaju na bazu
    private Button _Login, _SignUp, _ForgetPass;
    private TextInputLayout _Username, _Password;
    private MainActivityViewModel viewModel;
    private MainActivityDb mainActivityDb = new MainActivityDb();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainActivityViewModel.class);
        /*viewModel.getUserLoginState().observe(this, userResult -> {
            if (userResult.getStatus() == Result.OPERATION_SUCCESS) {
                Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                intent.putExtra("username", userResult.getValue().getUsername());
                User dummyUser = new User("Oke", "Oke", "email", "pass", "phone", 10l);
                intent.putExtra("user", (Serializable) dummyUser);
                startActivity(intent);
            } else {
                Log.d("ViewModel", userResult.getError().getMessage());
            }
        });*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        _Login = findViewById(R.id.login);
        _SignUp = findViewById(R.id.signup);
        _ForgetPass = findViewById(R.id.forgetPass);
        _Username = findViewById(R.id.username);
        _Password = findViewById(R.id.password);
    }

    private void registerCallbackListeners() {
        _Login.setOnClickListener(this::loginOnClick);
        _SignUp.setOnClickListener(this::signUpOnClick);
        _ForgetPass.setOnClickListener(this::loginOnClick);
    }

    private void loginOnClick(View view) {
        String username = _Username.getEditText().getText().toString();
        String password = _Password.getEditText().getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            makeToast("Empty fields");
        } else {
            //viewModel.userLogin(username, password);
            mainActivityDb.userLogin(username, password);

        }
    }

    private void signUpOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userLoginEvent(UserLoginEvent result) {
        if (result.getStatus() == UserLoginEvent.OPERATION_SUCCESS) {
            Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
            intent.putExtra("user", (Serializable) result.getUser());
            startActivity(intent);
        }
        else {
            System.out.println(result.getError());
        }
    }

    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}