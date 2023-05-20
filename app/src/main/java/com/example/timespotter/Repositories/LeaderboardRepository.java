package com.example.timespotter.Repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.timespotter.DataModels.Result;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LeaderboardRepository {
    private MutableLiveData<Result<Integer>> _UserPoints;
    private DatabaseReference database;

    public LeaderboardRepository() {
        _UserPoints = new MutableLiveData<>();
        database = FirebaseDatabase.getInstance().getReference();
    }
    public MutableLiveData<Result<Integer>> getUserPoints() {
        return _UserPoints;
    }
    public void updateUserPoints(String username, long pts) {
        final Result<Integer> result = new Result<>();
        database
                .child("Users")
                .child(username)
                .child("points")
                .get()
                .addOnSuccessListener(snapshot -> {
                    long newPts = snapshot.getValue(Long.class);
                    newPts += pts;
                    setNewUserPoints(username, newPts);
                })
                .addOnFailureListener(e -> {
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(e);
                });
    }
    private void setNewUserPoints(String username, long pts) {
        final Result<Integer> result = new Result<>();
        database
                .child("Users")
                .child(username)
                .child("points")
                .setValue(pts)
                .addOnSuccessListener(unused -> {
                    result.setOperationSuccess(Result.OPERATION_SUCCESS);
                    _UserPoints.postValue(result);
                })
                .addOnFailureListener(e -> {
                    result.setOperationSuccess(Result.OPERATION_FAILURE).setError(e);
                    _UserPoints.postValue(result);
                });
        updateLeaderboards(username, pts);
    }
    private void updateLeaderboards(String username, long pts) {
        final Result<Integer> result = new Result<>();
        database
                .child("Leaderboards")
                .child(username)
                .child("points")
                .setValue(pts)
                .addOnSuccessListener(unused -> {
                    result.setOperationSuccess(Result.OPERATION_SUCCESS);
                    _UserPoints.postValue(result);
                })
                .addOnFailureListener(e -> {
                    result.setOperationSuccess(Result.OPERATION_FAILURE);
                    _UserPoints.postValue(result);
                });
    }
}
