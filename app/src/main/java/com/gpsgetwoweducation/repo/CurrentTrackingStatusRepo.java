package com.gpsgetwoweducation.repo;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gpsgetwoweducation.App;
import com.gpsgetwoweducation.networks.ApiClient;
import com.gpsgetwoweducation.networks.ApiError;
import com.gpsgetwoweducation.networks.GPSgetWOWAppApi;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusResponse;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CurrentTrackingStatusRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public CurrentTrackingStatusRepo() {
        gpSgetWOWAppApi = ApiClient.getInsertGPSTracking().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<CurrentTrackingStatusResponse> getCurrentTrackingStatusLiveData(String country_code, String customer_id, String user_id) {
        MutableLiveData<CurrentTrackingStatusResponse> currentTrackingStatusResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.getCurrentStatus(country_code, customer_id, user_id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    CurrentTrackingStatusResponse currentTrackingStatusResponse = new Gson().fromJson(response.body(), CurrentTrackingStatusResponse.class);
                    currentTrackingStatusResponse.setStatus(true);
                    currentTrackingStatusResponse.setMessage("Data Get Successfully");
                    currentTrackingStatusResponseMutableLiveData.setValue(currentTrackingStatusResponse);
                } else {
                    CurrentTrackingStatusResponse currentTrackingStatusResponse = new CurrentTrackingStatusResponse();
                    currentTrackingStatusResponse.setStatus(false);
                    currentTrackingStatusResponse.setMessage("Your Mobile Number is not Registered.");
                    try {
                        currentTrackingStatusResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    currentTrackingStatusResponseMutableLiveData.setValue(currentTrackingStatusResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    CurrentTrackingStatusResponse currentTrackingStatusResponse = new CurrentTrackingStatusResponse();
                    currentTrackingStatusResponse.setMessage("Something went wrong!");
                    currentTrackingStatusResponseMutableLiveData.setValue(currentTrackingStatusResponse);
                }
            }
        });
        return currentTrackingStatusResponseMutableLiveData;
    }
}
