package com.gpsgetwoweducation.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPreferenceClass {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public SharedPreferenceClass(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void set(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String get(String key) {
        String value = "";
        value = sharedPreferences.getString(key, value);
        if (value == null) {
            value = "";
        }
        return value;
    }


    public void setLoginTime(String loginTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LOGIN_TIME, loginTime);
        editor.apply();
    }

    public String getLoginTime() {
        return sharedPreferences.getString(KEY_LOGIN_TIME, "");
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearLoginData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_LOGIN_TIME);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.apply();
    }
}
