package com.example.timespotter.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.timespotter.Activities.MainActivity;
import com.example.timespotter.AppData;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.DbContexts.ProfileFragmentDb;
import com.example.timespotter.Events.MyProfileEvent;
import com.example.timespotter.R;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProfileFragment extends Fragment {
    private static final int PHOTO_PICKER = 2;
    private Button _UpdateProfile, _LogoutProfile;
    private TextInputLayout _UsernameText, _EmailText, _PasswordText, _PhoneText;
    private TextView _UserUsernameText, _UserFullNameText, _UserPtsText, userSubmissionsText;
    private CircleImageView _UserProfileImage;
    private Uri avatarUri;

    public ProfileFragment() {
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
        _LogoutProfile = view.findViewById(R.id.profile_logout);
        _UsernameText = view.findViewById(R.id.profile_username);
        _EmailText = view.findViewById(R.id.profile_email);
        _PhoneText = view.findViewById(R.id.profile_phone);
        _PasswordText = view.findViewById(R.id.profile_password);
        _UserUsernameText = view.findViewById(R.id.profile_username_textview);
        _UserFullNameText = view.findViewById(R.id.profile_full_name);
        _UserPtsText = view.findViewById(R.id.profile_pts);
        _UserProfileImage = view.findViewById(R.id.profile_image);
        userSubmissionsText = view.findViewById(R.id.profile_submissions);
        //user = (User) getArguments().getSerializable("user");

        _UserUsernameText.setText(AppData.user.getUsername());
        _UserFullNameText.setText(AppData.user.getName());
        _UserPtsText.setText(AppData.user.getPoints().toString());
        userSubmissionsText.setText(AppData.user.getSubmissions().toString());
        _UpdateProfile.setOnClickListener(this::updateProfileOnClick);
        _LogoutProfile.setOnClickListener(this::logoutProfileOnClick);
        _UserProfileImage.setOnClickListener(this::changeProfileImageOnClick);
        Glide.with(requireContext()).load(AppData.user.getImageUri())
                .centerCrop().into(_UserProfileImage);
        return view;
    }

    private void updateProfileOnClick(View view) {
        ProfileFragmentDb db = new ProfileFragmentDb();
        String username, email, phone, password;
        username = _UsernameText.getEditText().getText().toString();
        email = _EmailText.getEditText().getText().toString();
        phone = _PhoneText.getEditText().getText().toString();
        password = _PasswordText.getEditText().getText().toString();

        User newUser = User.copyUser(AppData.user);

        if (validateUsername(newUser, username)
                && validateEmail(newUser, email)
                && validatePassword(newUser, password)
                && validatePhoneNumber(newUser, phone)) {
            boolean isAvatarChanged = avatarUri != null;
            db.updateUserProfile(AppData.user, newUser, avatarUri, isAvatarChanged);
        }
    }

    private void logoutProfileOnClick(View view) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void changeProfileImageOnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICKER);
    }

    private boolean validateUsername(User newUser, String username) {
        String noWhiteSpace = "^\\S+$";
        if (username.length() == 0) {
            _UsernameText.setError(null);
            _UsernameText.setErrorEnabled(false);
            newUser.setUsername(AppData.user.getUsername());
            return true;
        } else if (!username.matches(noWhiteSpace)) {
            _UsernameText.setError("Whitespaces are not allowed");
            newUser.setUsername(AppData.user.getUsername());
            return false;
        } else {
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
            newUser.setEmail(AppData.user.getEmail());
            return true;
        } else if (!email.matches(emailPattern)) {
            _EmailText.setError("Invalid email address");
            newUser.setEmail(AppData.user.getEmail());
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
            newUser.setPassword(AppData.user.getPassword());
            return true;
        } else if (!password.matches(passwordPattern)) {
            _PasswordText.setError("Weak password!");
            newUser.setPassword(AppData.user.getPassword());
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
            newUser.setPhone(AppData.user.getPhone());
            return true;
        } else if (!phone.matches(noWhiteSpace)) {
            _PhoneText.setError("Whitespaces are not allowed");
            newUser.setPhone(AppData.user.getPhone());
            return false;
        } else {
            _PhoneText.setError(null);
            _PhoneText.setErrorEnabled(false);
            newUser.setPhone(phone);
            return true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserUpdatedEvent(MyProfileEvent.UserUpdate result) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(requireContext()).load(data.getData()).centerCrop().into(_UserProfileImage);
                avatarUri = data.getData();
            }
        }

    }
}