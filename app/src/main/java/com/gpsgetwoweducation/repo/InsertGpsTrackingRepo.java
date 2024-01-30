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
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.InsertGpsTrackingResponse;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.SendGpsDataBody;
import com.gpsgetwoweducation.pojo.userlogin.UserLoginResponse;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertGpsTrackingRepo {
    private GPSgetWOWAppApi gpSgetWOWAppApi;
    public InsertGpsTrackingRepo() {
        gpSgetWOWAppApi = ApiClient.getInsertGPSTracking().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<InsertGpsTrackingResponse> sendGpsLiveData(SendGpsDataBody sendGpsDataBody) {
        MutableLiveData<InsertGpsTrackingResponse> insertGpsTrackingResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.insertGpsTracking(sendGpsDataBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: " + response.body());
                    InsertGpsTrackingResponse insertGpsTrackingResponse = new Gson().fromJson(response.body().toString(), InsertGpsTrackingResponse.class);
                    insertGpsTrackingResponseMutableLiveData.setValue(insertGpsTrackingResponse);
                } else {
                    InsertGpsTrackingResponse insertGpsTrackingResponse = new InsertGpsTrackingResponse();
                    try {
                        insertGpsTrackingResponse.setMessage(ApiError.getApiError(response, App.getContext()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    insertGpsTrackingResponseMutableLiveData.setValue(insertGpsTrackingResponse);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                if (t instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSLHandshakeException: " + t.getMessage());
                } else {
                    InsertGpsTrackingResponse insertGpsTrackingResponse = new InsertGpsTrackingResponse();
                    insertGpsTrackingResponse.setMessage("Something went wrong!");
                    insertGpsTrackingResponseMutableLiveData.setValue(insertGpsTrackingResponse);
                }
            }
        });
        return insertGpsTrackingResponseMutableLiveData;
    }
}
