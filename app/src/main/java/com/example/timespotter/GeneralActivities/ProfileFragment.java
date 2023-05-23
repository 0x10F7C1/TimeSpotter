package com.example.timespotter.GeneralActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.timespotter.DataModels.User;
import com.example.timespotter.DbMediators.ProfileFragmentDb;
import com.example.timespotter.Events.MyProfileEvent;
import com.example.timespotter.R;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.material.textfield.TextInputLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

//TODO: RESITI BUG GDE JE SVIM POLJIMA .length() == 0 i onda azurira podatke
public class ProfileFragment extends Fragment {
    private static final int PHOTO_PICKER = 2;
    private Button _UpdateProfile, _LogoutProfile;
    private TextInputLayout _UsernameText, _EmailText, _PasswordText, _PhoneText;
    private TextView _UserUsernameText, _UserFullNameText, _UserPtsText;
    private CircleImageView _UserProfileImage;
    private User user;
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
        _LogoutProfile = view.findViewById(R.id.profile_logout);
        _UsernameText = view.findViewById(R.id.profile_username);
        _EmailText = view.findViewById(R.id.profile_email);
        _PhoneText = view.findViewById(R.id.profile_phone);
        _PasswordText = view.findViewById(R.id.profile_password);
        _UserUsernameText = view.findViewById(R.id.profile_username_textview);
        _UserFullNameText = view.findViewById(R.id.profile_full_name);
        _UserPtsText = view.findViewById(R.id.profile_pts);
        _UserProfileImage = view.findViewById(R.id.profile_image);
        user = (User) getArguments().getSerializable("user");

        _UserUsernameText.setText(user.getUsername());
        _UserFullNameText.setText(user.getName());
        _UserPtsText.setText(user.getPoints().toString());
        _UpdateProfile.setOnClickListener(this::updateProfileOnClick);
        _LogoutProfile.setOnClickListener(this::logoutProfileOnClick);
        _UserProfileImage.setOnClickListener(this::changeProfileImageOnClick);
        Glide.with(requireContext()).load("https://firebasestorage.googleapis.com/v0/b/timespotter-95d44.appspot.com/o/Place%20photos%2F15dcf1a9-d6c6-4475-ad29-156cd8c45f51?alt=media&token=67428a47-f8b2-49d0-9b39-cedafadc4a87")
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

        User newUser = User.copyUser(user);

        if (validateUsername(newUser, username)
                && validateEmail(newUser, email)
                && validatePassword(newUser, password)
                && validatePhoneNumber(newUser, phone)) {
            db.updateUserProfile(user, newUser);
        }
    }

    private void logoutProfileOnClick(View view) {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
            newUser.setUsername(user.getUsername());
            return true;
        } else if (!username.matches(noWhiteSpace)) {
            _UsernameText.setError("Whitespaces are not allowed");
            newUser.setUsername(user.getUsername());
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
        } else if (!phone.matches(noWhiteSpace)) {
            _PhoneText.setError("Whitespaces are not allowed");
            newUser.setPhone(user.getPhone());
            return false;
        } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(requireContext()).load(data.getData()).centerCrop().into(_UserProfileImage);
            }
        }

    }
}