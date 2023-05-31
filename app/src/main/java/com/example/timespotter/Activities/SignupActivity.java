package com.example.timespotter.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.DbContexts.SignupActivityDb;
import com.example.timespotter.Events.SignupActivityEvent;
import com.example.timespotter.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private static final int PHOTO_PICKER = 2;
    private static final int USER_REGISTER_SUCCESS = 2;
    private static int userRegisterCount = 0;
    private final SignupActivityDb signupActivityDb = new SignupActivityDb();
    private TextInputLayout fullName, username, email, password, phone;
    private TextView avatarText;
    private MaterialButton signup;
    private ImageButton avatar;
    private Uri avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindViews();
        registerCallbackListeners();
    }

    private void bindViews() {
        fullName = findViewById(R.id.name);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phoneNum);
        signup = findViewById(R.id.signup);
        avatar = findViewById(R.id.profile_avatar);
        avatarText = findViewById(R.id.avatar_text);
    }

    private void registerCallbackListeners() {
        signup.setOnClickListener(this::signUpOnClick);
        avatar.setOnClickListener(this::avatarOnClick);
    }

    private void signUpOnClick(View view) {
        if (!validateName()
                | !validateUsername()
                | !validateEmail()
                | !validatePassword()
                | !validatePhone()
                | !validateAvatar()) {
            return;
        } else {
            String name, username, email, password, phone;
            name = fullName.getEditText().getText().toString();
            username = this.username.getEditText().getText().toString();
            email = this.email.getEditText().getText().toString();
            password = this.password.getEditText().getText().toString();
            phone = this.phone.getEditText().getText().toString();

            User user = new User(name, username, email, password, phone, 0l, 0l, "", "");
            signupActivityDb.userSignup(user, avatarUri);

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
        String name = fullName.getEditText().getText().toString();
        if (name.isEmpty()) {
            fullName.setError("Field cannot be empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String username = this.username.getEditText().getText().toString();
        String noWhiteSpace = "^[^\\s]+$";

        if (username.isEmpty()) {
            this.username.setError("Field cannot be empty");
            return false;
        } else if (username.length() >= 15) {
            this.username.setError("Username too long");
            return false;
        } else if (!username.matches(noWhiteSpace)) {
            this.username.setError("Whitespaces are not allowed");
            return false;
        } else {
            this.username.setError(null);
            this.username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = this.email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            this.email.setError("Field cannot be empty");
            return false;
        } else if (!email.matches(emailPattern)) {
            this.email.setError("Invalid email address");
            return false;
        } else {
            this.email.setError(null);
            this.email.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validatePassword() {
        String password = this.password.getEditText().getText().toString();
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
            this.password.setError("Field cannot be empty");
            return false;
        } else if (!password.matches(passwordPattern)) {
            this.password.setError("Password is too weak");
            return false;
        } else {
            this.password.setError(null);
            this.password.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validatePhone() {
        String phone = this.phone.getEditText().getText().toString();

        if (phone.isEmpty()) {
            this.phone.setError("Field cannot be empty");
            return false;
        } else {
            this.phone.setError(null);
            this.phone.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAvatar() {
        if (avatarUri == null) {
            avatarText.setText("Please select an avatar");
            avatarText.setTextColor(getResources().getColor(R.color.holo_dark_red));
            return false;
        }
        return true;
    }

    private void avatarOnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICKER);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER) {
            if (resultCode == RESULT_OK) {
                avatarUri = data.getData();
            }
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