package com.gpsgetwoweducation.utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionsClass {
    private Context context;

    public PermissionsClass(Context context ) {
        this.context = context;
    }

    //Collection of Permissions
    public String[] permissionsArray() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
        };
        return PERMISSIONS;
    }

    //Check whether the Application had above Permission or Not
    public boolean[] checkPermissions() {
        String[] PERMISSIONS = permissionsArray();
        boolean[] CHECK_PERMISSIONS = new boolean[PERMISSIONS.length];
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(context,
                    PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED) {
                CHECK_PERMISSIONS[i] = true;
            } else {
                CHECK_PERMISSIONS[i] = false;
            }
        }

        return CHECK_PERMISSIONS;
    }

    //Check whether all Permissions are Granted or Not
    public boolean checkAppPermission() {
        boolean[] CHECK_PERMISSIONS = checkPermissions();
        for (int i = 0; i < CHECK_PERMISSIONS.length; i++) {
            if (CHECK_PERMISSIONS[i] == false) {
                return false;
            }
        }
        return true;
    }


}
