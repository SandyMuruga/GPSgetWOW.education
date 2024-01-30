package com.gpsgetwoweducation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.adapter.RegisteredUsersAdapter;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberData;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPData;
import com.gpsgetwoweducation.pojo.userlogin.SendOTPResponse;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPData;
import com.gpsgetwoweducation.pojo.userlogin.VerifyOTPResponse;
import com.gpsgetwoweducation.utilities.AppAdminListUtility;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.viewmodel.OTPSendAndReciveViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import java.util.List;

public class ChooseRegisteredUser extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private AppCompatButton login_btn_two;
    private AppCompatTextView tv_registered_users;
    SharedPreferenceClass sharedPreferenceClass;
    private RecyclerView rv_registered_users;
    private AppCompatImageView iv_back;
    private RegisteredUsersAdapter registeredUsersAdapter;
    private List<RegisteredMobileNumberData> registeredMobileNumberData;
    private AppCompatImageView iv_registered_user_image, iv_customer_logo;
    private OTPSendAndReciveViewModel otpSendAndReciveViewModel;
    AlertDialog dialog;
    int desiredPosition = 0;
    String number_of_count, verification_otp;
    private LinearLayout noInternetLayout;
    private NetworkChangeReceiver networkChangeReceiver;
    private ProgressDialogHUD mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_registered_user);
        //Objects.requireNonNull(getSupportActionBar()).hide();
        sharedPreferenceClass = new SharedPreferenceClass(this);

        rv_registered_users = findViewById(R.id.rv_registered_users);
        tv_registered_users = findViewById(R.id.tv_registered_users);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        iv_back = findViewById(R.id.iv_back);
        mProgressDialog = new ProgressDialogHUD(this);
        noInternetLayout = findViewById(R.id.layoutNoInternet);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

       /* String isLogin = sharedPreferenceClass.get("isLogin");
        if (isLogin.equalsIgnoreCase("yes")) {
            Intent intent = new Intent(ChooseRegisteredUser.this, HomePage.class);
            startActivity(intent);
            finish();
        }*/

        AppCompatTextView tv_title_name = (AppCompatTextView) findViewById(R.id.tv_title_name);
        String businessName = sharedPreferenceClass.get("businessName");
        if (businessName != null) {
            tv_title_name.setText(businessName);
        } else {
            tv_title_name.setText("Select Your Registered Business");
        }
        String numberOfCount = sharedPreferenceClass.get("number_of_count");
        String registeredNumber = sharedPreferenceClass.get("registered_mobile_number");
        String countryCode = sharedPreferenceClass.get("country_code");

        String country_code1 = sharedPreferenceClass.get("country_code1");
        String customer_id = sharedPreferenceClass.get("customer_id");
        String sub_domain_name = sharedPreferenceClass.get("sub_domain_name");
        if (businessName != null) {
            tv_title_name.setText(businessName);
        } else {
            tv_title_name.setText("Select Your Registered Business");
        }

        if (country_code1 != null) {
            country_code1 = country_code1.toLowerCase();
        }
        String imageUrl = "https://cephapi.getster.tech/api/storage/" + country_code1 + "-" + customer_id + "/" + sub_domain_name + "-" + "icon-128x128.png";

        CustomerLogoUtil.loadImage(this, imageUrl, iv_customer_logo);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // tv_registered_users.setText("Please select from the following users registered with the mobile number:" + " " + countryCode + " " + registeredNumber);
        String text = "Please select from the following users registered with the mobile number: " + "+" + countryCode + " " + registeredNumber;
        SpannableString spannableString = new SpannableString(text);
        int grayColor = ContextCompat.getColor(this, R.color.colorGray);
        int startIndexCountryCode = text.indexOf(countryCode);
        int endIndexCountryCode = startIndexCountryCode + countryCode.length();
        spannableString.setSpan(new ForegroundColorSpan(grayColor), startIndexCountryCode, endIndexCountryCode, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndexRegisteredNumber = text.indexOf(registeredNumber);
        int endIndexRegisteredNumber = startIndexRegisteredNumber + registeredNumber.length();
        spannableString.setSpan(new ForegroundColorSpan(grayColor), startIndexRegisteredNumber, endIndexRegisteredNumber, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_registered_users.setText(spannableString);

        login_btn_two = (AppCompatButton) findViewById(R.id.login_btn_two);
        login_btn_two.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            List<RegisteredMobileNumberData> validUserData = (List<RegisteredMobileNumberData>) bundle.getSerializable("user_data_list");
            if (validUserData != null) {
                registeredMobileNumberData = validUserData;
                registeredUsersAdapter = new RegisteredUsersAdapter(this, registeredMobileNumberData);
                rv_registered_users.setLayoutManager(new LinearLayoutManager(this));
                registeredUsersAdapter.setOnRadioButtonClickListener(new RegisteredUsersAdapter.OnRadioButtonClickListener() {
                    @Override
                    public void onRadioButtonClick(int position) {
                        if (position >= 0 && position < registeredMobileNumberData.size()) {
                            desiredPosition = position;
                            login_btn_two.setVisibility(View.VISIBLE);
                        }
                        Log.d("RadioButtonClick", "Selected position: " + desiredPosition);
                    }
                });
                rv_registered_users.setAdapter(registeredUsersAdapter);
            }
        }
        login_btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registeredMobileNumberData.size() > 0 && desiredPosition < registeredMobileNumberData.size()) {
                    if ((Integer.parseInt(registeredMobileNumberData.get(desiredPosition).getNumber_of_failed_attempts_for_the_day()) >= 0 &&
                            Integer.parseInt(registeredMobileNumberData.get(desiredPosition).getNumber_of_failed_attempts_for_the_day()) <= 5)) {

                        String chooseUserID = registeredMobileNumberData.get(desiredPosition).getUser_id();
                        String chooseUserFirstName = registeredMobileNumberData.get(desiredPosition).getFirst_name();
                        String chooseUserLastName = registeredMobileNumberData.get(desiredPosition).getLast_name();

                        sharedPreferenceClass.set("chooseUserFirstName", chooseUserFirstName);
                        sharedPreferenceClass.set("chooseUserLastName", chooseUserLastName);
                        sharedPreferenceClass.set("chooseUserID", chooseUserID);

                        Intent intent = new Intent(ChooseRegisteredUser.this, FaceLogin.class);
                        startActivity(intent);
                    } else if ((Integer.parseInt(registeredMobileNumberData.get(desiredPosition).getNumber_of_failed_attempts_for_the_day()) >= 6) &&
                            Integer.parseInt(registeredMobileNumberData.get(desiredPosition).getNumber_of_failed_attempts_for_the_day()) <= 10) {
                        sendLoginOtp();
                        Log.d("DEBUG", "OTP popup called");
                    }
                }
            }
        });
    }

    // internet connection
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        updateInternetStatus(isConnected);
    }
    private void updateInternetStatus(boolean isConnected) {
        if (isConnected) {
            noInternetLayout.setVisibility(View.GONE);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }
    // end internet connection

    private void otpPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setCancelable(false);
        View customView = inflater.inflate(R.layout.pop_up_send_otp_login, null);
        AppCompatEditText et_otp = customView.findViewById(R.id.et_otp);
        AppCompatTextView tv_otp_count = customView.findViewById(R.id.tv_otp_count);
        AppCompatTextView tv_contact_your_administrator = customView.findViewById(R.id.tv_contact_your_administrator);
        AppCompatButton btn_login_otp = customView.findViewById(R.id.btn_login_otp);

        String message = "Your account shall be blocked, in case you continue to have failed login attempts and only your \n" +
                "APP Administrator can Unblock the same. ";
        SpannableString spannableString = new SpannableString(message);
        int blueColor = ContextCompat.getColor(this, R.color.colorPrimary);
        int grayColor = ContextCompat.getColor(this, R.color.colorGray);
        int startIndex = message.indexOf("APP Administrator");
        int endIndex = startIndex + "APP Administrator".length();
        spannableString.setSpan(new ForegroundColorSpan(blueColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(grayColor), 0, startIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(grayColor), endIndex, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_contact_your_administrator.setText(spannableString);
        tv_contact_your_administrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChooseRegisteredUser.this, "test", Toast.LENGTH_SHORT).show();
               // AppAdminListUtility.showAppAdminList(ChooseRegisteredUser.this);
            }
        });

        btn_login_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verification_otp = et_otp.getText().toString().trim();
                if (verification_otp.equalsIgnoreCase("")) {
                    Toast.makeText(ChooseRegisteredUser.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP();
                }
            }
        });

        int initialCountdown = 60;
        tv_otp_count.setText(String.valueOf(initialCountdown));
        new CountDownTimer(initialCountdown * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                tv_otp_count.setText(String.valueOf(secondsRemaining));
            }

            public void onFinish() {
                dialog.dismiss();
                finish();
                //
            }
        }.start();

        builder.setView(customView);
        dialog = builder.create();
        dialog.show();
    }

    private void sendLoginOtp() {
        SendOTPData sendOTPData = new SendOTPData();
        sendOTPData.setMobile_country_code(sharedPreferenceClass.get("country_code"));
        sendOTPData.setMobile_no(sharedPreferenceClass.get("registered_mobile_number"));
        sendOTPData.setApp_name(sharedPreferenceClass.get("sub_domain_name"));
        otpSendAndReciveViewModel = ViewModelProviders.of(ChooseRegisteredUser.this).get(OTPSendAndReciveViewModel.class);
        otpSendAndReciveViewModel.sendOTP(sendOTPData).observe(ChooseRegisteredUser.this, new Observer<SendOTPResponse>() {
            @Override
            public void onChanged(SendOTPResponse sendOTPResponse) {
                if (sendOTPResponse != null && sendOTPResponse.isStatus()) {
                    Toast.makeText(ChooseRegisteredUser.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                    otpPopUp();
                }
            }
        });
    }

    private void verifyOTP() {
        VerifyOTPData verifyOTPData = new VerifyOTPData();
        verifyOTPData.setMobile_country_code(sharedPreferenceClass.get("country_code"));
        verifyOTPData.setMobile_no(sharedPreferenceClass.get("registered_mobile_number"));
        verifyOTPData.setVerification_code(verification_otp);
        otpSendAndReciveViewModel = ViewModelProviders.of(ChooseRegisteredUser.this).get(OTPSendAndReciveViewModel.class);
        otpSendAndReciveViewModel.verifyOTP(verifyOTPData).observe(ChooseRegisteredUser.this, new Observer<VerifyOTPResponse>() {
            @Override
            public void onChanged(VerifyOTPResponse verifyOTPResponse) {
                if (verifyOTPResponse != null && verifyOTPResponse.isStatus()) {
                    Toast.makeText(ChooseRegisteredUser.this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChooseRegisteredUser.this, HomePage.class));
                    dialog.dismiss();
                    finish();
                } else {
                    Toast.makeText(ChooseRegisteredUser.this, "OTP entered is wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*    private void showAppAdminList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setCancelable(false);
        View customView = inflater.inflate(R.layout.dialog_app_administrator_list, null);
        RecyclerView rv_app_administrator_list = customView.findViewById(R.id.rv_app_administrator_list);
        List<AppAdministratorListData> appAdministratorListData = new ArrayList<>();

        AppAdministratorAdapter appAdminAdapter = new AppAdministratorAdapter(this, appAdministratorListData);
        rv_app_administrator_list.setAdapter(appAdminAdapter);
        rv_app_administrator_list.setLayoutManager(new LinearLayoutManager(this));

        builder.setView(customView);
        dialog = builder.create();
        dialog.show();
    }*/

    //progress dialog
    public void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) dismissProgress();
        mProgressDialog = ProgressDialogHUD.show(this, msg, true, false, null);
    }

    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}

