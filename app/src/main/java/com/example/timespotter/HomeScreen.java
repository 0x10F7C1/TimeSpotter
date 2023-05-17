package com.example.timespotter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeScreen extends AppCompatActivity {

    private ChipNavigationBar _BottomNav;
    private Discover _Discover;
    private Leaderboard _Leaderboard;
    private Profile _Profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        bindViews();
        registerCallbacks();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Leaderboard).commit();
    }

    private void bindViews() {
        _BottomNav = findViewById(R.id.bottom_nav);
    }
    private void registerCallbacks() {
        _BottomNav.setOnItemSelectedListener(this::bottomNavOnItemSelected);
        _Discover = new Discover();
        _Leaderboard = new Leaderboard();
        _Profile = new Profile();
    }
    private void bottomNavOnItemSelected(int position) {
        switch (position) {
            case R.id.leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Leaderboard).commit();
                break;
            case R.id.discover:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Discover).commit();
                Intent intent = new Intent(HomeScreen.this, MapActivity.class);
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