package com.example.timespotter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.Utils.AppData;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.Enums.EventType;
import com.example.timespotter.R;
import com.example.timespotter.Utils.Result;
import com.example.timespotter.DbContexts.UserDb;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button login, signup;
    private TextInputLayout username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    private void registerCallbackListeners() {
        login.setOnClickListener(this::loginOnClick);
        signup.setOnClickListener(this::signUpOnClick);
    }

    private void loginOnClick(View view) {
        String username = this.username.getEditText().getText().toString();
        String password = this.password.getEditText().getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            makeToast("Empty fields");
        } else {
            new UserDb().userLogin(username, password);
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
    public void eventHandling(Result result) {
        if (result.event == EventType.USER_LOGIN) {
            handleUserLogin(result);
        }
    }
    private void handleUserLogin(Result result) {
        if (result.operationStatus == Result.SUCCESS) {
            Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
            AppData.user = (User)result.result;
            startActivity(intent);
            finish();
        }
        else {
            makeToast(result.errMsg);
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