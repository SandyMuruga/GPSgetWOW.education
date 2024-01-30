package com.gpsgetwoweducation.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.activity.StopTracking;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentSocketTrackingDataResponse;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusData;
import com.gpsgetwoweducation.pojo.socketcurrenttrackingdata.SendLiveCoordinatesData;
import com.gpsgetwoweducation.roomdb.dao.GpsResponseDAO;
import com.gpsgetwoweducation.roomdb.entity.GpsResponseData;
import com.gpsgetwoweducation.sqlite.data.DBHelper;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.Utilss;
import com.gpsgetwoweducation.roomdb.dao.GpsTrackingDAO;
import com.gpsgetwoweducation.roomdb.database.GpsTrackingDB;
import com.gpsgetwoweducation.roomdb.entity.GpsTrackingData;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;


public class LocationUpdatesService extends Service {
    private static final String TAG = LocationUpdatesService.class.getSimpleName();
    private static final String CHANNEL_ID = "channel_01";
    public static String ACTION_BROADCAST = "com.google.android.gms.location.sample.locationupdatesforegroundservice.broadcast";
    public static String EXTRA_LOCATION = "com.google.android.gms.location.sample.locationupdatesforegroundservice.location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.google.android.gms.location.sample.locationupdatesforegroundservice.started_from_notification";
    private final IBinder mBinder = new LocalBinder();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;
    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private Socket socket;
    private String server_url = "https://u27api.getwow.education";
    private DBHelper dbHelper;
    private Context appContext;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int NOTIFICATION_ID = 12345678;
    private Timer mTimer = null;
    private SharedPreferenceClass sharedPreferenceClass;
    private LinkedList<GpsTrackingData> dataQueue = new LinkedList<>();
    private boolean isInternetConnected = false;

    @Override
    public void onCreate() {
        Log.d("LocationUpdatesService", "onCreate");
        checkAndEnableGPS();
        appContext = getApplicationContext();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        startService(new Intent(this, OverlayService.class));

        createLocationRequest();
        getLastLocation();
        requestLocationUpdates();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        dbHelper = new DBHelper();
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        } else {
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        /*if (Utilss.requestingLocationUpdates(this)) {
            startForeground(NOTIFICATION_ID, getNotification());
        }*/

        if (!Utilss.requestingLocationUpdates(this)) {
            stopSelf();
            stopForeground(true);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
        Log.d(TAG, "Service onDestroy called");
        super.onDestroy();
    }

    public void requestLocationUpdates() {
        Utilss.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utilss.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);

        }
    }
    public void removeLocationUpdates() {
        try {
            if (mFusedLocationClient != null && mLocationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Utilss.setRequestingLocationUpdates(this, false);
                Log.d(TAG, "Location updates removed successfully");
                stopSelf();
                stopForeground(true);
                Log.d(TAG, "Service stopped successfully");
            } else {
                Log.e(TAG, "mFusedLocationClient or mLocationCallback is null");
            }
        } catch (SecurityException unlikely) {
            Utilss.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
            unlikely.printStackTrace();
        }
    }


  /*  public void removeLocationUpdates() {
        try {
            if (mFusedLocationClient != null && mLocationCallback != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Utilss.setRequestingLocationUpdates(this, false);
                stopSelf();
            } else {
                Log.e(TAG, "mFusedLocationClient or mLocationCallback is null");
            }
        } catch (SecurityException unlikely) {
            Utilss.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }*/

    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .addAction(R.drawable.app_icon, getString(R.string.launch_activity), pendingIntent)
                .addAction(R.drawable.app_icon, getString(R.string.remove_location_updates), pendingIntent)
                .setContentText(Utilss.getLocationText(mLocation))
                .setContentTitle(Utilss.getLocationTitle(this))
                .setOngoing(true)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(Utilss.getLocationText(mLocation))
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSound(null)
                .setOnlyAlertOnce(true);


        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        if (location != null) {
            Log.i(TAG, "New location: " + location);
            mLocation = location;
            Intent intent = new Intent(ACTION_BROADCAST);
            intent.putExtra(EXTRA_LOCATION, location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            if (socket == null || !socket.connected()) {
                establishSocketConnection(location);
            } else {
                emitEvent(location);
            }
            if (serviceIsRunningInForeground(this)) {
                mNotificationManager.notify(NOTIFICATION_ID, getNotification());
            }
        } else {
            Log.e(TAG, "Null location received");
        }
    }

    private void establishSocketConnection(Location location) {
        IO.Options opts = new IO.Options();
        opts.transports = new String[]{WebSocket.NAME};
        opts.forceNew = true;
        opts.reconnection = true;
        try {  // connect socket
            socket = IO.socket(server_url, opts);
            socket.on(io.socket.client.Socket.EVENT_CONNECT, args -> {
                Log.d("Socket", "Connected");
                new Handler(Looper.getMainLooper()).post(() -> {
                   // Toast.makeText(LocationUpdatesService.this, "Socket connected", Toast.LENGTH_SHORT).show();
                });
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Log.e("Socket", "Connection error: " + args[0].toString());
            });
            socket.connect();
        } catch (URISyntaxException e) {
            Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void emitEvent(Location location) {
        CurrentTrackingStatusData trackingStatusData = new CurrentTrackingStatusData();
        sharedPreferenceClass = new SharedPreferenceClass(getApplicationContext());
        SendLiveCoordinatesData sendLiveCoordinatesData = new SendLiveCoordinatesData();
        String currentDateTime = getCurrentDateTime();
        sendLiveCoordinatesData.setCountry_code(sharedPreferenceClass.get("token_country_code"));
        sendLiveCoordinatesData.setCustomer_id(sharedPreferenceClass.get("token_customer_id"));
        sendLiveCoordinatesData.setLatest_datetime(currentDateTime);
        sendLiveCoordinatesData.setLatitude(location.getLatitude());
        sendLiveCoordinatesData.setLongitude(location.getLongitude());

        if (trackingStatusData.getIs_pickup_trip() == 1) {
            // Pickup trip
            double lastLatitude = Double.parseDouble(sharedPreferenceClass.get("last_latitude"));
            double lastLongitude = Double.parseDouble(sharedPreferenceClass.get("last_longitude"));
            sendLiveCoordinatesData.setLocation_latitude(lastLatitude);
            sendLiveCoordinatesData.setLocation_longitude(lastLongitude);
        } else if (trackingStatusData.getIs_pickup_trip() == 0) {
            // Drop-off trip
            /*double lastLatitudeDropoff = Double.parseDouble(sharedPreferenceClass.get("last_latitude_dropoff"));
            double lastLongitudeDropoff = Double.parseDouble(sharedPreferenceClass.get("last_longitude_dropoff"));*/

            double lastLatitudeDropoff = 0.0; // default value if string is empty
            double lastLongitudeDropoff = 0.0; // default value if string is empty
            String lastLatitudeDropoffStr = sharedPreferenceClass.get("last_latitude_dropoff");
            String lastLongitudeDropoffStr = sharedPreferenceClass.get("last_longitude_dropoff");
            if (!lastLatitudeDropoffStr.isEmpty()) {
                lastLatitudeDropoff = Double.parseDouble(lastLatitudeDropoffStr);
            }
            if (!lastLongitudeDropoffStr.isEmpty()) {
                lastLongitudeDropoff = Double.parseDouble(lastLongitudeDropoffStr);
            }
            sendLiveCoordinatesData.setLocation_latitude(lastLatitudeDropoff);
            sendLiveCoordinatesData.setLocation_longitude(lastLongitudeDropoff);
        }
        //sendLiveCoordinatesData.setLocation_latitude(12.735126);
        //sendLiveCoordinatesData.setLocation_longitude(77.829338);
        sendLiveCoordinatesData.setLogin_user_id(sharedPreferenceClass.get("token_user_id"));
        sendLiveCoordinatesData.setGps_tracking_event_id(sharedPreferenceClass.get("gps_tracking_event_id"));
        // Send postData to the server using sockets
        JSONObject postData = createJsonObject(sendLiveCoordinatesData);
        if (socket.connected()) {
            Object[] args = new Object[]{postData};
            socket.emit("setLiveCoordinates", args, args1 -> {
                Log.e("Socket", "Data Sent successfully");
                if (args1.length > 0) {
                    try {
                        String jsonString = args1[0].toString();
                        Log.d("Socket", "Raw JSON Response: " + jsonString);
                        JSONObject responseJson = new JSONObject(jsonString);
                        int statusCode = responseJson.getInt("statusCode");
                        String message = responseJson.getString("message");
                        if (statusCode == 200) {
                            JSONObject insertedData = responseJson.getJSONObject("insertedData");
                            // Check if "tracking_gps_data" is not null
                            if (!insertedData.isNull("tracking_gps_data")) {
                                JSONObject trackingGpsData = insertedData.getJSONObject("tracking_gps_data");

                                String gpsTrackingEventId = insertedData.getString("gps_tracking_event_id");
                                boolean location_reached = insertedData.getBoolean("location_reached");

                               /* sharedPreferenceClass.set("gps_tracking_event_id", gpsTrackingEventId);
                                sharedPreferenceClass.set("location_reached", String.valueOf(location_reached));*/

                                if (location_reached) {
                                    StopTracking stopTracking = new StopTracking();
                                    stopTracking.stopTracking();
                                }

                                List<CurrentSocketTrackingDataResponse> trackingDataList = new ArrayList<>();

                                double x = trackingGpsData.getDouble("x");
                                double y = trackingGpsData.getDouble("y");
                                String dateTime = trackingGpsData.getString("gps_mobile_app_test_datetime");

                                InsertResponseAsyncTask insertResponseAsyncTask = new InsertResponseAsyncTask(getApplicationContext());
                                insertResponseAsyncTask.execute(x, y, dateTime);
                            } else {
                                Log.e("Socket", "tracking_gps_data is null in the JSON response");
                            }
                        } else {
                            Log.e("Socket", "Server responded with an error: " + message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Log.e("Socket", "Socket is not connected");
        }
    }
    private JSONObject createJsonObject(SendLiveCoordinatesData data) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("country_code", data.getCountry_code());
            postData.put("customer_id", data.getCustomer_id());
            postData.put("latest_datetime", data.getLatest_datetime());
            postData.put("latitude", data.getLatitude());
            postData.put("longitude", data.getLongitude());
            postData.put("location_latitude", data.getLocation_latitude());
            postData.put("location_longitude", data.getLocation_longitude());
            postData.put("login_user_id", data.getLogin_user_id());
            postData.put("gps_tracking_event_id", data.getGps_tracking_event_id());
        } catch (JSONException e) {
            Log.e("SendDataToServer", "Error creating JSON data", e);
        }
        return postData;
    }

    // AsyncTask to insert RESPONSE Data into local Room database
    private static class InsertResponseAsyncTask extends AsyncTask<Object, Void, Void> {
        private GpsResponseDAO gpsResponseDAO;
        private Context context;

        public InsertResponseAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Object... params) {
            double x = (double) params[0];
            double y = (double) params[1];
            String dateTime = (String) params[2];

            GpsTrackingDB db = GpsTrackingDB.getInstance(context);
            gpsResponseDAO = db.getGpsResponseDAO();

            // Create GpsResponseData object and insert into the database
            GpsResponseData gpsResponseData = new GpsResponseData(x, y, dateTime);
            gpsResponseDAO.insertGpsResponseData(gpsResponseData);

            return null;
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                // If connected, send data to the server
                //sendStoredDataToServer();
            }
        }
    };

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                return service.foreground;
            }
        }
        return false;
    }

    private void checkAndEnableGPS() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            enableGpsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableGpsIntent);
        }
    }
}
