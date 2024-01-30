package com.gpsgetwoweducation.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.pojo.registeredcustomer.RegistredBusinessData;
import com.gpsgetwoweducation.utilities.PermissionsClass;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.utils.NetworkUtils;
import com.gpsgetwoweducation.viewmodel.RegisteredCustomerViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class RegisteredCustomerList extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private MapView map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private RelativeLayout rl_map_icon;
    private AppCompatImageView iv_map_center_icon;
    private SearchView searchView;

    private double yourLocationLatitude = 0;
    private double yourLocationLongitude = 0;
    private ItemizedOverlayWithFocus<OverlayItem> itemizedOverlay;
    private List<RegistredBusinessData> registredBusinessDataList;
    private RegisteredCustomerViewModel registeredCustomerViewModel;
    private SharedPreferenceClass sharedPreferenceClass;
    private Bitmap customMarkerBitmap;
    private List<Marker> markerList = new ArrayList<>();
    private boolean isLocationPermissionGranted = false;
    private LinearLayout noInternetLayout;
    private ProgressDialogHUD mProgressDialog;
    private boolean isDataLoaded = false;

    private boolean isOverlayPermissionPageOpened = false;
    private boolean isUsageAccessPermissionPageOpened = false;

    int MY_OVERLAY_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_customer);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        iv_map_center_icon = findViewById(R.id.iv_map_center_icon);
        rl_map_icon = findViewById(R.id.rl_map_icon);
        searchView = findViewById(R.id.searchView);
        mProgressDialog = new ProgressDialogHUD(this);
        noInternetLayout = findViewById(R.id.layoutNoInternet);
        iv_map_center_icon.setOnClickListener(v -> centerMapOnUserLocation());

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(this, "Please enable \"Appear on top\" settings.", Toast.LENGTH_SHORT).show();
        } */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, MY_OVERLAY_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "Please enable \"Appear on top\" settings.", Toast.LENGTH_SHORT).show();
        }

        if (!checkAndRequestLocationPermission()) {
            requestLocationPermission();
        } else {
            startLocationUpdates();
        }

        if (!hasUsageAccessPermission()) {
            requestUsageAccessPermission();
        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            updateInternetStatus(true);
            checkAndRequestLocationPermissions();
        } else {
            updateInternetStatus(false);
        }

        String isLogin = sharedPreferenceClass.get("isLogin");
        String isLogout = sharedPreferenceClass.get("isLogout");
        if (isLogin.equalsIgnoreCase("yes")) {
            Intent intent = new Intent(RegisteredCustomerList.this, HomePage.class);
            startActivity(intent);
        } else if (isLogout.equalsIgnoreCase("yes")) {
            finish();
            Intent intent = new Intent(RegisteredCustomerList.this, FaceLogin.class);
            startActivity(intent);
        }

        map = findViewById(R.id.mapView);
        if (map != null) {
            centerMapOnUserLocation();
            loadBusinessCustomerMarkers();
            map.getTileProvider().clearTileCache();
            Configuration.getInstance().setCacheMapTileCount((short) 12);
            Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
            map.setMultiTouchControls(true);
            map.setTileSource(new OnlineTileSourceBase("", 1, 20, 512, ".png",
                    new String[]{"https://a.tile.openstreetmap.org/"}) {
                @Override
                public String getTileURLString(long pMapTileIndex) {
                    return getBaseUrl()
                            + MapTileIndex.getZoom(pMapTileIndex)
                            + "/" + MapTileIndex.getX(pMapTileIndex)
                            + "/" + MapTileIndex.getY(pMapTileIndex)
                            + mImageFilenameEnding;
                }
            });
        } else {
            Toast.makeText(this, "Map is null", Toast.LENGTH_SHORT).show();
        }

        rl_map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBusinessCustomerMarkers();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText.toLowerCase());
                return false;
            }
        });
        itemizedOverlay = new ItemizedOverlayWithFocus<>(
                this,
                new ArrayList<>(),
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
    }


    // internet connection
    @Override
    public void onNetworkChanged(boolean isConnected) {
        updateInternetStatus(isConnected);
        if (isConnected && isLocationPermissionGranted) {
            if (!isLocationEnabled()) {
                showLocationServicesAlertDialog();
            }
        }
    }
    private void updateInternetStatus(boolean isConnected) {
        if (isConnected) {
            noInternetLayout.setVisibility(View.GONE);
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }
    // end internet connection

    private boolean checkAndRequestLocationPermission() {
        if (!checkLocationPermission()) {
            requestLocationPermission();
            return false;
        } else {
            startLocationUpdates();
            return true;
        }
    }
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
                centerMapOnUserLocation();
            } else {
                // Toast.makeText(this, "Please enable \"Appear on top\" settings.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void checkAndRequestLocationPermissions() {
        if (checkLocationPermission()) {
            isLocationPermissionGranted = true;
        }
    }
    private boolean isLocationEnabled() {
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }
    private void startLocationUpdates() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();
                    createOrUpdateMarker(userLatitude, userLongitude, "Your Location", R.drawable.ic_pink_marker);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Handle status changes if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Handle provider enabled
            }

            @Override
            public void onProviderDisabled(String provider) {
                // Handle provider disabled
            }
        };
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        }
    }
    private boolean hasUsageAccessPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int uid = android.os.Process.myUid();
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    private void requestUsageAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, "Please enable \"Usage data access\" settings.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
    private void showLocationServicesAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location services are disabled. Do you want to enable them?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(locationSettingsIntent, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void centerMapOnLocation(double latitude, double longitude) {
        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        mapController.setZoom(15.0);
    }

    private Marker createOrUpdateMarker(double latitude, double longitude, String title, int iconResource) {
        Marker myMarker;
        List<Overlay> mapOverlays = map.getOverlays();
        if (!mapOverlays.isEmpty() && mapOverlays.get(0) instanceof Marker) {
            myMarker = (Marker) mapOverlays.get(0);
        } else {
            myMarker = new Marker(map);
            mapOverlays.add(myMarker);
        }
        myMarker.setPosition(new GeoPoint(latitude, longitude));
        myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMarker.setTitle(title);
        myMarker.setPanToView(true);
        myMarker.setIcon(getResources().getDrawable(iconResource));
        map.invalidate();
        return myMarker;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            new StopLocationUpdatesTask().execute();
        }
        // unregisterReceiver(networkChangeReceiver);
    }
    private class StopLocationUpdatesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            locationManager.removeUpdates(locationListener);
            return null;
        }
    }
    @SuppressLint("MissingPermission")
    private void centerMapOnUserLocation() {
        if (map != null && locationListener != null && locationManager != null) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double userLatitude = lastKnownLocation.getLatitude();
                double userLongitude = lastKnownLocation.getLongitude();
                centerMapOnLocation(userLatitude, userLongitude);
                if (!isDataLoaded) {
                    getRegisteredCustomerList(userLatitude, userLongitude);
                    isDataLoaded = true; // Set the flag to indicate that data has been loaded
                }
            } else {
                Toast.makeText(this, "Unable to get your location", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Toast.makeText(this, "Map or location manager is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRegisteredCustomerList(double latitude, double longitude) {
        showProgress("Loading...");
        registeredCustomerViewModel = new ViewModelProvider(this).get(RegisteredCustomerViewModel.class);
        registeredCustomerViewModel.getRegisteredCustomerLiveData(String.valueOf(latitude), String.valueOf(longitude)).observe(this, registeredCustomerResponse -> {
            dismissProgress();
            if (registeredCustomerResponse != null && registeredCustomerResponse.getData() != null) {
                registredBusinessDataList = registeredCustomerResponse.getData();
                Log.d("RegisteredCustomer", "getRegisteredCustomerList: " + registredBusinessDataList.size());
                if (!registredBusinessDataList.isEmpty()) {
                    for (int i = 0; i < registredBusinessDataList.size(); i++) {
                        displayMarkersOnMap(registredBusinessDataList.get(i));
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void loadBusinessCustomerMarkers() {
        Log.d("DebugTest", "Inside loadBusinessCustomerMarkers");
        if (registredBusinessDataList != null && !registredBusinessDataList.isEmpty()) {
            // Clear existing markers
            map.getOverlays().removeAll(markerList);
            markerList.clear();
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double userLatitude = lastKnownLocation.getLatitude();
                double userLongitude = lastKnownLocation.getLongitude();

                Log.d("LocationDebug", "User Location: " + userLatitude + ", " + userLongitude);

                // Filter businesses within 50km
                for (int i = 0; i < registredBusinessDataList.size(); i++) {
                    RegistredBusinessData businessData = registredBusinessDataList.get(i);
                    double businessLatitude = businessData.getGps_coordinates().getX();
                    double businessLongitude = businessData.getGps_coordinates().getY();
                    Log.d("LocationDebug", "Business Location: " + businessLatitude + ", " + businessLongitude);
                    // Calculate distance between user and business
                    double distance = calculateDistance(userLatitude, userLongitude, businessLatitude, businessLongitude);
                    // Show businesses within 50km
                    //  if (distance <= 50_000) {  // 50km in meters
                    displayMarkersOnMap(businessData);
                    // }
                }
            }
        }
    }

    private void performSearch(String searchTerm) {
        if (registredBusinessDataList != null && !registredBusinessDataList.isEmpty()) {
            for (RegistredBusinessData businessData : registredBusinessDataList) {
                if (businessData.getRegistered_educational_institution_name().toLowerCase().contains(searchTerm)) {
                    double latitude = businessData.getGps_coordinates().getX();
                    double longitude = businessData.getGps_coordinates().getY();
                    centerMapOnLocation(latitude, longitude);
                    return; // Stop searching after finding the first match
                }
            }
            Toast.makeText(this, "Business not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayMarkersOnMap(RegistredBusinessData registredBusinessData) {
        if (registredBusinessData != null) {
            double latitude = registredBusinessData.getGps_coordinates().getX();
            double longitude = registredBusinessData.getGps_coordinates().getY();
            String title = registredBusinessData.getRegistered_educational_institution_name();
            Marker existingMarker = findMarkerForBusiness(registredBusinessData);
            if (existingMarker != null) {
                existingMarker.setPosition(new GeoPoint(latitude, longitude));
            } else {
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                Marker newMarker = new Marker(map);
                newMarker.setPosition(geoPoint);
                newMarker.setTitle(title);
                newMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
                initializeCustomMarkerBitmap(title);
                newMarker.setIcon(new BitmapDrawable(getResources(), customMarkerBitmap));
                newMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        showToastWithBusinessName(title, marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
                        return true;
                    }
                });
                // Add the marker to the map and the list
                map.getOverlays().add(newMarker);
                markerList.add(newMarker);
            }
            centerMapOnUserLocation();
        }
    }

    // Helper method to find a marker for a given business
    private Marker findMarkerForBusiness(RegistredBusinessData businessData) {
        for (Marker marker : markerList) {
            if (marker.getPosition().equals(new GeoPoint(businessData.getGps_coordinates().getX(),
                    businessData.getGps_coordinates().getY()))) {
                return marker;
            }
        }
        return null;
    }

    private void initializeCustomMarkerBitmap(String businessName) {
        // Inflate the custom marker layout
        View customMarkerView = LayoutInflater.from(this).inflate(R.layout.item_custom_marker_layout, null);

        // Set the business name in the TextView
        AppCompatTextView markerTitle = customMarkerView.findViewById(R.id.marker_text);
        markerTitle.setText(businessName);

        // Measure and layout the view
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());

        // Create a Bitmap from the layout
        customMarkerView.setDrawingCacheEnabled(true);
        customMarkerView.buildDrawingCache();
        customMarkerBitmap = Bitmap.createBitmap(customMarkerView.getDrawingCache());

        // Clear the drawing cache to avoid memory leaks
        customMarkerView.setDrawingCacheEnabled(false);
    }

    private void showToastWithBusinessName(String businessName, double latitude, double longitude) {
        for (int i = 0; i < registredBusinessDataList.size(); i++) {
            if (registredBusinessDataList.get(i).getGps_coordinates().getX() == latitude && registredBusinessDataList.get(i).getGps_coordinates().getY() == longitude) {
                String sub_domain_name = registredBusinessDataList.get(i).getSub_domain_name();
                String customer_id = registredBusinessDataList.get(i).getCustomer_id();
                String country_code = registredBusinessDataList.get(i).getCountry_code();
                sharedPreferenceClass.set("sub_domain_name", sub_domain_name);
                sharedPreferenceClass.set("businessName", businessName);
                sharedPreferenceClass.set("customer_id", customer_id);
                sharedPreferenceClass.set("country_code1", country_code);
                startActivity(new Intent(this, EnterRegisteredMobileNumber.class));
                break;
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;
        // Convert latitude and longitude from degrees to radians
        double lat1Radians = Math.toRadians(lat1);
        double lon1Radians = Math.toRadians(lon1);
        double lat2Radians = Math.toRadians(lat2);
        double lon2Radians = Math.toRadians(lon2);

        // Haversine formula to calculate the distance
        double dlon = lon2Radians - lon1Radians;
        double dlat = lat2Radians - lat1Radians;
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1Radians) * Math.cos(lat2Radians)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance * 1000; // Convert to meters
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
}