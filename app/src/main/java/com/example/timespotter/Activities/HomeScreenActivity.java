package com.example.timespotter.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.timespotter.Fragments.LeaderboardFragment;
import com.example.timespotter.Fragments.ProfileFragment;
import com.example.timespotter.Fragments.TableFragment;
import com.example.timespotter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity {
    private static final String TAG = HomeScreenActivity.class.getSimpleName();
    private final LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final TableFragment tableFragment = new TableFragment();
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        bindViews();
        registerCallbacks();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, leaderboardFragment).commit();
    }

    private void bindViews() {
        bottomNav = findViewById(R.id.bottom_nav);
    }

    private void registerCallbacks() {
        bottomNav.setOnItemSelectedListener(this::bottomNavOnItemSelected);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean bottomNavOnItemSelected(@NonNull android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, leaderboardFragment).commit();
                return true;
            //break;
            case R.id.discover:
                Intent intent = new Intent(HomeScreenActivity.this, MapActivity.class);
                startActivity(intent);
                return true;
            case R.id.table:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, tableFragment).commit();
                return true;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragment).commit();
                return true;
            default:
                return false;
        }
    }
}