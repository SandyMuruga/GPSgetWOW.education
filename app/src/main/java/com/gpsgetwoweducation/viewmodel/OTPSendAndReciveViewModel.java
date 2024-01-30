package com.gpsgetwoweducation.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.gpsgetwoweducation.pojo.userlogin.SendOTPData;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPResponse;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPData;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPResponse;
import com.gpsgetwoweducation.repo.OTPSendAndReciveRepo;

public class OTPSendAndReciveViewModel extends AndroidViewModel {
    OTPSendAndReciveRepo sendOTPRepo;

    public OTPSendAndReciveViewModel(@NonNull Application application) {
        super(application);
        sendOTPRepo = new OTPSendAndReciveRepo();
    }
    // Send OTP
    public LiveData<SendOTPResponse> sendOTP(SendOTPData sendOTPData) {
        return sendOTPRepo.sendOTP(sendOTPData);
    }

    // Verify OTP
    public LiveData<VerifyOTPResponse> verifyOTP(VerifyOTPData verifyOTPData) {
        return sendOTPRepo.verifyOTP(verifyOTPData);
    }

}
