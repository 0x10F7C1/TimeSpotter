package com.example.timespotter.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.example.timespotter.Repositories.LeaderboardRepository;
import com.example.timespotter.Repositories.PlaceRepository;

public class MapActivityViewModel extends AndroidViewModel {
    private PlaceRepository _PlaceRepository;
    private LeaderboardRepository _LeaderboardRepository;
    public MapActivityViewModel(@NonNull Application application) {
        super(application);
        _PlaceRepository = new PlaceRepository();
        _LeaderboardRepository = new LeaderboardRepository();
    }
    public MutableLiveData<Result<Integer>> getUserPoints() {
        return _LeaderboardRepository.getUserPoints();
    }
    public void updateUserPoints(String username, long pts) {
        _LeaderboardRepository.updateUserPoints(username, pts);
    }
}
