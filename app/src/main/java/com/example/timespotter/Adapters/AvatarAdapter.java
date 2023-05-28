package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.DataModels.Avatar;
import com.example.timespotter.R;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarHolder> {

    private final Context context;
    private final List<Avatar> avatars;

    public AvatarAdapter(Context context, List<Avatar> avatars) {
        this.context = context;
        this.avatars = avatars;
    }

    @NonNull
    @Override
    public AvatarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.avatar_recycler_item, parent, false);
        return new AvatarHolder(itemView, context, avatars);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarHolder holder, int position) {
        holder.bindData(avatars.get(position));
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }
}
