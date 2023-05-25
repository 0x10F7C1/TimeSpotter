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
    private final LeaderboardFragment _LeaderboardFragment = new LeaderboardFragment();
    private final ProfileFragment _Profile = new ProfileFragment();
    private final TableFragment _Table = new TableFragment();
    private BottomNavigationView _BottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //user = (User) getIntent().getSerializableExtra("user");
        Bundle bundle = new Bundle();
        //bundle.putString("username", getIntent().getStringExtra("username"));
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

    @SuppressLint("NonConstantResourceId")
    private boolean bottomNavOnItemSelected(@NonNull android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.leaderboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _LeaderboardFragment).commit();
                return true;
            //break;
            case R.id.discover:
                Intent intent = new Intent(HomeScreenActivity.this, MapActivity.class);
                //intent.putExtra("username", getIntent().getStringExtra("username"));
                //intent.putExtra("user", user);
                startActivity(intent);
                return true;
            case R.id.table:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Table).commit();
                return true;
            case R.id.profile:
                Bundle bundle = new Bundle();
                //bundle.putSerializable("user", user);
                _Profile.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, _Profile).commit();
                return true;
            //break;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println(TAG + " " + "Brisem se!");
        super.onDestroy();
    }

}