package com.gpsgetwoweducation.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.utilities.AppAdminListUtility;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.viewmodel.UserLoginViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class FaceLogin extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener{

  /*  private AppUsageViewModel appUsageViewModel;
    List<AppUsageData> appUsageDataList = new ArrayList<>();*/
    private AppCompatButton face_login_btn;
    private AppCompatEditText et_password;
    private UserLoginViewModel userLoginViewModel;
    private SharedPreferenceClass sharedPreferenceClass;
    private AppCompatTextView tv_remaining_attempts_count, tv_user_name;
    private String editPassword;
    private AppCompatImageView iv_customer_logo, iv_back, iv_user_logo, iv_eye_new;
    boolean isPasswordVisible = false;
    private LinearLayout noInternetLayout;
    private NetworkChangeReceiver networkChangeReceiver;
    private AppCompatTextView tv_app_admin;

    private ProgressDialogHUD mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        mProgressDialog = new ProgressDialogHUD(this);
        AppCompatTextView tv_title_name = (AppCompatTextView) findViewById(R.id.tv_title_name);
        String businessName = sharedPreferenceClass.get("businessName");
        if (businessName != null && businessName.equalsIgnoreCase("")) {
            tv_title_name.setText(businessName);
        } else {
            tv_title_name.setText("Select Your Registered Business");
        }

        sharedPreferenceClass = new SharedPreferenceClass(this);
        userLoginViewModel = ViewModelProviders.of(this).get(UserLoginViewModel.class);
        iv_back = findViewById(R.id.iv_back);
        et_password = findViewById(R.id.et_password);
        iv_user_logo = findViewById(R.id.iv_user_logo);
        //tv_remaining_attempts_count = findViewById(R.id.tv_remaining_attempts_count);
        editPassword = et_password.getText().toString().trim();
        face_login_btn = (AppCompatButton) findViewById(R.id.face_login_btn);
        tv_user_name = findViewById(R.id.tv_user_name);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        tv_app_admin = findViewById(R.id.tv_app_admin);
        iv_eye_new = findViewById(R.id.iv_eye_new);
        noInternetLayout = findViewById(R.id.layoutNoInternet);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        String user_first_name = sharedPreferenceClass.get("chooseUserFirstName");
        String user_last_name = sharedPreferenceClass.get("chooseUserLastName");

        tv_user_name.setText("Hello  " + user_first_name + " " + user_last_name);

        String fullText = "In case you forgot the password, Please contact your APP Administrators in order to reset the password.";

        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf("APP Administrators");
        int endIndex = startIndex + "APP Administrators".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_app_admin.setText(spannableString);
        tv_app_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AppAdminListUtility.showAppAdminList(FaceLogin.this);
            }
        });

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
        String customer_imageUrl = "https://cephapi.getster.tech/api/storage/" + country_code1 + "-" + customer_id + "/" + sub_domain_name + "-" + "icon-128x128.png";
        CustomerLogoUtil.loadImage(this, customer_imageUrl, iv_customer_logo);

        String userImage = "https://cephapi.getster.tech/api/storage/" +
                country_code1 + "-" +
                customer_id + "/" + sharedPreferenceClass.get("chooseUserID") + ".png";
        CustomerLogoUtil.loadImage(this, userImage, iv_user_logo);

        String isLogin = sharedPreferenceClass.get("isLogin");
        String isLogout = sharedPreferenceClass.get("isLogout");
        if (isLogin.equalsIgnoreCase("yes") && isLogout.equalsIgnoreCase("yes")) {
            finish();
            Intent intent = new Intent(FaceLogin.this, HomePage.class);
            startActivity(intent);
        }

        final Drawable hidePasswordDrawable = getResources().getDrawable(R.drawable.ic_hide_password);
        final Drawable showPasswordDrawable = getResources().getDrawable(R.drawable.ic_visible_eye);
        iv_eye_new.setImageDrawable(hidePasswordDrawable);
        iv_eye_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible = false;
                    iv_eye_new.setImageDrawable(hidePasswordDrawable);
                } else {
                    et_password.setTransformationMethod(null);
                    isPasswordVisible = true;
                    iv_eye_new.setImageDrawable(showPasswordDrawable);
                }
                et_password.setSelection(et_password.getText().length());
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        face_login_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                editPassword = et_password.getText().toString().trim();
                if (editPassword.equalsIgnoreCase("")) {
                    showSnackbar(et_password, "Please enter password");
                } else {
                    getUserLogin();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getUserLogin() {
        showProgress("Loading...");
        userLoginViewModel.getUserLoginLiveData(sharedPreferenceClass.get("chooseUserID"), editPassword, sharedPreferenceClass.get("sub_domain_name")).observe(this, userLoginResponse -> {
            dismissProgress();
            if (userLoginResponse != null && userLoginResponse.getData() != null) {
                if (userLoginResponse.getData().getToken() != null && userLoginResponse.getData().isIs_password_incorrect() == false) {
                    // Success
                    String jwtToken = userLoginResponse.getData().getToken();
                    String secretKey = "05b6b4a13eaaa8f6e4bf14b352fbf6dbaf102d1c1c25e4261faee6eae89a56ee";
                    try {
                        Jws<Claims> jws = Jwts.parserBuilder()
                                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                                .build()
                                .parseClaimsJws(jwtToken);

                        // Access claims from the token
                        Claims claims = jws.getBody();

                        Map<String, Object> userClaims = (Map<String, Object>) claims.get("user");
                        String userId = String.valueOf(userClaims.get("user_id"));
                        String customer_id = String.valueOf(userClaims.get("customer_id"));
                        String country_code = String.valueOf(userClaims.get("country_code"));
                        String registered_educational_institution_name = String.valueOf(userClaims.get("registered_educational_institution_name"));
                        String time_zone_iana_string = String.valueOf(userClaims.get("time_zone_iana_string"));
                        String app_name = String.valueOf(userClaims.get("app_name"));
                        String user_category_type = String.valueOf(userClaims.get("user_category_type"));
                        String user_registered_categories_ids = String.valueOf(userClaims.get("user_registered_categories_ids"));
                        String customer_type = String.valueOf(userClaims.get("customer_type"));

                        // Step 1: Split the string into an array using comma as the delimiter
                        String[] stringArray = user_category_type.split(",");

                        // Step 2: Iterate through the array and compare the values
                        for (String value : stringArray) {
                            if (value.equals("2")) {
                                sharedPreferenceClass.set("token_user_category_type", user_category_type);
                            }
                        }
                        sharedPreferenceClass.set("token_user_id", userId);
                        sharedPreferenceClass.set("token_customer_id", customer_id);
                        sharedPreferenceClass.set("token_country_code", country_code);
                        sharedPreferenceClass.set("token_registered_educational_institution_name", registered_educational_institution_name);
                        sharedPreferenceClass.set("token_user_registered_categories_ids", user_registered_categories_ids);
                        navigateToHomeActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) >= 5 &&
                                Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) <= 10)) {
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) > 0 &&
                                Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) < 5)) {
                    //finish();
                    startActivity(new Intent(FaceLogin.this, EnterRegisteredMobileNumber.class));
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) < 0)) {
                    Toast.makeText(this, "Blocked", Toast.LENGTH_SHORT).show();
                   /* String fullText = "Your today's Login Attempts exist. Please contact your APP Administrator.";
                    showAppAdminAlertDialog(fullText);*/
                }
            } else {
                Toast.makeText(this, "Please Check your Login Credentials", Toast.LENGTH_SHORT).show();
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

    private void navigateToHomeActivity() {
        sharedPreferenceClass.set("isLogin", "yes");
        sharedPreferenceClass.set("isLogout", "");
        Intent intent = new Intent(FaceLogin.this, HomePage.class);
        startActivity(intent);
        //insertStartDateTime();
        finish();
    }

    private String getFormattedDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();
    }

    private void showAppAdminAlertDialog(String message) {
        int startIndex = message.indexOf("App Administrator");

        // Check if the substring exists in the message
        if (startIndex != -1) {
            int endIndex = startIndex + "App Administrator".length();
            SpannableString spannableMessage = new SpannableString(message);
            ForegroundColorSpan blueColor = new ForegroundColorSpan(Color.BLUE);

            // Check if endIndex is within the bounds of the message
            if (endIndex <= message.length()) {
                spannableMessage.setSpan(blueColor, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Create a TextView
                TextView messageTextView = new TextView(this);
                messageTextView.setText(spannableMessage);
                messageTextView.setPadding(16, 16, 16, 16); // Add padding if needed

                // Create AlertDialog
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Login Attempts")
                        .setView(messageTextView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle OK button click
                                dialog.dismiss();
                            }
                        })
                        .create();

                // Show AlertDialog
                alertDialog.show();
            } else {
                // Handle the case when endIndex is out of bounds
                // Log or display an error message
            }
        } else {
            // Handle the case when the substring is not found
            // Log or display an error message
        }
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