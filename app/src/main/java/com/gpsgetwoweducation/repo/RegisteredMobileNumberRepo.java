package com.gpsgetwoweducation.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.App;
import com.gpsgetwoweducation.networks.ApiClient;
import com.gpsgetwoweducation.networks.ApiError;
import com.gpsgetwoweducation.networks.GPSgetWOWAppApi;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredMobileNumberRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public RegisteredMobileNumberRepo() {
        gpSgetWOWAppApi = ApiClient.getRegisteredCustomerMobileNumber().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<RegisteredMobileNumberResponse> getRegisteredMobileNumberLiveData(String registered_mobile_number, String registered_mobile_country_code, String app_name) {
        MutableLiveData<RegisteredMobileNumberResponse> registeredMobileNumberResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.getRegisteredCustomerMobileNumber(registered_mobile_number, registered_mobile_country_code, app_name);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    RegisteredMobileNumberResponse registeredMobileNumberResponse = new Gson().fromJson(response.body(), RegisteredMobileNumberResponse.class);
                    registeredMobileNumberResponse.setStatus(true);
                    registeredMobileNumberResponse.setMessage("Data Get Successfully");
                    registeredMobileNumberResponseMutableLiveData.setValue(registeredMobileNumberResponse);
                } else {
                    RegisteredMobileNumberResponse registeredMobileNumberResponse = new RegisteredMobileNumberResponse();
                    registeredMobileNumberResponse.setStatus(false);
                    registeredMobileNumberResponse.setMessage("Your Mobile Number is not Registered.");
                    try {
                        registeredMobileNumberResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registeredMobileNumberResponseMutableLiveData.setValue(registeredMobileNumberResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    RegisteredMobileNumberResponse registeredMobileNumberResponse = new RegisteredMobileNumberResponse();
                    registeredMobileNumberResponse.setMessage("Something went wrong!");
                    registeredMobileNumberResponseMutableLiveData.setValue(registeredMobileNumberResponse);
                }
            }
        });
        return registeredMobileNumberResponseMutableLiveData;
    }
}
