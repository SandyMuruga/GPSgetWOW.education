package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.insertgpstrackingdata.InsertGpsTrackingResponse;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.SendGpsDataBody;
import com.gpsgetwoweducation.pojo.userlogin.UserLoginResponse;
import com.gpsgetwoweducation.repo.InsertGpsTrackingRepo;
import com.gpsgetwoweducation.repo.UserLoginRepo;

public class InsertGpsTrackingViewModel extends AndroidViewModel {
    InsertGpsTrackingRepo insertGpsTrackingRepo;

    public InsertGpsTrackingViewModel(@NonNull Application application) {
        super(application);
        insertGpsTrackingRepo = new InsertGpsTrackingRepo();
    }

    public MutableLiveData<InsertGpsTrackingResponse> sendGpsLiveData(SendGpsDataBody sendGpsDataBody) {
        return insertGpsTrackingRepo.sendGpsLiveData(sendGpsDataBody);
    }

}
