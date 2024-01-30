package com.gpsgetwoweducation.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class AppAdminListUtility {

    /*public static void showAppAdminList(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.dialog_app_administrator_list, null);
        RecyclerView rv_app_administrator_list = customView.findViewById(R.id.rv_app_administrator_list);
        List<AppAdministratorListData> appAdministratorListData = new ArrayList<>();

        AppAdministratorAdapter appAdminAdapter = new AppAdministratorAdapter(context, appAdministratorListData);
        rv_app_administrator_list.setAdapter(appAdminAdapter);
        rv_app_administrator_list.setLayoutManager(new LinearLayoutManager(context));
        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
        AppAdministratorViewModel appAdministratorViewModel = ViewModelProviders.of((FragmentActivity) context).get(AppAdministratorViewModel.class);
        appAdministratorViewModel.getAppAdministratorList(sharedPreferenceClass.get("sub_domain_name")).observe((LifecycleOwner) context, appAdministratorListResponse -> {
            if (appAdministratorListResponse != null) {
                if (appAdministratorListResponse.isStatus()) {
                    if (appAdministratorListResponse.getData() != null) {
                        if (appAdministratorListResponse.getData().size() > 0) {
                            appAdministratorListData.addAll(appAdministratorListResponse.getData());
                            appAdminAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Toast.makeText(context, appAdministratorListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/


}
