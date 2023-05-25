package com.example.timespotter.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timespotter.DataModels.TableItem;
import com.example.timespotter.R;

public class TableViewHolder extends RecyclerView.ViewHolder {
    private TextView
            _TableUsername,
            _TablePlaceName,
            _TablePlacePhone,
            _TablePlaceWebsite,
            _TablePlaceStartTime,
            _TablePlaceCloseTime;

    public TableViewHolder(@NonNull View itemView) {
        super(itemView);
        _TableUsername = itemView.findViewById(R.id.table_user_name);
        _TablePlaceName = itemView.findViewById(R.id.table_place_name);
        _TablePlacePhone = itemView.findViewById(R.id.table_place_phone);
        _TablePlaceWebsite = itemView.findViewById(R.id.table_place_website);
        _TablePlaceStartTime = itemView.findViewById(R.id.table_place_start_time);
        _TablePlaceCloseTime = itemView.findViewById(R.id.table_place_close_time);
    }
    public void bindData(TableItem entry) {
        _TableUsername.setText(entry.getUsername());
        _TablePlaceName.setText(entry.getPlaceName());
        _TablePlacePhone.setText(entry.getPhone());
        _TablePlaceWebsite.setText(entry.getWebsite());
        _TablePlaceStartTime.setText(entry.getStartTime());
        _TablePlaceCloseTime.setText(entry.getCloseTime());
    }
}
