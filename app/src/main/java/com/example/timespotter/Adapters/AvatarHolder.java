package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.timespotter.DataModels.Avatar;
import com.example.timespotter.R;

import java.util.List;

public class AvatarHolder extends RecyclerView.ViewHolder {
    private final Context context;
    private final List<Avatar> avatars;
    private final ImageView avatarImage;

    public AvatarHolder(@NonNull View itemView, Context context, List<Avatar> avatars) {
        super(itemView);
        this.context = context;
        this.avatars = avatars;
        avatarImage = itemView.findViewById(R.id.avatar);
    }

    public void bindData(Avatar avatar) {
        //avatarImage.setImageResource(avatar.getAvatarId());
        Glide.with(context).load(avatar.getAvatarId()).into(avatarImage);
    }
}
