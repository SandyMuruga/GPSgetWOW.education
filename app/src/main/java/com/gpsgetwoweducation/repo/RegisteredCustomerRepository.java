package com.gpsgetwoweducation.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.App;
import com.gpsgetwoweducation.networks.ApiClient;
import com.gpsgetwoweducation.networks.ApiError;
import com.gpsgetwoweducation.networks.GPSgetWOWAppApi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gpsgetwoweducation.pojo.registeredcustomer.RegisteredCustomerResponse;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredCustomerRepository {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public RegisteredCustomerRepository() {
        gpSgetWOWAppApi = ApiClient.getClient().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<RegisteredCustomerResponse> getRegisteredCustomerLiveData(String current_latitude, String current_longitude) {
        MutableLiveData<RegisteredCustomerResponse> registeredCustomerResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.getRegisteredInstitution(current_latitude, current_longitude);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    RegisteredCustomerResponse registeredCustomerResponse = new Gson().fromJson(response.body(), RegisteredCustomerResponse.class);
                    registeredCustomerResponse.setStatus(true);
                    registeredCustomerResponse.setMessage("Data Get Successfully");
                    registeredCustomerResponseMutableLiveData.setValue(registeredCustomerResponse);
                } else {
                    RegisteredCustomerResponse registeredCustomerResponse = new RegisteredCustomerResponse();
                    try {
                        registeredCustomerResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registeredCustomerResponseMutableLiveData.setValue(registeredCustomerResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    RegisteredCustomerResponse registeredCustomerResponse = new RegisteredCustomerResponse();
                    registeredCustomerResponse.setMessage("Something went wrong!");
                    registeredCustomerResponseMutableLiveData.setValue(registeredCustomerResponse);
                }
            }
        });
        return registeredCustomerResponseMutableLiveData;
    }
}
