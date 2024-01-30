package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberResponse;
import com.gpsgetwoweducation.repo.RegisteredMobileNumberRepo;

public class RegisteredMobileNumberViewModel extends AndroidViewModel {
    RegisteredMobileNumberRepo registeredMobileNumberRepo;

    public RegisteredMobileNumberViewModel(@NonNull Application application) {
        super(application);
        registeredMobileNumberRepo = new RegisteredMobileNumberRepo();
    }

    public MutableLiveData<RegisteredMobileNumberResponse> getRegisteredMobileNumberLiveData(String registered_mobile_number, String registered_mobile_country_code, String app_name) {
        return registeredMobileNumberRepo.getRegisteredMobileNumberLiveData(registered_mobile_number, registered_mobile_country_code, app_name);
    }

}
