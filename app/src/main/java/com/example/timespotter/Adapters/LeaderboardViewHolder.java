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
    private final Context context;
    private final List<UserLeaderboard> users;
    private final CircleImageView userAvatar;
    private final TextView userUsername, userName, userPoints;
    private final ImageView imageStar;

    public LeaderboardViewHolder(@NonNull View itemView, Context context, List<UserLeaderboard> users) {
        super(itemView);
        this.context = context;
        this.users = users;

        userAvatar = itemView.findViewById(R.id.leaderboard_avatar);
        userUsername = itemView.findViewById(R.id.leaderboard_username);
        userName = itemView.findViewById(R.id.leaderboard_full_name);
        userPoints = itemView.findViewById(R.id.leaderboard_points);
        imageStar = itemView.findViewById(R.id.leaderboard_star_icon);
    }

    public void bindData(UserLeaderboard user) {
        if (getAdapterPosition() == 0)
            imageStar.setImageResource(R.drawable.first_place_leaderboard_star);
        else if (getAdapterPosition() == 1)
            imageStar.setImageResource(R.drawable.second_place_leaderboard_star);
        else if (getAdapterPosition() == 2)
            imageStar.setImageResource(R.drawable.third_place_leaderboard_star);
        Glide.with(context)
                .load(user.getImageUri())
                .into(userAvatar);
        userUsername.setText(user.getUsername());
        userName.setText(user.getName());
        userPoints.setText(String.valueOf(user.getPoints()));
    }
}
