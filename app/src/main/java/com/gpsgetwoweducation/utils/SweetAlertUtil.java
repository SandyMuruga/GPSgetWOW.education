package com.gpsgetwoweducation.utils;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.gpsgetwoweducation.activity.FaceLogin;
import com.gpsgetwoweducation.services.OverlayService;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetAlertUtil {
    Context context;
    SharedPreferenceClass sharedPreferenceClass;
    private static List<String> storedPackageNames;

    public static void processPackageNames(List<String> packageNames) {
        storedPackageNames = packageNames;
        for (String packageName : packageNames) {
            // Do whatever you need with packageName in SweetAlertUtil
            Log.d("SweetAlertUtil", "Package Name: " + packageName);
        }
    }
    public static void showLogoutConfirmation(Context context, Class<?> targetActivity) {
        OverlayService overlayService = OverlayService.getInstance();
        if (overlayService != null) {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Logout")
                    .setContentText("Are you sure you want to log out?")
                    .setConfirmText("Yes")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            overlayService.removeOverlay();
                            SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
                            sharedPreferenceClass.set("token_user_category_type", "");
                            sharedPreferenceClass.set("token_user_registered_categories_ids", "");
                            sharedPreferenceClass.set("isLogin", "");
                            sharedPreferenceClass.set("isLogout", "yes");
                            //insertEndtDateTime(context);
                            Intent intent = new Intent(context, FaceLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    })
                    .setCancelText("No")
                    .show();
        }
    }

   /* private static void insertEndtDateTime(Context context) {
        AppUsageViewModel appUsageViewModel = ViewModelProviders.of((FragmentActivity) context).get(AppUsageViewModel.class);
        String endDateTime = getFormattedDateTime(System.currentTimeMillis());
        SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(context);
        String startDateTime = sharedPreferenceClass.get("start_date_time");

        long startTimeMillis = convertDateTimeToMillis(startDateTime);
        long endTimeMillis = System.currentTimeMillis();
        long timeDifferenceMillis = endTimeMillis - startTimeMillis;
        long timeDifferenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis);
        // Get the list of used app names
        List<String> usedApps = getUsedApps(context, startTimeMillis, endTimeMillis, storedPackageNames);
        //List<String> usedApps = getUsedInstalledApps(context, startTimeMillis, endTimeMillis);
        String appsUsed = TextUtils.join(", ", usedApps);

        *//*if (storedPackageNames != null) {
            for (String packageName : storedPackageNames) {
                Log.d("SweetAlertUtil", "Example Method - Package Name: " + packageName);
            }
        } else {
            Log.d("SweetAlertUtil", "No stored package names");
        }*//*

        EndUsageData endUsageData = new EndUsageData();
        endUsageData.setCountry_code(sharedPreferenceClass.get("registered_country_code"));
        endUsageData.setCustomer_id(sharedPreferenceClass.get("registered_customer_id"));
        endUsageData.setSession_end_datetime(endDateTime);
        endUsageData.setApps_used(appsUsed);
        endUsageData.setId(sharedPreferenceClass.get("id"));
        appUsageViewModel.sendAppEndUsageData(endUsageData).observe((FragmentActivity) context, endUsageDataResponse -> {
            EndUsageData endUsageDataList = new EndUsageData();
            if (endUsageDataResponse.isStatus()) {
                endUsageDataList = endUsageDataResponse.getData();
            } else {
                Toast.makeText(context, "Data Not Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
