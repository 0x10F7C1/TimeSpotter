package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
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
    private final CircleImageView _UserAvatar;
    private final TextView _UserUsername;
    private final TextView _UserName;
    private final TextView _UserPoints;
    private final ImageView _ImageStar;

    public LeaderboardViewHolder(@NonNull View itemView, Context context, List<UserLeaderboard> users) {
        super(itemView);
        _Context = context;
        _Users = users;

        _UserAvatar = itemView.findViewById(R.id.leaderboard_avatar);
        _UserUsername = itemView.findViewById(R.id.leaderboard_username);
        _UserName = itemView.findViewById(R.id.leaderboard_full_name);
        _UserPoints = itemView.findViewById(R.id.leaderboard_points);
        _ImageStar = itemView.findViewById(R.id.leaderboard_star_icon);
    }
    public void bindData(UserLeaderboard user) {
        if (getAdapterPosition() == 0) _ImageStar.setImageResource(R.drawable.first_place_leaderboard_star);
        else if (getAdapterPosition() == 1) _ImageStar.setImageResource(R.drawable.second_place_leaderboard_star);
        else if (getAdapterPosition() == 2) _ImageStar.setImageResource(R.drawable.third_place_leaderboard_star);
        Glide.with(_Context)
                .load(user.getImageUri())
                .into(_UserAvatar);
        _UserUsername.setText(user.getUsername());
        _UserName.setText(user.getName());
        _UserPoints.setText(String.valueOf(user.getPoints()));
    }
}
