package com.gpsgetwoweducation.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusData;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.DropOffRouteStopsItem;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.GpsCoordinates;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.PickUpRouteStopsItem;
import com.gpsgetwoweducation.roomdb.dao.GpsResponseDAO;
import com.gpsgetwoweducation.roomdb.database.GpsTrackingDB;
import com.gpsgetwoweducation.roomdb.entity.GpsResponseData;
import com.gpsgetwoweducation.services.LocationUpdatesService;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.DateUtils;
import com.gpsgetwoweducation.utils.Utilss;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapViewCurrentTrackingStatus extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MapView mapViewCurrentTrackingStatus;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final long MIN_TIME = 10000;
    private static final float MIN_DISTANCE = 10f;
    private boolean shouldCenterMap = true;
    private AppCompatImageView iv_back, iv_customer_logo;
    private ProgressDialogHUD mProgressDialog;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private Marker userLocationMarker;
    private SharedPreferenceClass sharedPreferenceClass;
    private String businessName, country_code, customer_id, sub_domain_name;

    private AppCompatTextView tv_tracking_initiated_by, tv_tracking_start_date_time;

    // service related
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((LocationUpdatesService.LocalBinder) service).getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    private final Handler handler = new Handler();
    private static final long UPDATE_INTERVAL = 5000;
    private final Runnable updateMapRunnable = new Runnable() {
        @Override
        public void run() {
            // Execute the AsyncTask to fetch the latest coordinates from the database
            new FetchLatestCoordinatesTask().execute();
        }
    };

    private boolean is_disabled_stop_tracking;

    private class FetchLatestCoordinatesTask extends AsyncTask<Void, Void, GpsResponseData> {
        @Override
        protected GpsResponseData doInBackground(Void... voids) {
            GpsTrackingDB appDatabase = GpsTrackingDB.getInstance(MapViewCurrentTrackingStatus.this);
            GpsResponseDAO gpsResponseDAO = appDatabase.getGpsResponseDAO();
            return gpsResponseDAO.getLatestCoordinate();
        }

        @Override
        protected void onPostExecute(GpsResponseData latestCoordinate) {
            displayGpsLiveResponseData(Collections.singletonList(latestCoordinate));
            handler.postDelayed(updateMapRunnable, UPDATE_INTERVAL);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_map_current_tracking_status);

        //startService(new Intent(this, LocationUpdatesService.class));
        sharedPreferenceClass = new SharedPreferenceClass(this);
        mapViewCurrentTrackingStatus = findViewById(R.id.mapViewCurrentTrackingStatus);
        mProgressDialog = new ProgressDialogHUD(this);

        tv_tracking_initiated_by = findViewById(R.id.tv_tracking_initiated_by);
        tv_tracking_start_date_time = findViewById(R.id.tv_tracking_start_date_time);

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

        String tracking_event_initiated_by_app_name = sharedPreferenceClass.get("tracking_event_initiated_by_app_name");
        String trip_scheduled_start_datetime = sharedPreferenceClass.get("trip_scheduled_start_datetime");

        tv_tracking_initiated_by.setText(tracking_event_initiated_by_app_name);
        tv_tracking_start_date_time.setText(DateUtils.formatDateTime(trip_scheduled_start_datetime));

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            finish();
        });

        startPeriodicUpdates();
        new LoadMapTask().execute();

        if (Utilss.requestingLocationUpdates(this) && !checkPermissions()) {
            requestPermissions();
        }

        stopTracking();
    }

    private void startPeriodicUpdates() {
        handler.postDelayed(updateMapRunnable, UPDATE_INTERVAL);
    }

    private void stopPeriodicUpdates() {
        handler.removeCallbacks(updateMapRunnable);
    }

    private Marker createMarker(double x, double y, String dateTime, int iconResource) {
        GeoPoint geoPoint = new GeoPoint(x, y);

        Drawable drawable = ContextCompat.getDrawable(MapViewCurrentTrackingStatus.this, iconResource);
        if (drawable == null) {
            Log.e("MapView", "Drawable is null");
            return null;
        }

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Marker marker = new Marker(mapViewCurrentTrackingStatus);
        marker.setPosition(geoPoint);
        marker.setIcon(drawable);
        marker.setSnippet("Date/Time: " + dateTime);
        return marker;
    }

    private void configureMapView() {
        if (mapViewCurrentTrackingStatus != null) {
            mapViewCurrentTrackingStatus.getTileProvider().clearTileCache();
            Configuration.getInstance().setCacheMapTileCount((short) 12);
            Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
            mapViewCurrentTrackingStatus.setMultiTouchControls(true);
            mapViewCurrentTrackingStatus.setTileSource(TileSourceFactory.MAPNIK);
            // Add this line to set a sample location and zoom level
            mapViewCurrentTrackingStatus.getController().setCenter(new GeoPoint(0, 0));
            mapViewCurrentTrackingStatus.getController().setZoom(2.0);
        } else {
            Toast.makeText(this, "Map is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapViewCurrentTrackingStatus != null) {
            mapViewCurrentTrackingStatus.onResume();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapViewCurrentTrackingStatus != null) {
            mapViewCurrentTrackingStatus.onPause();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    private void initializeLocationManager() {
        showProgress("Loading Map...");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mapViewCurrentTrackingStatus == null) {
                    Log.e("MapViewCurrentTracking", "MapView is null");
                    return;
                }
                //  mapViewCurrentTrackingStatus.getOverlays().clear();
                if (location != null) {
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();
                    Marker stopMarker = new Marker(mapViewCurrentTrackingStatus);
                    stopMarker.setPosition(new GeoPoint(userLatitude, userLongitude));
                    stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    createOrUpdateMarker(userLatitude, userLongitude, "Your Location", R.drawable.bus_location_icon);
                    centerMapOnLocation(userLatitude, userLongitude);
                }
                List<GeoPoint> stopCoordinates = new ArrayList<>();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    List<PickUpRouteStopsItem> pickUpRouteStops = (List<PickUpRouteStopsItem>) bundle.getSerializable("pickUpRouteStops");
                    List<DropOffRouteStopsItem> dropOffRouteStops = (List<DropOffRouteStopsItem>) bundle.getSerializable("dropOffRouteStops");

                    if (pickUpRouteStops != null && !pickUpRouteStops.isEmpty()) {
                        processPickUpRouteStops(pickUpRouteStops, R.drawable.ic_location, stopCoordinates);
                    }

                    if (dropOffRouteStops != null && !dropOffRouteStops.isEmpty()) {
                        processDropOffRouteStops(dropOffRouteStops, R.drawable.ic_location, stopCoordinates);
                    }
                } else {
                    Log.d("MapViewCurrentTracking", "bundle is null");
                }

                if (!stopCoordinates.isEmpty()) {
                    Polyline routePolyline = new Polyline();
                    routePolyline.setPoints(stopCoordinates);
                    routePolyline.setColor(Color.BLUE);
                    routePolyline.setWidth(5f);
                    mapViewCurrentTrackingStatus.getOverlays().add(routePolyline);
                }
                //mapViewCurrentTrackingStatus.invalidate();
                // dismissProgress();
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void processPickUpRouteStops(List<? extends PickUpRouteStopsItem> routeStops, int markerDrawable, List<GeoPoint> stopCoordinates) {
        double minLatitude = Double.MAX_VALUE;
        double maxLatitude = -Double.MAX_VALUE;
        double minLongitude = Double.MAX_VALUE;
        double maxLongitude = -Double.MAX_VALUE;

        for (PickUpRouteStopsItem routeStop : routeStops) {
            GpsCoordinates gpsCoordinates = routeStop.getGps_coordinates();
            String stopName = routeStop.getStop_name();
            Log.d("MapViewCurrentTracking", "Pickup Stop Name: " + stopName);

            if (gpsCoordinates != null) {
                double latitude = gpsCoordinates.getX();
                double longitude = gpsCoordinates.getY();
                Log.d("MapViewCurrentTracking", "Pickup GpsCoordinates: " + latitude + ", " + longitude);

                Marker stopMarker = new Marker(mapViewCurrentTrackingStatus);
                stopMarker.setPosition(new GeoPoint(latitude, longitude));
                stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                stopMarker.setIcon(getResources().getDrawable(markerDrawable));
                mapViewCurrentTrackingStatus.getOverlays().add(stopMarker);

                InfoWindow infoWindow = new MyInfoWindow(stopMarker, mapViewCurrentTrackingStatus, stopName);
                stopMarker.setInfoWindow(infoWindow);

                minLatitude = Math.min(minLatitude, latitude);
                maxLatitude = Math.max(maxLatitude, latitude);
                minLongitude = Math.min(minLongitude, longitude);
                maxLongitude = Math.max(maxLongitude, longitude);

                stopMarker.showInfoWindow();
            } else {
                Log.d("MapViewCurrentTracking", "Pickup GpsCoordinates is null for Stop Name: " + stopName);
            }
        }
        BoundingBox boundingBox = new BoundingBox(maxLatitude, maxLongitude, minLatitude, minLongitude);
        mapViewCurrentTrackingStatus.zoomToBoundingBox(boundingBox, true, 8); // Change 10 to your desired zoom level
    }

    private void processDropOffRouteStops(List<? extends DropOffRouteStopsItem> routeStops, int markerDrawable, List<GeoPoint> stopCoordinates) {
        double minLatitude = Double.MAX_VALUE;
        double maxLatitude = -Double.MAX_VALUE;
        double minLongitude = Double.MAX_VALUE;
        double maxLongitude = -Double.MAX_VALUE;

        for (DropOffRouteStopsItem routeStop : routeStops) {
            GpsCoordinates gpsCoordinates = routeStop.getGps_coordinates();
            String stopName1 = routeStop.getStopName();
            if (gpsCoordinates != null) {
                double latitude = gpsCoordinates.getX();
                double longitude = gpsCoordinates.getY();

                Log.d("MapViewCurrentTracking", "Dropoff Stop Name: " + stopName1 + ", Latitude: " + latitude + ", Longitude: " + longitude);

                Marker stopMarker = new Marker(mapViewCurrentTrackingStatus);
                stopMarker.setPosition(new GeoPoint(latitude, longitude));
                stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                stopMarker.setIcon(getResources().getDrawable(markerDrawable));
                mapViewCurrentTrackingStatus.getOverlays().add(stopMarker);

                InfoWindow infoWindow1 = new MyInfoWindow(stopMarker, mapViewCurrentTrackingStatus, stopName1);
                stopMarker.setInfoWindow(infoWindow1);

                stopMarker.showInfoWindow();

                minLatitude = Math.min(minLatitude, latitude);
                maxLatitude = Math.max(maxLatitude, latitude);
                minLongitude = Math.min(minLongitude, longitude);
                maxLongitude = Math.max(maxLongitude, longitude);
            } else {
                Log.d("MapViewCurrentTracking", "Dropoff GpsCoordinates is null for Stop Name: " + stopName1);
            }
        }
        BoundingBox boundingBox = new BoundingBox(maxLatitude, maxLongitude, minLatitude, minLongitude);
        mapViewCurrentTrackingStatus.zoomToBoundingBox(boundingBox, true, 8); // Change 10 to your desired zoom level
    }

    private Marker createOrUpdateMarker(double latitude, double longitude, String title, int iconResource) {
        Marker myMarker;
        List<Overlay> mapOverlays = mapViewCurrentTrackingStatus.getOverlays();
        if (!mapOverlays.isEmpty() && mapOverlays.get(0) instanceof Marker) {
            myMarker = (Marker) mapOverlays.get(0);
        } else {
            myMarker = new Marker(mapViewCurrentTrackingStatus);
            mapOverlays.add(myMarker);
        }
        myMarker.setPosition(new GeoPoint(latitude, longitude));
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMarker.setTitle(title);
        myMarker.setPanToView(true);
        myMarker.setIcon(getResources().getDrawable(iconResource));
        //mapViewCurrentTrackingStatus.invalidate();
        return myMarker;
    }

    private void centerMapOnLocation(double latitude, double longitude) {
        IMapController mapController = mapViewCurrentTrackingStatus.getController();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        mapController.setZoom(15.0);
    }

    private class MyInfoWindow extends InfoWindow {
        private String stopName;
        private GpsCoordinates gpsCoordinates;

        public MyInfoWindow(Marker marker, MapView mapView, String stopName) {
            super(R.layout.map_stop_name, mapView);
            this.stopName = stopName;
            this.gpsCoordinates = gpsCoordinates;
            onClose();
            onOpen(marker);
        }

        @Override
        public void onClose() {
        }

        @Override
        public void onOpen(Object arg0) {
            TextView stopNameTextView = mView.findViewById(R.id.stopNameTextView);
            stopNameTextView.setText(stopName);

            if (gpsCoordinates != null) {
                double latitude = gpsCoordinates.getY();
                double longitude = gpsCoordinates.getX();
                String coordinates = "Latitude: " + latitude + ", Longitude: " + longitude;
            } else {

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        stopPeriodicUpdates();
    }

    // service related methods
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            if (mBound) {
                mService.requestLocationUpdates();
            } else {
                bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MapViewCurrentTrackingStatus.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mService.requestLocationUpdates();
            } else {
                Toast.makeText(MapViewCurrentTrackingStatus.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            // txt_status.setText(Utilss.getLocationText(location));
            if (location != null) {
                /*Toast.makeText(MapViewCurrentTrackingStatus.this, Utilss.getLocationText(location),
                        Toast.LENGTH_SHORT).show();*/
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        /*if (s.equals(Utilss.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utilss.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }*/
    }

    //progress dialog
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

    private class LoadMapTask extends AsyncTask<Void, Void, List<GpsResponseData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress("Loading Map...");
        }

        @Override
        protected List<GpsResponseData> doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Perform database operations in the background
            GpsTrackingDB appDatabase = GpsTrackingDB.getInstance(MapViewCurrentTrackingStatus.this);
            GpsResponseDAO gpsResponseDAO = appDatabase.getGpsResponseDAO();
            return gpsResponseDAO.getAllData();
        }

        @Override
        protected void onPostExecute(List<GpsResponseData> dataList) {
            super.onPostExecute(dataList);
            if (mapViewCurrentTrackingStatus != null) {
                configureMapView();
                initializeLocationManager();
                displayGpsLiveResponseData(dataList);
                dismissProgress();
            }
        }
    }

    private void displayGpsLiveResponseData(List<GpsResponseData> dataList) {
        if (mapViewCurrentTrackingStatus == null) {
            Log.e("MapViewCurrentTracking", "MapView is null");
            return;
        }
        List<Overlay> overlays = mapViewCurrentTrackingStatus.getOverlays();
        List<Marker> markers = new ArrayList<>();
        for (GpsResponseData data : dataList) {
            if (data != null) {
                double latitude = data.getMX();
                double longitude = data.getMY();
                Marker pinkMarker = createMarker(latitude, longitude, "", R.drawable.dot_red);
                markers.add(pinkMarker);
            }
        }

        // Add all markers to overlays
        overlays.addAll(markers);

        // Refresh the map
        // mapViewCurrentTrackingStatus.invalidate();
    }

    private void stopTracking() {
        CurrentTrackingStatusData currentTrackingStatusData = new CurrentTrackingStatusData();
        boolean is_disabled_stop_tracking = currentTrackingStatusData.isIs_disabled_stop_tracking();
        if (is_disabled_stop_tracking) {
            Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
            stopService(serviceIntent);
            startActivity(new Intent(MapViewCurrentTrackingStatus.this, HomePage.class));
            // Remove location updates
            if (locationManager != null && locationListener != null) {
                try {
                    locationManager.removeUpdates(locationListener);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            mapViewCurrentTrackingStatus.getOverlays().clear();
            mapViewCurrentTrackingStatus.invalidate();
            Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
        }
    }

}
