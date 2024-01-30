package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusResponse;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDataResponse;
import com.gpsgetwoweducation.repo.CurrentTrackingStatusRepo;
import com.gpsgetwoweducation.repo.PastTrackingDataRepo;

;

public class PastTrackingDataViewModel extends AndroidViewModel {
    PastTrackingDataRepo pastTrackingDataRepo;

    public PastTrackingDataViewModel(@NonNull Application application) {
        super(application);
        pastTrackingDataRepo = new PastTrackingDataRepo();
    }

    public MutableLiveData<PastTrackingDataResponse> pastTrackingDataLiveData(String country_code, String customer_id, String user_id) {
        return pastTrackingDataRepo.getPastTrackingDataLiveData(country_code, customer_id, user_id);
    }
}
