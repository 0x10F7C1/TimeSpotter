package com.example.timespotter.GeneralActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.R;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeScreenActivity extends AppCompatActivity {

    private ChipNavigationBar _BottomNav;
    private LeaderboardFragment _LeaderboardFragment;
    private ProfileFragment _Profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        bindViews();
        registerCallbacks();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _LeaderboardFragment).commit();
    }

    private void bindViews() {
        _BottomNav = findViewById(R.id.bottom_nav);
    }

    private void registerCallbacks() {
        _BottomNav.setOnItemSelectedListener(this::bottomNavOnItemSelected);
        _LeaderboardFragment = new LeaderboardFragment();
        _Profile = new ProfileFragment();
    }

    private void bottomNavOnItemSelected(int position) {
        switch (position) {
            case R.id.leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _LeaderboardFragment).commit();
                break;
            case R.id.discover:
                Intent intent = new Intent(HomeScreenActivity.this, MapActivity.class);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                break;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Profile).commit();
                break;
            default:
                return;
        }
    }
}