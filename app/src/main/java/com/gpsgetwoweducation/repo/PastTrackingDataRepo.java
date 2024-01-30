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
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDataResponse;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastTrackingDataRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public PastTrackingDataRepo() {
        gpSgetWOWAppApi = ApiClient.getInsertGPSTracking().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<PastTrackingDataResponse> getPastTrackingDataLiveData(String country_code, String customer_id, String user_id) {
        MutableLiveData<PastTrackingDataResponse> pastTrackingDataResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.getPastTrackingData(country_code, customer_id, user_id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    PastTrackingDataResponse pastTrackingDataResponse = new Gson().fromJson(response.body(), PastTrackingDataResponse.class);
                    pastTrackingDataResponse.setStatus(true);
                    pastTrackingDataResponse.setMessage("Data Get Successfully");
                    pastTrackingDataResponseMutableLiveData.setValue(pastTrackingDataResponse);
                } else {
                    PastTrackingDataResponse pastTrackingDataResponse = new PastTrackingDataResponse();
                    pastTrackingDataResponse.setStatus(false);
                    pastTrackingDataResponse.setMessage("Internal server error");
                    try {
                        pastTrackingDataResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pastTrackingDataResponseMutableLiveData.setValue(pastTrackingDataResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    PastTrackingDataResponse pastTrackingDataResponse = new PastTrackingDataResponse();
                    pastTrackingDataResponse.setMessage("Internal server error");
                    pastTrackingDataResponseMutableLiveData.setValue(pastTrackingDataResponse);
                }
            }
        });
        return pastTrackingDataResponseMutableLiveData;
    }



}
