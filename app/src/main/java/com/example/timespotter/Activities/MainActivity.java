package com.example.timespotter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.AppData;
import com.example.timespotter.DbContexts.MainActivityDb;
import com.example.timespotter.Events.MainActivityEvent;
import com.example.timespotter.R;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final MainActivityDb mainActivityDb = new MainActivityDb();
    //TODO -> Dodati submissions na User model
    //TODO -> Filter: resiti bug gde ne skriva jedan od Markera
    //TODO -> implementirati tabelu (ili kao recycler view ili kao TableLayout)
    //TODO -> Remember me implementirati
    //TODO -> dodati validaciju za password, i proveru da li email/username vec postoji u bazi ili ne
    //TODO -> kada user doda marker, da on ide u excluded markers?
    //TODO -> ikonice na markere resiti
    private Button _Login, _SignUp, _ForgetPass;
    private TextInputLayout _Username, _Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onUserLoggedInEvent(MainActivityEvent.UserLoginSuccess result) {
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        AppData.user = result.result;
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        System.out.println(TAG + " " + "Brisem se!");
        super.onDestroy();
    }
}