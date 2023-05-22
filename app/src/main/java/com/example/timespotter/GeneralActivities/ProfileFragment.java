package com.example.timespotter.GeneralActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.MyProfileEvent;
import com.example.timespotter.ProfileFragmentDb;
import com.example.timespotter.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private Button _UpdateProfile;
    private TextInputLayout _UsernameText, _EmailText, _PasswordText, _PhoneText;
    private TextView _UserUsernameText, _UserFullNameText, _UserPtsText;
    private User user;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private MyProfileEvent.AppendUser appendUserResult;
    private MyProfileEvent.RemoveUser removeUserResult;
    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        _UpdateProfile = view.findViewById(R.id.profile_update);
        _UsernameText = view.findViewById(R.id.profile_username);
        _EmailText = view.findViewById(R.id.profile_email);
        _PhoneText = view.findViewById(R.id.profile_phone);
        _PasswordText = view.findViewById(R.id.profile_password);
        _UserUsernameText = view.findViewById(R.id.profile_username_textview);
        _UserFullNameText = view.findViewById(R.id.profile_full_name);
        _UserPtsText = view.findViewById(R.id.profile_pts);
        user = (User) getArguments().getSerializable("user");

        _UserUsernameText.setText(user.getUsername());
        _UserFullNameText.setText(user.getName());
        _UserPtsText.setText(user.getPoints().toString());
        _UpdateProfile.setOnClickListener(this::updateProfileOnClick);

        return view;
    }

    private void updateProfileOnClick(View view) {
        ProfileFragmentDb db = new ProfileFragmentDb();
        String username, email, phone, password;
        username = _UsernameText.getEditText().getText().toString();
        email = _EmailText.getEditText().getText().toString();
        phone = _PhoneText.getEditText().getText().toString();
        password = _PasswordText.getEditText().getText().toString();

        User newUser = User.copyUser(user);

        if (validateUsername(newUser, username)
                && validateEmail(newUser, email)
                && validatePassword(newUser, password)
                && validatePhoneNumber(newUser, phone)) {
            db.updateUserProfile(user, newUser);
        }
    }
    private boolean validateUsername(User newUser, String username) {
        String noWhiteSpace = "^\\S+$";
        if (username.length() == 0) {
            _UsernameText.setError(null);
            _UsernameText.setErrorEnabled(false);
            newUser.setUsername(user.getUsername());
            return true;
        }
        else if (!username.matches(noWhiteSpace)) {
            _UsernameText.setError("Whitespaces are not allowed");
            newUser.setUsername(user.getUsername());
            return false;
        }
        else {
            _UsernameText.setError(null);
            _UsernameText.setErrorEnabled(false);
            newUser.setUsername(username);
            return true;
        }
    }
    private boolean validateEmail(User newUser, String email) {
        String emailPattern = "[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            _EmailText.setError(null);
            _EmailText.setErrorEnabled(false);
            newUser.setEmail(user.getEmail());
            return true;
        } else if (!email.matches(emailPattern)) {
            _EmailText.setError("Invalid email address");
            newUser.setEmail(user.getEmail());
            return false;
        } else {
            _EmailText.setError(null);
            _EmailText.setErrorEnabled(false);
            newUser.setEmail(email);
            return true;
        }

    }
    private boolean validatePassword(User newUser, String password) {
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
            _PasswordText.setError(null);
            _PasswordText.setErrorEnabled(false);
            newUser.setPassword(user.getPassword());
            return true;
        } else if (!password.matches(passwordPattern)) {
            _PasswordText.setError("Weak password!");
            newUser.setPassword(user.getPassword());
            return false;
        } else {
            _PasswordText.setError(null);
            _PasswordText.setErrorEnabled(false);
            newUser.setPassword(password);
            return true;
        }

    }
    private boolean validatePhoneNumber(User newUser, String phone) {
        String noWhiteSpace = "^\\S+$";
        if (phone.length() == 0) {
            _PhoneText.setError(null);
            _PhoneText.setErrorEnabled(false);
            newUser.setPhone(user.getPhone());
            return true;
        }
        else if (!phone.matches(noWhiteSpace)) {
            _PhoneText.setError("Whitespaces are not allowed");
            newUser.setPhone(user.getPhone());
            return false;
        }
        else {
            _PhoneText.setError(null);
            _PhoneText.setErrorEnabled(false);
            newUser.setPhone(phone);
            return true;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAppendEvent(MyProfileEvent.AppendUser result) {
        appendUserResult = result;
        if (removeUserResult != null) {
            appendUserResult = null;
            removeUserResult = null;
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRemoveEvent(MyProfileEvent.RemoveUser result) {
        removeUserResult = result;
        if (appendUserResult != null) {
            appendUserResult = null;
            removeUserResult = null;
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}