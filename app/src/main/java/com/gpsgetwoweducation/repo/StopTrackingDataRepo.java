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
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDataResponse;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopLiveCoordinatesData;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopTrackingDataResponse;
import com.gpsgetwoweducation.viewmodel.PastTrackingDataViewModel;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopTrackingDataRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public StopTrackingDataRepo() {
        gpSgetWOWAppApi = ApiClient.getInsertGPSTracking().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<StopTrackingDataResponse> stopTrackingDataLiveData(StopLiveCoordinatesData stopLiveCoordinatesData) {
        MutableLiveData<StopTrackingDataResponse> stopTrackingDataResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.stopLiveCoordinates(stopLiveCoordinatesData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    StopTrackingDataResponse stopTrackingDataResponse = new Gson().fromJson(response.body(), StopTrackingDataResponse.class);
                    stopTrackingDataResponse.setStatus(true);
                    stopTrackingDataResponse.setMessage("Data Get Successfully");
                    stopTrackingDataResponseMutableLiveData.setValue(stopTrackingDataResponse);
                } else {
                    StopTrackingDataResponse stopTrackingDataResponse = new StopTrackingDataResponse();
                    stopTrackingDataResponse.setStatus(false);
                    stopTrackingDataResponse.setMessage("Internal server error");
                    try {
                        stopTrackingDataResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stopTrackingDataResponseMutableLiveData.setValue(stopTrackingDataResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    StopTrackingDataResponse stopTrackingDataResponse = new StopTrackingDataResponse();
                    stopTrackingDataResponse.setMessage("Internal server error");
                    stopTrackingDataResponseMutableLiveData.setValue(stopTrackingDataResponse);
                }
            }
        });
        return stopTrackingDataResponseMutableLiveData;
    }
}
