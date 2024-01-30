package com.gpsgetwoweducation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusData;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.SendGpsDataBody;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopLiveCoordinatesData;
import com.gpsgetwoweducation.roomdb.dao.GpsResponseDAO;
import com.gpsgetwoweducation.roomdb.dao.GpsTrackingDAO;
import com.gpsgetwoweducation.roomdb.database.GpsTrackingDB;
import com.gpsgetwoweducation.roomdb.entity.GpsResponseData;
import com.gpsgetwoweducation.roomdb.entity.GpsTrackingData;
import com.gpsgetwoweducation.services.LocationUpdatesService;
import com.gpsgetwoweducation.services.OverlayService;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.viewmodel.StopTrackingDataViewModel;
import com.gpsgetwoweducation.viewmodel.UserLoginViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

public class StopTracking extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private SharedPreferenceClass sharedPreferenceClass;
    private AppCompatImageView iv_customer_logo, iv_eye_new;
    private AppCompatEditText et_password;
    private AppCompatButton face_logout_btn;
    private AppCompatTextView tv_user_name;
    private NetworkChangeReceiver networkChangeReceiver;
    private LinearLayout noInternetLayout;
    boolean isPasswordVisible = false;
    private UserLoginViewModel userLoginViewModel;
    private StopTrackingDataViewModel stopTrackingDataViewModel;
    private ProgressDialogHUD mProgressDialog;
    private String businessName, country_code, customer_id, sub_domain_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_tracking);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        mProgressDialog = new ProgressDialogHUD(this);
        userLoginViewModel = ViewModelProviders.of(this).get(UserLoginViewModel.class);
        stopTrackingDataViewModel = ViewModelProviders.of(this).get(StopTrackingDataViewModel.class);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        AppCompatTextView tv_title_name = (AppCompatTextView) findViewById(R.id.tv_title_name);
        et_password = findViewById(R.id.et_password);
        iv_eye_new = findViewById(R.id.iv_eye_new);
        tv_user_name = findViewById(R.id.tv_user_name);
        face_logout_btn = (AppCompatButton) findViewById(R.id.face_logout_btn);

        noInternetLayout = findViewById(R.id.layoutNoInternet);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        businessName = sharedPreferenceClass.get("businessName");
        country_code = sharedPreferenceClass.get("country_code1");

        customer_id = sharedPreferenceClass.get("customer_id");
        sub_domain_name = sharedPreferenceClass.get("sub_domain_name");
        if (businessName != null) {
            tv_title_name.setText(businessName);
        } else {
            tv_title_name.setText("Select Your Registered Business");
        }
        if (country_code != null) {
            country_code = country_code.toLowerCase();
        }

        String imageUrl = "https://cephapi.getster.tech/api/storage/" + country_code + "-" + customer_id + "/" + sub_domain_name + "-" + "icon-128x128.png";
        CustomerLogoUtil.loadImage(this, imageUrl, iv_customer_logo);

        String user_first_name = sharedPreferenceClass.get("chooseUserFirstName");
        String user_last_name = sharedPreferenceClass.get("chooseUserLastName");

        tv_user_name.setText(user_first_name + " " + user_last_name);

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

        face_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_password.getText().toString().trim().length() == 0) {
                    Toast.makeText(StopTracking.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    getUserLogOut();
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

    private void getUserLogOut() {
       /* showProgress("Loading...");
        StopLiveCoordinatesData stopLiveCoordinatesData = new StopLiveCoordinatesData();
        stopLiveCoordinatesData.setCountry_code("in");
        stopLiveCoordinatesData.setCustomer_id("102");
        stopLiveCoordinatesData.setLogin_user_id("1");
        stopLiveCoordinatesData.setLatitude("13.041351913652589");
        stopLiveCoordinatesData.setLongitude("80.20362854003906");
        stopLiveCoordinatesData.setLatest_datetime("2024-01-04 15:54:05");
        stopLiveCoordinatesData.setGps_tracking_event_id("3");*/

        showProgress("Loading...");
        userLoginViewModel.getUserLoginLiveData(sharedPreferenceClass.get("chooseUserID"), et_password.getText().toString(), sharedPreferenceClass.get("sub_domain_name")).observe(this, userLoginResponse -> {
            dismissProgress();
            if (userLoginResponse != null && userLoginResponse.getData() != null) {
                if (userLoginResponse.getData().getToken() != null && userLoginResponse.getData().isIs_password_incorrect() == false) {
                    // Success
                    //SweetAlertUtil.showLogoutConfirmation(StopTracking.this, FaceLogin.class);
                    stopTracking();
                    startActivity(new Intent(StopTracking.this, HomePage.class));
                    sharedPreferenceClass.set("gps_tracking_event_id", "");
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) >= 5 &&
                                Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) <= 10)) {
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) > 0 &&
                                Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) < 5)) {
                    //finish();
                    startActivity(new Intent(StopTracking.this, EnterRegisteredMobileNumber.class));
                } else if (userLoginResponse.getData().getRemaining_attempts_count() != null &&
                        (Integer.parseInt(userLoginResponse.getData().getRemaining_attempts_count()) < 0)) {
                    Toast.makeText(this, "Blocked", Toast.LENGTH_SHORT).show();
                    String fullText = "Your today's Login Attempts exist. Please contact your APP Administrator.";
                    showAppAdminAlertDialog(fullText);
                }
            } else {
                Toast.makeText(this, "Please Check your Login Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAppAdminAlertDialog(String message) {
        int startIndex = message.indexOf("App Administrator");
        if (startIndex != -1) {
            int endIndex = startIndex + "App Administrator".length();
            SpannableString spannableMessage = new SpannableString(message);
            ForegroundColorSpan blueColor = new ForegroundColorSpan(getColor(R.color.colorPrimary));
            // Check if endIndex is within the bounds of the message
            if (endIndex <= message.length()) {
                spannableMessage.setSpan(blueColor, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // Create a TextView
                TextView messageTextView = new TextView(this);
                messageTextView.setText(spannableMessage);
                messageTextView.setPadding(16, 16, 16, 16);
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

    public void stopTracking() {
        OverlayService overlayService = OverlayService.getInstance();
        stopTrackingDataViewModel = ViewModelProviders.of(this).get(StopTrackingDataViewModel.class);
        StopLiveCoordinatesData stopLiveCoordinatesData = new StopLiveCoordinatesData();
        stopLiveCoordinatesData.setCountry_code(sharedPreferenceClass.get("token_country_code"));
        stopLiveCoordinatesData.setCustomer_id(sharedPreferenceClass.get("token_customer_id"));
        stopLiveCoordinatesData.setLogin_user_id(sharedPreferenceClass.get("token_user_id"));
        stopLiveCoordinatesData.setLongitude("13.041351");
        stopLiveCoordinatesData.setLatitude("80.203628");
        stopLiveCoordinatesData.setLatest_datetime("2024-01-04 15:54:05");
        stopLiveCoordinatesData.setGps_tracking_event_id(sharedPreferenceClass.get("gps_tracking_event_id"));

        stopTrackingDataViewModel.stopTrackingDataLiveData(stopLiveCoordinatesData).observe(this, stopTrackingDataResponse -> {
            if (stopTrackingDataResponse != null && stopTrackingDataResponse.getData() != null) {
                Log.e("stopTrackingData", stopTrackingDataResponse.getData().toString());
                Toast.makeText(this, "data sent", Toast.LENGTH_SHORT).show();

                Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
                stopService(serviceIntent);
                overlayService.removeOverlay();

                GpsTrackingDB appDatabase = GpsTrackingDB.getInstance(this);
                GpsResponseDAO gpsResponseDAO = appDatabase.getGpsResponseDAO();
                DeleteAllDataLocalDBAsyncTask deleteAllDataAsyncTask = new DeleteAllDataLocalDBAsyncTask(gpsResponseDAO);
                deleteAllDataAsyncTask.execute();

                /*if (mService != null) {
                    mService.removeLocationUpdates();
                    sharedPreferenceClass.set("gps_tracking_event_id", "");
                }*/
            } else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static class DeleteAllDataLocalDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private GpsResponseDAO gpsResponseDAO;

        DeleteAllDataLocalDBAsyncTask(GpsResponseDAO dao) {
            this.gpsResponseDAO = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            gpsResponseDAO.deleteAllData();
            return null;
        }
    }
}