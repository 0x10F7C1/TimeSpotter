package com.example.timespotter.GeneralActivities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.Adapters.LeaderboardAdapter;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardFragment extends Fragment {
    private static final long DUMMY_POINTS_GENERATOR = 100;
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private List<UserLeaderboard> _Users;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        return fragment;
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

        loadDummyUsers();
        return view;
    }

    private void loadDummyUsers() {
        _Users.add(new UserLeaderboard("Nesto", "Okeej", "", 100l));
        _Users.add(new UserLeaderboard("Nesto", "Okeej", "", 200l));
        _Users.add(new UserLeaderboard("Nesto", "Okeej", "", 90l));
        _Users.add(new UserLeaderboard("Nesto", "Okeej", "", 30l));
        sortDummyUsers();
        //adapter.notifyDataSetChanged();
    }

    private void sortDummyUsers() {
        Collections.sort(_Users, Collections.reverseOrder());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }
}