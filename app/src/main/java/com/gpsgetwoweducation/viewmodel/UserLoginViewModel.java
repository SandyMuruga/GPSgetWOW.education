package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.pojo.userlogin.UserLoginResponse;
import com.gpsgetwoweducation.repo.UserLoginRepo;

public class UserLoginViewModel extends AndroidViewModel{
    UserLoginRepo userLoginRepo;

    public UserLoginViewModel(@NonNull Application application) {
        super(application);
        userLoginRepo = new UserLoginRepo();
    }

    public MutableLiveData<UserLoginResponse> getUserLoginLiveData(String user_id, String user_password, String app_name) {
        return userLoginRepo.getUserLoginLiveData(user_id, user_password, app_name);
    }

}
