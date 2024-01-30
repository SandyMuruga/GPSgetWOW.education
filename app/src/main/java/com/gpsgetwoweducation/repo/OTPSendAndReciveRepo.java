package com.gpsgetwoweducation.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.gpsgetwoweducation.networks.ApiClient;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPData;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPResponse;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPData;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gpsgetwoweducation.networks.GPSgetWOWAppApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPSendAndReciveRepo {
    GPSgetWOWAppApi gpSgetWOWAppApi;

    public OTPSendAndReciveRepo() {
        this.gpSgetWOWAppApi = ApiClient.getUserLogin().create(GPSgetWOWAppApi.class);
    }
    public MutableLiveData<SendOTPResponse> sendOTP(SendOTPData sendOTPData) {
        MutableLiveData<SendOTPResponse> sendOTPResponseMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call = gpSgetWOWAppApi.sendOTP(sendOTPData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    SendOTPResponse sendOTPResponse = new Gson().fromJson(response.body(), SendOTPResponse.class);
                    sendOTPResponse.setStatus(true);
                    sendOTPResponseMutableLiveData.setValue(sendOTPResponse);
                } else {
                    SendOTPResponse sendOTPResponse = new SendOTPResponse();
                    sendOTPResponse.setStatus(false);
                    sendOTPResponse.setMessage(response.message());
                    sendOTPResponseMutableLiveData.setValue(sendOTPResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                SendOTPResponse sendOTPResponse = new SendOTPResponse();
                sendOTPResponse.setStatus(false);
                sendOTPResponse.setMessage(t.getMessage());
                sendOTPResponseMutableLiveData.setValue(sendOTPResponse);
            }
        });
        return sendOTPResponseMutableLiveData;
    }


    // Verify OTP
    public MutableLiveData<VerifyOTPResponse> verifyOTP(VerifyOTPData verifyOTPData) {
        MutableLiveData<VerifyOTPResponse> verifyOTPDataMutableLiveData = new MutableLiveData<>();
        Call<JsonObject> call1 = gpSgetWOWAppApi.verifyOTP(verifyOTPData);
        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    VerifyOTPResponse verifyOTPResponse = new Gson().fromJson(response.body(), VerifyOTPResponse.class);
                    verifyOTPResponse.setStatus(true);
                    verifyOTPDataMutableLiveData.setValue(verifyOTPResponse);
                } else {
                    VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
                    verifyOTPResponse.setStatus(false);
                    verifyOTPResponse.setMessage(response.message());
                    verifyOTPDataMutableLiveData.setValue(verifyOTPResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
                verifyOTPResponse.setStatus(false);
                verifyOTPResponse.setMessage(t.getMessage());
                verifyOTPDataMutableLiveData.setValue(verifyOTPResponse);
            }
        });
        return verifyOTPDataMutableLiveData;
    }
}
