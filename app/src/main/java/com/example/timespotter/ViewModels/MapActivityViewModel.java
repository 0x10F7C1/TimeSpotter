package com.example.timespotter.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Place;
import com.example.timespotter.DataModels.Result;
import com.example.timespotter.Repositories.LeaderboardRepository;
import com.example.timespotter.Repositories.PlaceRepository;

public class MapActivityViewModel extends AndroidViewModel {
    private final PlaceRepository _PlaceRepository;
    private final LeaderboardRepository _LeaderboardRepository;
    public MapActivityViewModel(@NonNull Application application) {
        super(application);
        _PlaceRepository = new PlaceRepository();
        _LeaderboardRepository = new LeaderboardRepository();
    }
    public MutableLiveData<Result<Integer>> getUserPoints() {
        return _LeaderboardRepository.getUserPoints();
    }
    public MutableLiveData<Result<Void>> getExcludeMarker() {
        return _PlaceRepository.getExcludeMarker();
    }
    public MutableLiveData<Result<Place>> getPlace() {
        return _PlaceRepository.getPlace();
    }
    public void updateUserPoints(String username, long pts) {
        _LeaderboardRepository.updateUserPoints(username, pts);
    }
    public void excludeUserMarker(String username, String placeKey) {
        _PlaceRepository.excludeUserMarker(username, placeKey);
    }
    public void loadMarkers(String username) {
        _PlaceRepository.loadMarkers(username);
    }

}
