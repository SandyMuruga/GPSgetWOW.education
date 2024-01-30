package com.gpsgetwoweducation.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.adapter.PastTrackingDataAdapter;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.viewmodel.PastTrackingDataViewModel;

public class PastTrackingData extends AppCompatActivity implements View.OnClickListener {
    private AppCompatImageView past_tracking_back,iv_customer_logo;
    private RecyclerView rv_past_tracking_data;
    private String businessName, country_code, customer_id, sub_domain_name;
    private SharedPreferenceClass sharedPreferenceClass;
    private PastTrackingDataViewModel pastTrackingDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_tracking_data);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        past_tracking_back = (AppCompatImageView) findViewById(R.id.iv_back);
        past_tracking_back.setOnClickListener(this);

        rv_past_tracking_data = (RecyclerView) findViewById(R.id.rv_past_tracking_data);

        iv_customer_logo = findViewById(R.id.iv_customer_logo);
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

        getPastTrackingData();
    }

    @Override
    public void onClick(View v) {
        if (v == past_tracking_back) {
            onBackPressed();
            finish();
        }
    }

    private void getPastTrackingData() {
        pastTrackingDataViewModel = new ViewModelProvider(this).get(PastTrackingDataViewModel.class);
        pastTrackingDataViewModel.pastTrackingDataLiveData(sharedPreferenceClass.get("token_country_code"), sharedPreferenceClass.get("token_customer_id"), sharedPreferenceClass.get("token_user_id")).observe(this, pastTrackingDataResponse -> {
            if (pastTrackingDataResponse != null && pastTrackingDataResponse.getData() != null) {
                if (!pastTrackingDataResponse.getData().isEmpty()) {
                    // Data is available, set up the adapter
                    PastTrackingDataAdapter pastTrackingDataAdapter = new PastTrackingDataAdapter(this, pastTrackingDataResponse.getData());
                    rv_past_tracking_data.setAdapter(pastTrackingDataAdapter);
                    rv_past_tracking_data.setLayoutManager(new LinearLayoutManager(this));
                    findViewById(R.id.tv_no_past_data).setVisibility(View.GONE);
                    rv_past_tracking_data.setVisibility(View.VISIBLE);
                } else {
                    // No data available, show the TextView
                    findViewById(R.id.tv_no_past_data).setVisibility(View.VISIBLE);
                    rv_past_tracking_data.setVisibility(View.GONE);
                }
            } else {
                // Response or data is null, show the TextView
                findViewById(R.id.tv_no_past_data).setVisibility(View.VISIBLE);
                rv_past_tracking_data.setVisibility(View.GONE);
            }
        });
    }

}