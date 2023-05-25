package com.example.timespotter.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.TableItem;
import com.example.timespotter.R;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {
    private List<TableItem> tableItems;
    private Context _Context;
    public TableAdapter(Context context, List<TableItem> tableItems) {
        _Context = context;
        this.tableItems = tableItems;
    }
    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(_Context).inflate(R.layout.table_layout_item, parent, false);
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
