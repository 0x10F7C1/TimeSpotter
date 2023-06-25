package com.example.timespotter.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.Utils.AppData;
import com.example.timespotter.DataModels.TableItem;
import com.example.timespotter.R;

public class TableViewHolder extends RecyclerView.ViewHolder {
    private final TextView tableUsername;
    private final TextView tablePlaceName;
    private final TextView tablePlacePhone;
    private final TextView tablePlaceWebsite;
    private final TextView tablePlaceStartTime;
    private final TextView tablePlaceCloseTime;

    public TableViewHolder(@NonNull View itemView) {
        super(itemView);
        tableUsername = itemView.findViewById(R.id.table_user_name);
        tablePlaceName = itemView.findViewById(R.id.table_place_name);
        tablePlacePhone = itemView.findViewById(R.id.table_place_phone);
        tablePlaceWebsite = itemView.findViewById(R.id.table_place_website);
        tablePlaceStartTime = itemView.findViewById(R.id.table_place_start_time);
        tablePlaceCloseTime = itemView.findViewById(R.id.table_place_close_time);
    }

    public void bindData(TableItem entry) {
        if (entry.getUsername().equals(AppData.user.getUsername())) {
            tableUsername.setText("Me");
        }
        else {
            tableUsername.setText(entry.getUsername());
        }
        tablePlaceName.setText(entry.getPlaceName());
        tablePlacePhone.setText(entry.getPhone());
        tablePlaceWebsite.setText(entry.getWebsite());
        tablePlaceStartTime.setText(entry.getStartTime());
        tablePlaceCloseTime.setText(entry.getCloseTime());
    }
}
