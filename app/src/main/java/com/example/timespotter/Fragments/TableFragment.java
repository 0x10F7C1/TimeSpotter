package com.example.timespotter.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timespotter.Adapters.TableAdapter;
import com.example.timespotter.DataModels.TableItem;
import com.example.timespotter.DbContexts.TableFragmentDb;
import com.example.timespotter.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class TableFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<TableItem> tableItems;
    private TableAdapter adapter;

    public TableFragment() {
    }

    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        tableItems = new ArrayList<>();
        adapter = new TableAdapter(requireContext(), tableItems);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        new TableFragmentDb().loadTableEntries();

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTableEntryAdded(TableItem entry) {
        int position = tableItems.size();
        tableItems.add(entry);
        adapter.notifyItemInserted(position);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}