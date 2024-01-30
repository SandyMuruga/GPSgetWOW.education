package com.gpsgetwoweducation;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    //app whitelist
    private static App instance;

    public static void init(Application application) {
        if (instance == null) {
            instance = (App) application;
        }
    }

    public static App get() {
        return instance;
    }

    public App() {
        instance = this;
    }

}
