package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusResponse;;
import com.gpsgetwoweducation.repo.CurrentTrackingStatusRepo;

public class CurrentTrackingStatusViewModel extends AndroidViewModel {
    CurrentTrackingStatusRepo currentTrackingStatusRepo;

    public CurrentTrackingStatusViewModel(@NonNull Application application) {
        super(application);
        currentTrackingStatusRepo = new CurrentTrackingStatusRepo();
    }

    public MutableLiveData<CurrentTrackingStatusResponse> currentTrackingStatusLiveData(String country_code, String customer_id, String user_id) {
        return currentTrackingStatusRepo.getCurrentTrackingStatusLiveData(country_code, customer_id, user_id);
    }
}
