package com.gpsgetwoweducation.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.activity.HomePage;

public class OverlayService extends Service {
    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;
    private ImageView appIconImageView;
    private boolean isOverlayActive = true;

    private long serviceStartTime;
    private long serviceStopTime;

    public long getServiceStartTime() {
        return serviceStartTime;
    }

    public long getServiceEndTime() {
        return serviceStopTime;
    }

    public OverlayService() {
    }

    public static OverlayService instance;

    public static OverlayService getInstance() {
        return instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAppIcon(isConnected());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        OverlayService.instance = this;
        // Check if the SYSTEM_ALERT_WINDOW permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            // If not granted, request the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createOverlay();
        serviceStartTime = System.currentTimeMillis();
    }

    private void createOverlay() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
        appIconImageView = overlayView.findViewById(R.id.appIconImageView);
        appIconImageView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            private boolean isClick;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isClick = true; // Assume it's a click until proven otherwise
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) (event.getRawX() - initialTouchX);
                        int deltaY = (int) (event.getRawY() - initialTouchY);
                        params.x = initialX + deltaX;
                        params.y = initialY + deltaY;
                        windowManager.updateViewLayout(overlayView, params);
                        // If movement is detected, it's not a click
                        isClick = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            // It's a click, handle the click event
                            Intent intent = new Intent(OverlayService.this, HomePage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        return true;
                }
                return false;
            }
        });

        // Configure WindowManager.LayoutParams
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER | Gravity.LEFT;
        params.x = 0; // Initial X position
        params.y = 200; //

        windowManager.addView(overlayView, params);

        // Update the app icon based on the internet connection status
        updateAppIcon(isConnected());
    }

    private void updateAppIcon(boolean isConnected) {
        // Set the icon dynamically based on the internet connection status
        appIconImageView.setImageResource(isConnected ? R.drawable.map_on_icon : R.drawable.map_off_icon);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void removeOverlay() {
        if (overlayView != null && windowManager != null && overlayView.getWindowToken() != null) {
            // Remove the overlay from the WindowManager
            windowManager.removeView(overlayView);
            isOverlayActive = false;
            // Stop the service
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
        removeOverlay();
        serviceStopTime = System.currentTimeMillis();
    }
}
