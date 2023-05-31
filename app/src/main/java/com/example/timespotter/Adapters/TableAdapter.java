package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.DataModels.TableItem;
import com.example.timespotter.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {
    private final List<TableItem> tableItems;
    private final Context context;

    public TableAdapter(Context context, List<TableItem> tableItems) {
        this.context = context;
        this.tableItems = tableItems;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.table_layout_item, parent, false);
        return new TableViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.bindData(tableItems.get(position));
    }

    @Override
    public int getItemCount() {
        return tableItems.size();
    }
}
