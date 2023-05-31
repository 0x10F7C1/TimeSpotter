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
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    private final List<UserLeaderboard> users;
    private final Context context;

    public LeaderboardAdapter(Context context, List<UserLeaderboard> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.leaderboard_item_layout, parent, false);
        return new LeaderboardViewHolder(itemView, context, users);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserLeaderboard user = users.get(position);
        holder.bindData(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
