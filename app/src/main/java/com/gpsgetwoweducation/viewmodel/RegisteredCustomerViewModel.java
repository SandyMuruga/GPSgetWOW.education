package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.registeredcustomer.RegisteredCustomerResponse;
import com.gpsgetwoweducation.repo.RegisteredCustomerRepository;


public class RegisteredCustomerViewModel extends AndroidViewModel {

    RegisteredCustomerRepository apkRepository;

    public RegisteredCustomerViewModel(@NonNull Application application) {
        super(application);
        apkRepository = new RegisteredCustomerRepository();
    }
    public MutableLiveData<RegisteredCustomerResponse> getRegisteredCustomerLiveData(String current_latitude, String current_longitude) {
        return apkRepository.getRegisteredCustomerLiveData(current_latitude, current_longitude);
    }
}
