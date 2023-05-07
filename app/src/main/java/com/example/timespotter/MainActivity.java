package com.example.timespotter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

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
            makeToast("Prazna polja");
        }
        else {
            new AccountLogin(() -> {
                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(intent);
            }).execute(username, password);
        }
    }

    private void signUpOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}