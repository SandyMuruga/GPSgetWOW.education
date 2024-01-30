package com.gpsgetwoweducation.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.fragment.HomeFragment;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.InsertGpsTrackingData;
import com.gpsgetwoweducation.pojo.insertgpstrackingdata.SendGpsDataBody;
import com.gpsgetwoweducation.services.OverlayService;
import com.gpsgetwoweducation.utilities.CustomerLogoUtil;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.NetworkChangeReceiver;
import com.gpsgetwoweducation.viewmodel.InsertGpsTrackingViewModel;
import com.gpsgetwoweducation.views.ProgressDialogHUD;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomePage extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeListener {
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private static final long MIN_TIME = 10000;
    private static final float MIN_DISTANCE = 10f;

    private AppCompatImageView burger_menu, iv_customer_logo;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionToggle;
    private MapView mapTestGps;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private OverlayService overlayService;
    private String type = "";
    private ProgressDialogHUD mProgressDialog;
    private InsertGpsTrackingViewModel insertGpsTrackingViewModel;
    private List<InsertGpsTrackingData> insertGpsTrackingDataList;
    private NetworkChangeReceiver networkChangeReceiver;
    private LinearLayout ll_main, llNoInternet, ll_map_curent_location;
    private String businessName, country_code, customer_id, sub_domain_name;
    private SharedPreferenceClass sharedPreferenceClass;
    private boolean isGpsDataInserted = false;

    String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE));
        setContentView(R.layout.activity_home_page);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);

        mProgressDialog = new ProgressDialogHUD(this);
        ll_main = findViewById(R.id.ll_main);
        llNoInternet = findViewById(R.id.ll_NoInternet);
        ll_map_curent_location = findViewById(R.id.ll_map_curent_location);
        mapTestGps = findViewById(R.id.mapViewTestGPS);
        checkAndRequestLocationPermission();

        iv_customer_logo = findViewById(R.id.iv_customer_logo);
        AppCompatTextView tv_title_name = findViewById(R.id.tv_title_name);
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

        userImage = "https://cephapi.getster.tech/api/storage/" + country_code + "-" + customer_id + "/" + sharedPreferenceClass.get("chooseUserID") + ".png";
        //CustomerLogoUtil.loadImage(this, userImage, iv_user_logo);

        //startService(new Intent(this, OverlayService.class));

        ll_map_curent_location.setOnClickListener(v -> centerMapOnUserLocation());

        initializeUI();
        initializeDrawer();
        initializeMapAsync();
    }


    // internet connection
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        updateInternetStatus(isConnected);
        if (isConnected) {
            reloadPage();
        }
    }

    private void updateInternetStatus(boolean isConnected) {
        if (isConnected) {
            llNoInternet.setVisibility(View.GONE);
            ll_main.setVisibility(View.VISIBLE);
        } else {
            llNoInternet.setVisibility(View.VISIBLE);
            ll_main.setVisibility(View.GONE);
        }
    }
    // end internet connection

    private void reloadPage() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment instanceof HomeFragment) {
            navigatePage(new HomeFragment(), "getWOWstudyblock");
        }
    }

    private void checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // centerMapOnUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void initializeUI() {
        burger_menu = findViewById(R.id.burger_menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        overlayService = new OverlayService();
        type = getIntent().getStringExtra("type");
        burger_menu.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.LEFT));
        navigationView.setNavigationItemSelectedListener(this::handleNavigationItemSelected);
    }

    private boolean handleNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int itemId = item.getItemId();
        if (itemId == R.id.test_this_gps_tracker) {
            startActivity(new Intent(HomePage.this, HomePage.class));
        } else if (itemId == R.id.current_tracking_status) {
            startActivity(new Intent(HomePage.this, CurrentTrackingStatus.class));
        } else if (itemId == R.id.past_tracking_data) {
            startActivity(new Intent(HomePage.this, PastTrackingData.class));
        } else if (itemId == R.id.your_reg) {
            startActivity(new Intent(HomePage.this, YourRegistrationDetails.class));
        } else if (itemId == R.id.terms_and_cond) {
            startActivity(new Intent(HomePage.this, TermsAndConditions.class));
        } else {
            navigatePage(new HomeFragment(), item.getTitle().toString());
        }
        return false;
    }

    private void initializeDrawer() {
        actionToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionToggle);
        actionToggle.syncState();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerLayout.closeDrawers();
    }

    private class MapInitTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            initializeMap();
        }
    }

    // Call this method in your onCreate()
    private void initializeMapAsync() {
        new MapInitTask().execute();
    }

    private void initializeMap() {
        if (mapTestGps != null) {
            centerMapOnUserLocation();
            mapTestGps.getTileProvider().clearTileCache();
            Configuration.getInstance().setCacheMapTileCount((short) 12);
            Configuration.getInstance().setCacheMapTileOvershoot((short) 12);
            mapTestGps.setMultiTouchControls(true);
            mapTestGps.setTileSource(TileSourceFactory.MAPNIK);
            mapTestGps.setTileSource(new OnlineTileSourceBase("", 1, 20, 512, ".png",
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
            mapTestGps.onResume();
        } else {
            Toast.makeText(this, "Map is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapTestGps != null) {
            mapTestGps.onResume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mapTestGps != null) {
            mapTestGps.onPause();
        }
    }
    @SuppressLint("MissingPermission")
    private void centerMapOnUserLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();
                    Log.d("Location", "Latitude: " + userLatitude + ", Longitude: " + userLongitude);
                    //createOrUpdateMarker(userLatitude, userLongitude, "Your Location", R.drawable.bus_location_icon);
                    //centerMapOnLocation(userLatitude, userLongitude);
                    insertGpsTrackingData(userLatitude, userLongitude);
                    //  isGpsDataInserted = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(HomePage.this, "GPS provider disabled", Toast.LENGTH_SHORT).show();
            }
        };
        if (mapTestGps != null && locationListener != null && locationManager != null) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double userLatitude = lastKnownLocation.getLatitude();
                double userLongitude = lastKnownLocation.getLongitude();
                //createOrUpdateMarker(userLatitude, userLongitude, "Your Location", R.drawable.bus_location_icon);
                int locationMarkerDrawableId = R.drawable.bus_location_icon;
                createOrUpdateMarker(userLatitude, userLongitude, "Your Location", locationMarkerDrawableId, userImage);
                centerMapOnLocation(userLatitude, userLongitude);
            } else {
                Toast.makeText(this, "Unable to get your location", Toast.LENGTH_SHORT).show();
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } else {
            //Toast.makeText(this, "Map or location manager is null", Toast.LENGTH_SHORT).show();
        }
    }
    // Inset GPS Tracking Data
    private void insertGpsTrackingData(double latitude, double longitude) {
        insertGpsTrackingViewModel = ViewModelProviders.of(this).get(InsertGpsTrackingViewModel.class);
        String currentDateTime = getCurrentDateTime();
        SendGpsDataBody sendGpsDataBody = new SendGpsDataBody();
        sendGpsDataBody.setCustomer_id(sharedPreferenceClass.get("token_customer_id"));
        sendGpsDataBody.setCountry_code(sharedPreferenceClass.get("token_country_code"));
        sendGpsDataBody.setTime_zone_iana_string("Asia/Kolkata");
        sendGpsDataBody.setApp_login_user_id(sharedPreferenceClass.get("token_user_id"));
        sendGpsDataBody.setApp_type("0");
        sendGpsDataBody.setLatitude(String.valueOf(latitude));
        sendGpsDataBody.setLongitude(String.valueOf(longitude));
        sendGpsDataBody.setLast_tested_live_location_datetime(currentDateTime);
        showProgress("Loading...");
        insertGpsTrackingViewModel.sendGpsLiveData(sendGpsDataBody).observe(this, insertGpsTrackingResponse -> {
            dismissProgress();
            if (insertGpsTrackingResponse != null) {
                Object responseData = insertGpsTrackingResponse.getData();
                if (responseData instanceof List) {
                    List<InsertGpsTrackingData> dataList = (List<InsertGpsTrackingData>) responseData;
                    if (insertGpsTrackingDataList == null) {
                        insertGpsTrackingDataList = new ArrayList<>();
                    }
                    insertGpsTrackingDataList.addAll(dataList);
                    //Toast.makeText(this, "inserted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("InsertGpsTrackingData", "Unexpected data type received");
                }
            } else {
                Log.e("InsertGpsTrackingData", "Null response received");
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Marker createOrUpdateMarker(double latitude, double longitude, String title, int locationMarkerDrawableId, String userImageUrl) {
        if (mapTestGps == null) {
            Log.e("MapView", "mapTestGps is null");
            return null;
        }

        Marker locationMarker;
        List<Overlay> mapOverlays = mapTestGps.getOverlays();
        if (!mapOverlays.isEmpty() && mapOverlays.get(0) instanceof Marker) {
            locationMarker = (Marker) mapOverlays.get(0);
        } else {
            locationMarker = new Marker(mapTestGps);
            mapOverlays.add(locationMarker);
        }
        if (locationMarker != null) {
            try {
                locationMarker.setPosition(new GeoPoint(latitude, longitude));
                locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                locationMarker.setTitle(title);
                locationMarker.setPanToView(true);
                // Set drawable icon for location marker
                locationMarker.setIcon(getResources().getDrawable(locationMarkerDrawableId));

                // Load the user image dynamically using Glide for the user's image marker
                Glide.with(this)
                        .asBitmap()
                        .load(userImageUrl)
                        .override(73, 73)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                // Create or update the user's image marker
                                Bitmap roundedBitmap = getRoundedCornerBitmap(bitmap, 100);
                                // Create or update the user's image marker
                                Marker userImageMarker = new Marker(mapTestGps);
                                userImageMarker.setPosition(new GeoPoint(latitude, longitude));
                                userImageMarker.setAnchor(0.5f, 0.65f);
                                userImageMarker.setIcon(new BitmapDrawable(getResources(), roundedBitmap));
                                mapTestGps.getOverlays().add(userImageMarker);
                                mapTestGps.invalidate();
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Placeholder or clear action if needed
                            }
                        });
            } catch (Exception e) {
                Log.e("MapView", "Error updating marker: " + e.getMessage());
            }
        } else {
            Log.e("MapView", "Failed to create or update marker");
        }
        return locationMarker;
    }

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        Bitmap roundedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rect, radius, radius, paint);

        return roundedBitmap;
    }


    /*private Marker createOrUpdateMarker(double latitude, double longitude, String title, String imageUrl) {
        if (mapTestGps == null) {
            Log.e("MapView", "mapTestGps is null");
            return null;
        }

        Marker myMarker;
        List<Overlay> mapOverlays = mapTestGps.getOverlays();
        if (!mapOverlays.isEmpty() && mapOverlays.get(0) instanceof Marker) {
            myMarker = (Marker) mapOverlays.get(0);
        } else {
            myMarker = new Marker(mapTestGps);
            mapOverlays.add(myMarker);
        }

        if (myMarker != null) {
            try {
                myMarker.setPosition(new GeoPoint(latitude, longitude));
                myMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                myMarker.setTitle(title);
                myMarker.setPanToView(true);

                // Load the image dynamically using Glide
                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                // Set the loaded image as the marker icon
                                myMarker.setIcon(new BitmapDrawable(getResources(), bitmap));
                                mapTestGps.invalidate();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Placeholder or clear action if needed
                            }
                        });

            } catch (Exception e) {
                Log.e("MapView", "Error updating marker: " + e.getMessage());
            }
        } else {
            Log.e("MapView", "Failed to create or update marker");
        }
        return myMarker;
    }*/

    private void centerMapOnLocation(double latitude, double longitude) {
        IMapController mapController = mapTestGps.getController();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        mapController.setZoom(15.0);
    }

    public void navigatePage(final Fragment fragment, final String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        setTitle(title);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);*/
        startActivity(new Intent(HomePage.this, StopTracking.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // centerMapOnUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
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
