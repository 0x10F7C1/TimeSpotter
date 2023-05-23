package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.R;

import java.util.List;

//nek se u Leaderboard cvoru cuva (username, pts, link ka slici, ime)
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private final List<UserLeaderboard> _Users;
    private final Context _Context;

    public LeaderboardAdapter(Context context, List<UserLeaderboard> users) {
        _Context = context;
        _Users = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(_Context).inflate(R.layout.leaderboard_item_layout, parent, false);
        return new LeaderboardViewHolder(itemView, _Context, _Users);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserLeaderboard user = _Users.get(position);
        holder.bindData(user);
    }

    @Override
    public int getItemCount() {
        return _Users.size();
    }
}
