package com.gpsgetwoweducation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.adapter.CurrentTrackingStatusAdapter;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusData;
import com.gpsgetwoweducation.services.LocationUpdatesService;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.viewmodel.CurrentTrackingStatusViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

public class CurrentTrackingStatus extends AppCompatActivity implements View.OnClickListener {
    private AppCompatImageView iv_back, iv_customer_logo,iv_user_logo;
    private AppCompatButton bt_map_view;
    private RecyclerView rv_current_tracking_status;
    private CurrentTrackingStatusViewModel currentTrackingStatusViewModel;
    private ProgressDialogHUD mProgressDialog;
    LocationUpdatesService service;
    private SharedPreferenceClass sharedPreferenceClass;
    private String businessName, country_code, customer_id, sub_domain_name;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_tracking_status);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        mProgressDialog = new ProgressDialogHUD(this);
        iv_back = (AppCompatImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        service = new LocationUpdatesService();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_current_tracking_status = (RecyclerView) findViewById(R.id.rv_current_tracking_status);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        iv_user_logo = findViewById(R.id.iv_user_logo);
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
        if (country_code != null) {
            country_code = country_code.toLowerCase();
        }
        String imageUrl = "https://cephapi.getster.tech/api/storage/" + country_code + "-" + customer_id + "/" + sub_domain_name + "-" + "icon-128x128.png";
        CustomerLogoUtil.loadImage(this, imageUrl, iv_customer_logo);

        String userImage = "https://cephapi.getster.tech/api/storage/" + country_code + "-" + customer_id + "/" + sharedPreferenceClass.get("chooseUserID") + ".png";
        CustomerLogoUtil.loadImage(this, userImage, iv_user_logo);

        getCurrentTrackingStatus();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back) {
            onBackPressed();
            finish();
        }
    }
    private void getCurrentTrackingStatus() {
        showProgress("Loading...");
        currentTrackingStatusViewModel = new ViewModelProvider(this).get(CurrentTrackingStatusViewModel.class);
        dismissProgress();
        currentTrackingStatusViewModel.currentTrackingStatusLiveData(sharedPreferenceClass.get("token_country_code"), sharedPreferenceClass.get("token_customer_id"), sharedPreferenceClass.get("token_user_id")).observe(this, currentTrackingStatusResponse -> {
        //currentTrackingStatusViewModel.currentTrackingStatusLiveData("in","102","1").observe(this, currentTrackingStatusResponse -> {
            if (currentTrackingStatusResponse != null && currentTrackingStatusResponse.getData() != null) {
                if (!currentTrackingStatusResponse.getData().isEmpty()) {
                    CurrentTrackingStatusAdapter currentTrackingStatusAdapter = new CurrentTrackingStatusAdapter(this, currentTrackingStatusResponse.getData(), service, swipeRefreshLayout);
                    rv_current_tracking_status.setAdapter(currentTrackingStatusAdapter);
                    rv_current_tracking_status.setLayoutManager(new LinearLayoutManager(this));
                    findViewById(R.id.tv_no_data).setVisibility(View.GONE);
                    rv_current_tracking_status.setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.tv_no_data).setVisibility(View.VISIBLE);
                    rv_current_tracking_status.setVisibility(View.GONE);
                }
            } else {
                findViewById(R.id.tv_no_data).setVisibility(View.VISIBLE);
                rv_current_tracking_status.setVisibility(View.GONE);
            }
        });
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
}