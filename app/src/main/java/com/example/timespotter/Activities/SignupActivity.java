package com.example.timespotter.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.DbContexts.MainActivityDb;
import com.example.timespotter.DbContexts.SignupActivityDb;
import com.example.timespotter.Events.SignupActivityEvent;
import com.example.timespotter.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SignupActivity extends AppCompatActivity {

    private static final int USER_REGISTER_SUCCESS = 2;
    private static int userRegisterCount = 0;
    private final MainActivityDb mainActivityDb = new MainActivityDb();
    private final SignupActivityDb signupActivityDb = new SignupActivityDb();
    private TextInputLayout _FullName, _Username, _Email, _Password, _Phone;
    private MaterialButton _SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        _FullName = findViewById(R.id.name);
        _Username = findViewById(R.id.username);
        _Email = findViewById(R.id.email);
        _Password = findViewById(R.id.password);
        _Phone = findViewById(R.id.phoneNum);
        _SignUp = findViewById(R.id.signup);
    }

    private void registerCallbackListeners() {
        _SignUp.setOnClickListener(this::signUpOnClick);
    }

    private void signUpOnClick(View view) {
        if (!validateName() | !validateUsername() | !validateEmail() | !validatePassword() | !validatePhone()) {
            return;
        } else {
            String name, username, email, password, phone;
            name = _FullName.getEditText().getText().toString();
            username = _Username.getEditText().getText().toString();
            email = _Email.getEditText().getText().toString();
            password = _Password.getEditText().getText().toString();
            phone = _Phone.getEditText().getText().toString();

            User user = new User(name, username, email, password, phone, 0l, "");
            signupActivityDb.userSignup(user);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userSignupEvent(SignupActivityEvent.RegisterUser result) {
        userRegisterCount = userRegisterCount + 1;
        if (userRegisterCount == USER_REGISTER_SUCCESS) {
            userRegisterCount = 0;
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateName() {
        String name = _FullName.getEditText().getText().toString();
        if (name.isEmpty()) {
            _FullName.setError("Field cannot be empty");
            return false;
        } else {
            _FullName.setError(null);
            _FullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String username = _Username.getEditText().getText().toString();
        String noWhiteSpace = "^[^\\s]+$";

        if (username.isEmpty()) {
            _Username.setError("Field cannot be empty");
            return false;
        } else if (username.length() >= 15) {
            _Username.setError("Username too long");
            return false;
        } else if (!username.matches(noWhiteSpace)) {
            _Username.setError("Whitespaces are not allowed");
            return false;
        } else {
            _Username.setError(null);
            _Username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = _Email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            _Email.setError("Field cannot be empty");
            return false;
        } else if (!email.matches(emailPattern)) {
            _Email.setError("Invalid email address");
            return false;
        } else {
            _Email.setError(null);
            _Email.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validatePassword() {
        String password = _Password.getEditText().getText().toString();
        String passwordPattern = "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}" +
                "$";

        if (password.isEmpty()) {
            _Password.setError("Field cannot be empty");
            return false;
        } else if (!password.matches(passwordPattern)) {
            _Password.setError("Password is too weak");
            return false;
        } else {
            _Password.setError(null);
            _Password.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validatePhone() {
        String phone = _Phone.getEditText().getText().toString();

        if (phone.isEmpty()) {
            _Phone.setError("Field cannot be empty");
            return false;
        } else {
            _Phone.setError(null);
            _Phone.setErrorEnabled(false);
            return true;
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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