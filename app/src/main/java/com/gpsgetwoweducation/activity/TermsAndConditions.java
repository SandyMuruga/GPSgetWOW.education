package com.gpsgetwoweducation.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;

import java.util.Objects;

public class TermsAndConditions extends AppCompatActivity implements View.OnClickListener {
    private AppCompatImageView terms_and_conditions, iv_customer_logo,iv_back;
    private SharedPreferenceClass sharedPreferenceClass;
    private String businessName, country_code, customer_id, sub_domain_name;

    private AppCompatTextView tv_title_name1, tv_title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        iv_back = findViewById(R.id.iv_back);
        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        tv_title_name = (AppCompatTextView) findViewById(R.id.tv_title_name);
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

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == terms_and_conditions) {
            onBackPressed();
            finish();
        }
    }
}