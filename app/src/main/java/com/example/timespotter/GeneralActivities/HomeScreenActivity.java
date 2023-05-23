package com.example.timespotter.GeneralActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.DataModels.User;
import com.example.timespotter.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.Serializable;

public class HomeScreenActivity extends AppCompatActivity {

    private ChipNavigationBar _BottomNav;
    private LeaderboardFragment _LeaderboardFragment = new LeaderboardFragment();
    private ProfileFragment _Profile = new ProfileFragment();
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        user = (User) getIntent().getSerializableExtra("user");
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));
        _Profile.setArguments(bundle);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bindViews();
        registerCallbacks();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _LeaderboardFragment).commit();
    }

    private void bindViews() {
        _BottomNav = findViewById(R.id.bottom_nav);
    }

    private void registerCallbacks() {
        _BottomNav.setOnItemSelectedListener(this::bottomNavOnItemSelected);
    }

    private void bottomNavOnItemSelected(int position) {
        switch (position) {
            case R.id.leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _LeaderboardFragment).commit();
                break;
            case R.id.discover:
                Intent intent = new Intent(HomeScreenActivity.this, MapActivity.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                intent.putExtra("user", (Serializable) user);
                startActivity(intent);
                break;
            case R.id.profile:
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);
                _Profile.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Profile).commit();
                break;
            default:
                return;
        }
    }

}