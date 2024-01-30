package com.gpsgetwoweducation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.GpsCoordinates;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.PickUpRouteStopsItem;
import com.gpsgetwoweducation.pojo.pasttrackingdata.DropStopDetails;
import com.gpsgetwoweducation.pojo.pasttrackingdata.GpsCoordinatesPast;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastGpsTrackingData;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDatas;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PickupStopDetails;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.DateUtils;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.util.ArrayList;
import java.util.List;

public class MapViewPastTrackingData extends AppCompatActivity {
    private MapView mapViewPastTrackingData;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final long MIN_TIME = 10000;
    private static final float MIN_DISTANCE = 10f;
    private boolean shouldCenterMap = true;
    private AppCompatTextView tv_no_data, tv_tracking_initiated_by, tv_tracking_start_date_time;
    private AppCompatImageView iv_back, iv_customer_logo;
    private String businessName, country_code, customer_id, sub_domain_name;
    private ProgressDialogHUD mProgressDialog;
    private SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_past_tracking_data);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        mapViewPastTrackingData = findViewById(R.id.mapViewPastTrackingData);
        tv_no_data = findViewById(R.id.tv_no_data);

        tv_tracking_initiated_by = findViewById(R.id.tv_tracking_initiated_by);
        tv_tracking_start_date_time = findViewById(R.id.tv_tracking_start_date_time);

        mProgressDialog = new ProgressDialogHUD(this);

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

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pastTrackingData")) {
            PastTrackingDatas pastTrackingData = intent.getParcelableExtra("pastTrackingData");
            Log.d("SendingActivity", "PastTrackingData: " + pastTrackingData.toString());
            String tracking_event_initiated_by_app_name = pastTrackingData.getTracking_event_initiated_by_app_name();
            String trip_scheduled_start_datetime = pastTrackingData.getTrip_scheduled_start_datetime();
            tv_tracking_initiated_by.setText(tracking_event_initiated_by_app_name);
            tv_tracking_start_date_time.setText(DateUtils.formatDateTime(trip_scheduled_start_datetime));
        } else {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }

        configureMapView();
        initializeLocationManager();
    }

    private void configureMapView() {
        if (mapViewPastTrackingData != null) {
            mapViewPastTrackingData.getTileProvider().clearTileCache();
            Configuration.getInstance().setCacheMapTileCount((short) 12);
            Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
            mapViewPastTrackingData.setMultiTouchControls(true);
            mapViewPastTrackingData.setTileSource(TileSourceFactory.MAPNIK);

            mapViewPastTrackingData.getController().setCenter(new GeoPoint(0, 0));
            mapViewPastTrackingData.getController().setZoom(2.0);
        } else {
            Toast.makeText(this, "Map is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapViewPastTrackingData != null) {
            mapViewPastTrackingData.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapViewPastTrackingData != null) {
            mapViewPastTrackingData.onPause();
        }
    }

    private void initializeLocationManager() {
        showProgress("Loading...");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mapViewPastTrackingData == null) {
                    Log.e("MapViewPastTrackingData", "MapView is null");
                    return;
                }
                mapViewPastTrackingData.getOverlays().clear();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    PastTrackingDatas pastTrackingData = bundle.getParcelable("pastTrackingData");
                    if (pastTrackingData != null) {
                        // Add markers for pickup stops
                        List<PickupStopDetails> pickupStopDetailsList = pastTrackingData.getPickup_stop_details();
                        if (pickupStopDetailsList != null && !pickupStopDetailsList.isEmpty()) {
                            for (PickupStopDetails pickupStop : pickupStopDetailsList) {
                                Marker marker = new Marker(mapViewPastTrackingData);
                                double xCoordinate = pickupStop.getGps_coordinates().getX();
                                double yCoordinate = pickupStop.getGps_coordinates().getY();
                                marker.setPosition(new GeoPoint(xCoordinate, yCoordinate));
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                marker.setTitle(pickupStop.getStop_name()); // Set stop name as the marker title
                                marker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Set your ic_location drawable here

                                MyInfoWindow infoWindow = new MyInfoWindow(marker, mapViewPastTrackingData, pickupStop.getStop_name(), pickupStop.getGps_coordinates());
                                marker.setInfoWindow(infoWindow);

                                mapViewPastTrackingData.getOverlays().add(marker);

                                marker.showInfoWindow();
                            }

                            // Center the map on the first pickup stop
                            PickupStopDetails firstStop = pickupStopDetailsList.get(0);
                            mapViewPastTrackingData.getController().setCenter(new GeoPoint(firstStop.getGps_coordinates().getX(), firstStop.getGps_coordinates().getY()));
                            mapViewPastTrackingData.getController().setZoom(17.0);
                        }
                        // Add markers for drop-off stops
                        List<DropStopDetails> dropOffStopDetailsList = pastTrackingData.getDrop_off_stop_details();
                        if (dropOffStopDetailsList != null && !dropOffStopDetailsList.isEmpty()) {
                            // Loop through drop-off stops
                            for (DropStopDetails dropOffStop : dropOffStopDetailsList) {
                                // Create a marker for each drop-off stop
                                Marker marker = new Marker(mapViewPastTrackingData);
                                double xCoordinate = dropOffStop.getGps_coordinates().getX();
                                double yCoordinate = dropOffStop.getGps_coordinates().getY();
                                marker.setPosition(new GeoPoint(xCoordinate, yCoordinate));
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                marker.setTitle(dropOffStop.getStop_name());
                                marker.setIcon(getResources().getDrawable(R.drawable.ic_location));

                                MyInfoWindow infoWindow = new MyInfoWindow(marker, mapViewPastTrackingData, dropOffStop.getStop_name(), dropOffStop.getGps_coordinates());
                                marker.setInfoWindow(infoWindow);
                                mapViewPastTrackingData.getOverlays().add(marker);
                                //show tile
                                marker.showInfoWindow();
                            }

                            // Center the map on the first drop-off stop
                            DropStopDetails firstDropOffStop = dropOffStopDetailsList.get(0);
                            mapViewPastTrackingData.getController().setCenter(new GeoPoint(firstDropOffStop.getGps_coordinates().getX(), firstDropOffStop.getGps_coordinates().getY()));
                            mapViewPastTrackingData.getController().setZoom(15.0);
                        }
                        // Display dot markers for tracking_gps_data coordinates
                        List<PastGpsTrackingData> trackingDataList = pastTrackingData.getTracking_gps_data();
                        for (PastGpsTrackingData trackingData : trackingDataList) {
                            Marker intermediateMarker = new Marker(mapViewPastTrackingData);
                            intermediateMarker.setPosition(new GeoPoint(trackingData.getX(), trackingData.getY()));
                            intermediateMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            // Create a custom dot marker
                            Drawable dotDrawable = getResources().getDrawable(R.drawable.dot_red);
                            int dotSize = 20;
                            Bitmap bitmap = Bitmap.createBitmap(dotSize, dotSize, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            dotDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            dotDrawable.draw(canvas);
                            BitmapDrawable customDotDrawable = new BitmapDrawable(getResources(), bitmap);

                            intermediateMarker.setIcon(customDotDrawable);
                            mapViewPastTrackingData.getOverlays().add(intermediateMarker);
                        }

                        // If you want to center the map on the last tracking point, you can add the following lines:
                        if (!trackingDataList.isEmpty()) {
                            PastGpsTrackingData lastLocation = trackingDataList.get(trackingDataList.size() - 1);
                            mapViewPastTrackingData.getController().setCenter(new GeoPoint(lastLocation.getX(), lastLocation.getY()));
                            mapViewPastTrackingData.getController().setZoom(15.0);
                        }

                        if ((pickupStopDetailsList == null || pickupStopDetailsList.isEmpty()) && (dropOffStopDetailsList == null || dropOffStopDetailsList.isEmpty())) {
                            tv_no_data.setVisibility(View.VISIBLE);
                            tv_no_data.setText("No pickup and drop-off stop details available");
                        }
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        tv_no_data.setText("No tracking data available");
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    tv_no_data.setText("No tracking data available");
                    Log.d("MapViewPastTrackingData", "bundle is null");
                }

                // Refresh the map after all operations
                mapViewPastTrackingData.invalidate();
                dismissProgress();
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


   /* private void initializeLocationManager() {
        showProgress("Loading...");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mapViewPastTrackingData == null) {
                    Log.e("MapViewPastTrackingData", "MapView is null");
                    return;
                }
                mapViewPastTrackingData.getOverlays().clear();

                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    List<PastGpsTrackingData> pastTrackingData = bundle.getParcelableArrayList("pastTrackingData");
                    if (pastTrackingData != null && !pastTrackingData.isEmpty()) {
                        // Add a marker for the start location
                        PastGpsTrackingData startLocation = pastTrackingData.get(0);
                        Marker startMarker = new Marker(mapViewPastTrackingData);
                        startMarker.setPosition(new GeoPoint(startLocation.getX(), startLocation.getY()));
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_location)); // Replace with your start marker drawable
                        mapViewPastTrackingData.getOverlays().add(startMarker);

                        // Add markers for intermediate locations (dots)
                        for (int i = 1; i < pastTrackingData.size() - 1; i++) {
                            PastGpsTrackingData intermediateLocation = pastTrackingData.get(i);
                            Marker intermediateMarker = new Marker(mapViewPastTrackingData);
                            intermediateMarker.setPosition(new GeoPoint(intermediateLocation.getX(), intermediateLocation.getY()));
                            intermediateMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            Drawable dotDrawable = getResources().getDrawable(R.drawable.dot_red);
                            int dotSize = 20;
                            Bitmap bitmap = Bitmap.createBitmap(dotSize, dotSize, Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            dotDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            dotDrawable.draw(canvas);
                            BitmapDrawable customDotDrawable = new BitmapDrawable(getResources(), bitmap);
                            intermediateMarker.setIcon(customDotDrawable);
                            mapViewPastTrackingData.getOverlays().add(intermediateMarker);
                        }
                        // Add a marker for the end location
                        PastGpsTrackingData endLocation = pastTrackingData.get(pastTrackingData.size() - 1);
                        Marker endMarker = new Marker(mapViewPastTrackingData);
                        endMarker.setPosition(new GeoPoint(endLocation.getX(), endLocation.getY()));
                        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
                        mapViewPastTrackingData.getOverlays().add(endMarker);

                        // Center the map on the last marker (end location)
                        mapViewPastTrackingData.getController().setCenter(endMarker.getPosition());
                        mapViewPastTrackingData.getController().setZoom(17.0);

                        // Center the map on the midpoint between start and end locations
                        GeoPoint startLocationPoint = new GeoPoint(startLocation.getX(), startLocation.getY());
                        GeoPoint endLocationPoint = new GeoPoint(endLocation.getX(), endLocation.getY());
                        GeoPoint midpoint = calculateMidpoint(startLocationPoint, endLocationPoint);
                        mapViewPastTrackingData.getController().setCenter(midpoint);
                        mapViewPastTrackingData.getController().setZoom(17.0);
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        tv_no_data.setText("No tracking data available");
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    tv_no_data.setText("No tracking data available");
                    Log.d("MapViewPastTrackingData", "bundle is null");
                }

                // Refresh the map after all operations
                mapViewPastTrackingData.invalidate();
                dismissProgress();
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }*/

    private GeoPoint calculateMidpoint(GeoPoint point1, GeoPoint point2) {
        double lat = (point1.getLatitude() + point2.getLatitude()) / 2.0;
        double lon = (point1.getLongitude() + point2.getLongitude()) / 2.0;
        return new GeoPoint(lat, lon);
    }

    private class MyInfoWindow extends InfoWindow {
        private String stopName;
        private GpsCoordinatesPast gpsCoordinatesPast;

        public MyInfoWindow(Marker marker, MapView mapView, String stopName, GpsCoordinatesPast gpsCoordinatesPast) {
            super(R.layout.map_stop_name, mapView);

            this.stopName = stopName;
            this.gpsCoordinatesPast = gpsCoordinatesPast;
            onClose();
            onOpen(marker);
        }

        @Override
        public void onClose() {
            // Implement any cleanup or actions when the info window is closed
        }

        @Override
        public void onOpen(Object arg0) {
            if (gpsCoordinatesPast != null) {
                double latitude = gpsCoordinatesPast.getY();
                double longitude = gpsCoordinatesPast.getX();
                String coordinates = "Latitude: " + latitude + ", Longitude: " + longitude;

                // Now you can use 'stopName' and 'coordinates' as needed

                // Set the title (stop name) to be displayed by default
                TextView titleTextView = mView.findViewById(R.id.stopNameTextView);
                titleTextView.setText(stopName);
            } else {
                // Handle the case where gpsCoordinates is null
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
    }

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

    /*class CustomInfoWindow extends InfoWindow {
        private String title;

        public CustomInfoWindow(MapView mapView, String title) {
            super(R.layout.map_stop_name, mapView); // custom_info_window is a layout XML for your InfoWindow
            this.title = title;
        }

        @Override
        public void onClose() {
            // Implementation not needed for this example
        }

        @Override
        public void onOpen(Object item) {
            // Set the title in the InfoWindow layout
            TextView titleTextView = mView.findViewById(R.id.stopNameTextView);
            titleTextView.setText(title);
        }
    }*/
}