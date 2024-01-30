package com.gpsgetwoweducation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredLoginData;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberData;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.viewmodel.RegisteredMobileNumberViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;
import com.hbb20.CountryCodePicker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnterRegisteredMobileNumber extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener{
    private AppCompatButton btn_next_registered_mobile_no;

    private SharedPreferenceClass sharedPreferenceClass;
    private AppCompatEditText et_register_mobile_no;
    private RegisteredMobileNumberViewModel registeredMobileNumberViewModel;
    private RegisteredLoginData registeredMobileNumberData;
    private CountryCodePicker countryCodePicker;
    private AppCompatImageView iv_back, iv_registered_customer_logo, iv_customer_logo;
    String registeredMobileNumber, countryCode;
    AlertDialog dialog;
    private LinearLayout noInternetLayout;
    private NetworkChangeReceiver networkChangeReceiver;
    private String businessName, country_code, customer_id, sub_domain_name;

    private ProgressDialogHUD mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_registered_mobile_number);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        mProgressDialog = new ProgressDialogHUD(this);
        registeredMobileNumberViewModel = ViewModelProviders.of(this).get(RegisteredMobileNumberViewModel.class);

        countryCodePicker = findViewById(R.id.ccp);
        btn_next_registered_mobile_no = findViewById(R.id.btn_next_registered_mobile_no);
        iv_back = findViewById(R.id.iv_back);
        et_register_mobile_no = findViewById(R.id.et_register_mobile_no);
        iv_registered_customer_logo = findViewById(R.id.iv_registered_customer_logo);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        noInternetLayout = findViewById(R.id.layoutNoInternet);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        AppCompatTextView tv_title_name = (AppCompatTextView) findViewById(R.id.tv_title_name);
        businessName = sharedPreferenceClass.get("businessName");
        country_code = sharedPreferenceClass.get("country_code1");

        customer_id = sharedPreferenceClass.get("customer_id");
        sub_domain_name = sharedPreferenceClass.get("sub_domain_name");
        if (businessName != null) {
            tv_title_name.setText(businessName);
        } else {
            tv_title_name.setText("Select Your Registered Business");
        }

        String isLogin = sharedPreferenceClass.get("isLogin");
        if (isLogin.equalsIgnoreCase("yes")) {
            Intent intent = new Intent(EnterRegisteredMobileNumber.this, HomePage.class);
            startActivity(intent);
            finish();
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (country_code != null) {
            country_code = country_code.toLowerCase();
        }

        String imageUrl = "https://cephapi.getster.tech/api/storage/" + country_code + "-" + customer_id + "/" + sub_domain_name + "-" + "icon-128x128.png";

        CustomerLogoUtil.loadImage(this, imageUrl, iv_customer_logo);
        CustomerLogoUtil.loadImage(this, imageUrl, iv_registered_customer_logo);

        countryCodePicker.setDefaultCountryUsingNameCode("IN");
        countryCodePicker.setClickable(false);

        btn_next_registered_mobile_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_register_mobile_no.getText().toString().trim().isEmpty()) {
                    showSnackbar(et_register_mobile_no, "Please enter registered mobile number");
                } else {
                    registeredMobileNumber = et_register_mobile_no.getText().toString().trim();
                    countryCode = countryCodePicker.getSelectedCountryCode();
                    sharedPreferenceClass.set("country_code", countryCode);
                    getRegisteredMobileNumberData();
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

    private void getRegisteredMobileNumberData() {
        showProgress("Loading...");
        registeredMobileNumberViewModel.getRegisteredMobileNumberLiveData(registeredMobileNumber, countryCode, sharedPreferenceClass.get("sub_domain_name")).observe(this, registeredMobileNumberResponse -> {
            dismissProgress();
            if (registeredMobileNumberResponse != null) {
                if (registeredMobileNumberResponse.getData() != null && !registeredMobileNumberResponse.getData().getUser_login_data().isEmpty()) {

                    sharedPreferenceClass.set("registered_customer_id", registeredMobileNumberResponse.getData().getCustomer_id());
                    sharedPreferenceClass.set("registered_country_code", registeredMobileNumberResponse.getData().getCountry_code());

                    List<RegisteredMobileNumberData> registeredLoginData = registeredMobileNumberResponse.getData().getUser_login_data();
                    if (registeredLoginData != null && !registeredLoginData.isEmpty()) {
                        List<RegisteredMobileNumberData> validUserData = new ArrayList<>();
                        for (RegisteredMobileNumberData userData : registeredLoginData) {
                            if (userData != null && userData.getRegistered_mobile_no().equals(registeredMobileNumber)) {
                                validUserData.add(userData);
                            }
                        }
                        if (!validUserData.isEmpty()) {
                            for (int i = 0; i < validUserData.size(); i++) {
                                sharedPreferenceClass.set("country_code", validUserData.get(i).getRegistered_mobile_country_code());
                                sharedPreferenceClass.set("registered_mobile_number", validUserData.get(i).getRegistered_mobile_no());
                                sharedPreferenceClass.set("user_id", validUserData.get(i).getUser_id());
                                sharedPreferenceClass.set("number_of_count", validUserData.get(i).getNumber_of_failed_attempts_for_the_day());
                                sharedPreferenceClass.set("first_name", validUserData.get(i).getFirst_name());
                                sharedPreferenceClass.set("last_name", validUserData.get(i).getLast_name());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user_data_list", (Serializable) validUserData);

                            Intent intent = new Intent(EnterRegisteredMobileNumber.this, ChooseRegisteredUser.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            noneOfTheStudentAndUserPopUp();
                        }
                    }
                } else {
                    // Toast.makeText(this, "Your Mobile Number is not Registered.", Toast.LENGTH_SHORT).show();
                    notRegisteredMobileNumberPopUp();
                }
            } else {
                //Toast.makeText(this, "Your Mobile Number is not Registered.", Toast.LENGTH_SHORT).show();
                notRegisteredMobileNumberPopUp();
            }
        });
    }

    private void notRegisteredMobileNumberPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.pop_up_not_registered_mobile_number, null);
        AppCompatImageView iv_registered_customer_logo = customView.findViewById(R.id.iv_registered_customer_logo);
        AppCompatTextView tv_not_registered_mobile_number = customView.findViewById(R.id.tv_registered_customer_mobile_number);
        AppCompatTextView tv_contact_your_administrator = customView.findViewById(R.id.tv_contact_your_administrator);
        AppCompatButton btn_new_register = customView.findViewById(R.id.btn_new_register);
        tv_not_registered_mobile_number.setText(registeredMobileNumber);


        String imageUrl = "https://cephapi.getster.tech/api/storage/" +
                country_code + "-" +
                customer_id + "/" +
                sub_domain_name + "-" +
                "icon-128x128.png";

        CustomerLogoUtil.loadImage(this, imageUrl, iv_registered_customer_logo);

        String message = "Please contact your APP Administrator in case you are unable to resolve the issue.";
        SpannableString spannableString = new SpannableString(message);
        int blueColor = ContextCompat.getColor(this, R.color.colorPrimary);
        int grayColor = ContextCompat.getColor(this, R.color.colorGray);
        spannableString.setSpan(new ForegroundColorSpan(grayColor), 0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndex = message.indexOf("APP Administrator");
        int endIndex = startIndex + "APP Administrator".length();
        spannableString.setSpan(new ForegroundColorSpan(blueColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_contact_your_administrator.setText(spannableString);
        tv_contact_your_administrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AppAdminListUtility.showAppAdminList(EnterRegisteredMobileNumber.this);
            }
        });

        btn_new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sub_domain_name = sharedPreferenceClass.get("sub_domain_name");
                String subdomain = "https://" + sub_domain_name + ".getwow.education/#/launch-app/registration1";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(subdomain));
                startActivity(intent);
            }
        });

        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void noneOfTheStudentAndUserPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setCancelable(false);
        View customView = inflater.inflate(R.layout.pop_up_non_of_the_user_student, null);
        AppCompatTextView tv_contact_your_administrator = customView.findViewById(R.id.tv_contact_your_administrator);
        AppCompatButton btn_exit = customView.findViewById(R.id.btn_exit);

        String message = "Please ask your APP Administrators to enable access to getWOW.study app for your registered student user category.";
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
                // Toast.makeText(RegisteredMobileNumber.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setView(customView);
        dialog = builder.create();
        dialog.show();
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();
    }

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