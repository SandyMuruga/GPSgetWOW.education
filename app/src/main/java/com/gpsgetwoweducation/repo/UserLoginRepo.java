package com.gpsgetwoweducation.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.networks.ApiClient;
import com.gpsgetwoweducation.networks.ApiError;
import com.gpsgetwoweducation.pojo.userlogin.UserLoginResponse;
import com.gpsgetwoweducation.App;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gpsgetwoweducation.networks.GPSgetWOWAppApi;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public UserLoginRepo() {
        gpSgetWOWAppApi = ApiClient.getUserLogin().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<UserLoginResponse> getUserLoginLiveData(String user_id, String user_password, String app_name) {
        MutableLiveData<UserLoginResponse> userLoginResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.getUserLogin(user_id, user_password, app_name);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    UserLoginResponse userLoginResponse = new Gson().fromJson(response.body().toString(), UserLoginResponse.class);
                    userLoginResponseMutableLiveData.setValue(userLoginResponse);
                } else {
                    UserLoginResponse userLoginResponse = new UserLoginResponse();
                    try {
                        userLoginResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userLoginResponseMutableLiveData.setValue(userLoginResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    UserLoginResponse userLoginResponse = new UserLoginResponse();
                    userLoginResponse.setMessage("Something went wrong!");
                    userLoginResponseMutableLiveData.setValue(userLoginResponse);
                }
            }
        });
        return userLoginResponseMutableLiveData;
    }
}
