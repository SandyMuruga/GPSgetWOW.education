package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDataResponse;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopLiveCoordinatesData;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopTrackingDataResponse;
import com.gpsgetwoweducation.repo.PastTrackingDataRepo;
import com.gpsgetwoweducation.repo.StopTrackingDataRepo;

;

public class StopTrackingDataViewModel extends AndroidViewModel {
   StopTrackingDataRepo stopTrackingDataRepo;

    public StopTrackingDataViewModel(@NonNull Application application) {
        super(application);
        stopTrackingDataRepo = new StopTrackingDataRepo();
    }
    public MutableLiveData<StopTrackingDataResponse> stopTrackingDataLiveData(StopLiveCoordinatesData stopLiveCoordinatesData) {
        return stopTrackingDataRepo.stopTrackingDataLiveData(stopLiveCoordinatesData);
    }
}
