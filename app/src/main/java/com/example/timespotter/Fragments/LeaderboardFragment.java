package com.example.timespotter.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.Adapters.LeaderboardAdapter;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.DbContexts.LeaderboardFragmentDb;
import com.example.timespotter.Events.LeaderboardFragmentEvent;
import com.example.timespotter.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardFragment extends Fragment {
    private final LeaderboardFragmentDb leaderboardFragmentDb = new LeaderboardFragmentDb();
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<UserLeaderboard> _Users;
    public LeaderboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        recyclerView = view.findViewById(R.id.leaderboard_recycler);
        _Users = new ArrayList<>();
        adapter = new LeaderboardAdapter(requireContext(), _Users);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        leaderboardFragmentDb.loadLeaderboard(_Users);

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLeaderboardLoaded(LeaderboardFragmentEvent.LeaderboardLoaded result) {
        _Users.sort(Collections.reverseOrder());
        adapter.notifyDataSetChanged();
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