package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.timespotter.DataModels.UserLeaderboard;
import com.example.timespotter.R;
import com.github.abdularis.civ.CircleImageView;

import java.util.List;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    private final Context _Context;
    private final List<UserLeaderboard> _Users;
    private CircleImageView _UserAvatar;
    private TextView _UserUsername, _UserName, _UserPoints;
    public LeaderboardViewHolder(@NonNull View itemView, Context context, List<UserLeaderboard> users) {
        super(itemView);
        _Context = context;
        _Users = users;

        _UserAvatar = itemView.findViewById(R.id.leaderboard_avatar);
        _UserUsername = itemView.findViewById(R.id.leaderboard_username);
        _UserName = itemView.findViewById(R.id.leaderboard_full_name);
        _UserPoints = itemView.findViewById(R.id.leaderboard_points);
    }
    public void bindData(UserLeaderboard user) {
        Glide.with(_Context)
                .load("https://firebasestorage.googleapis.com/v0/b/timespotter-95d44.appspot.com/o/Place%20photos%2F15dcf1a9-d6c6-4475-ad29-156cd8c45f51?alt=media&token=67428a47-f8b2-49d0-9b39-cedafadc4a87")
                .into(_UserAvatar);
        _UserUsername.setText(user.getUsername());
        _UserName.setText(user.getName());
        _UserPoints.setText(String.valueOf(user.getPoints()));
    }
}
