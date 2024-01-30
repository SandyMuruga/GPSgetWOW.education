package com.gpsgetwoweducation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.activity.MapViewCurrentTrackingStatus;
import com.gpsgetwoweducation.activity.StopTracking;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.CurrentTrackingStatusData;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.DropOffRouteStopsItem;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.GpsCoordinates;
import com.gpsgetwoweducation.pojo.currenttrackingstatus.PickUpRouteStopsItem;
import com.gpsgetwoweducation.pojo.registredmobilenumber.RegisteredMobileNumberData;
import com.gpsgetwoweducation.pojo.stoplivecoordinates.StopLiveCoordinatesData;
import com.gpsgetwoweducation.services.LocationUpdatesService;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.DateUtils;
import com.gpsgetwoweducation.viewmodel.StopTrackingDataViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurrentTrackingStatusAdapter extends RecyclerView.Adapter<CurrentTrackingStatusAdapter.ViewHolder> {
    private List<CurrentTrackingStatusData> currentTrackingStatusData;
    private SharedPreferenceClass sharedPreferenceClass;
    private Context context;
    private StopTrackingDataViewModel stopTrackingDataViewModel;
    private LocationUpdatesService mService;

    private SwipeRefreshLayout swipeRefreshLayout;
    public CurrentTrackingStatusAdapter(Context context, List<CurrentTrackingStatusData> currentTrackingStatusData,
                                        LocationUpdatesService mService,SwipeRefreshLayout swipeRefreshLayout) {
        this.currentTrackingStatusData = currentTrackingStatusData;
        this.context = context;
        this.mService = mService;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public void setLocationUpdatesService(LocationUpdatesService service) {
        this.mService = service;
    }

    public void updateData(List<CurrentTrackingStatusData> newData) {
        currentTrackingStatusData.clear();
        currentTrackingStatusData.addAll(newData);
        notifyDataSetChanged();

        // Call setRefreshing(false) to stop the refreshing animation
        swipeRefreshLayout.setRefreshing(false);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_tracking_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        //return currentTrackingStatusData.size();
        if (currentTrackingStatusData != null) {
            return currentTrackingStatusData.size();
        } else {
            return 0; // or handle it according to your logic
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tv_tracking_initiated_by, tv_tracking_start_time_date, tv_tracking_end_time_date;
        private AppCompatButton bt_map_view, bt_stop_tracking;

        private LocationUpdatesService mService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tracking_initiated_by = itemView.findViewById(R.id.tv_tracking_initiated_by);
            tv_tracking_start_time_date = itemView.findViewById(R.id.tv_tracking_start_time_date);
            tv_tracking_end_time_date = itemView.findViewById(R.id.tv_tracking_end_time_date);

            bt_map_view = itemView.findViewById(R.id.bt_map_view);
            bt_stop_tracking = itemView.findViewById(R.id.bt_stop_tracking);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sharedPreferenceClass = new SharedPreferenceClass(context);
        CurrentTrackingStatusData trackingStatusData = currentTrackingStatusData.get(position);

        // Update the TextViews with relevant data
        holder.tv_tracking_initiated_by.setText(String.valueOf(trackingStatusData.getTracking_event_initiated_by_app_name()));
        holder.tv_tracking_start_time_date.setText(DateUtils.formatDateTime(trackingStatusData.getTrip_scheduled_start_datetime()));
        holder.tv_tracking_end_time_date.setText(DateUtils.formatDateTime(trackingStatusData.getTrip_scheduled_end_datetime()));

        String gps_tracking_event_id = String.valueOf(trackingStatusData.getGps_tracking_event_id());
        String tracking_event_initiated_by_app_name = String.valueOf(trackingStatusData.getTracking_event_initiated_by_app_name());
        String trip_scheduled_start_datetime = String.valueOf(trackingStatusData.getTrip_scheduled_start_datetime());
        boolean is_disabled_stop_tracking = trackingStatusData.isIs_disabled_stop_tracking();

        if (is_disabled_stop_tracking == true) {
            holder.bt_stop_tracking.setVisibility(View.GONE);
        } else {
            holder.bt_stop_tracking.setVisibility(View.VISIBLE);
        }

            holder.bt_map_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (trackingStatusData.getIs_pickup_trip() == 1) {
                        if (trackingStatusData.getPickup_stop_details() != null &&
                                !trackingStatusData.getPickup_stop_details().getPickUpRouteStops().isEmpty()) {
                            Log.e("Adapter", "pickupStopDetails is not null");
                            bundle.putParcelableArrayList("pickUpRouteStops",
                                    new ArrayList<>(trackingStatusData.getPickup_stop_details().getPickUpRouteStops()));

                             // Get the last pickup stop's coordinates
                            List<PickUpRouteStopsItem> pickUpRouteStopsList = trackingStatusData.getPickup_stop_details().getPickUpRouteStops();
                            if (!pickUpRouteStopsList.isEmpty()) {
                                PickUpRouteStopsItem lastPickupStop = pickUpRouteStopsList.get(pickUpRouteStopsList.size() - 1);

                                GpsCoordinates pickupStopCoordinates = lastPickupStop.getGps_coordinates();
                                if (pickupStopCoordinates != null) {
                                    double lastLatitude = pickupStopCoordinates.getX();
                                    double lastLongitude = pickupStopCoordinates.getY();
                                    sharedPreferenceClass.set("last_latitude", String.valueOf(lastLatitude));
                                    sharedPreferenceClass.set("last_longitude", String.valueOf(lastLongitude));
                                } else {
                                    Log.e("Adapter", "GpsCoordinates is null for the last pickup stop");
                                }
                            }
                        } else {
                            Log.e("Adapter", "pickupStopDetails is null");
                        }
                    } else if (trackingStatusData.getIs_pickup_trip() == 0) {
                        if (trackingStatusData.getDrop_off_stop_details() != null &&
                                !trackingStatusData.getDrop_off_stop_details().getDropOffRouteStops().isEmpty()) {
                            Log.e("Adapter", "dropOffStopDetails is not null");
                            bundle.putParcelableArrayList("dropOffRouteStops",
                                    new ArrayList<>(trackingStatusData.getDrop_off_stop_details().getDropOffRouteStops()));
                            // Get the last drop-off stop's coordinates
                            List<DropOffRouteStopsItem> dropOffRouteStopsList = trackingStatusData.getDrop_off_stop_details().getDropOffRouteStops();
                            if (!dropOffRouteStopsList.isEmpty()) {
                                DropOffRouteStopsItem lastDropOffStop = dropOffRouteStopsList.get(dropOffRouteStopsList.size() - 1);
                                GpsCoordinates dropOffCoordinates = lastDropOffStop.getGps_coordinates();
                                if (dropOffCoordinates != null) {
                                    double lastLatitude = dropOffCoordinates.getX();
                                    double lastLongitude = dropOffCoordinates.getY();
                                    sharedPreferenceClass.set("last_latitude_dropoff", String.valueOf(lastLatitude));
                                    sharedPreferenceClass.set("last_longitude_dropoff", String.valueOf(lastLongitude));
                                } else {
                                    Log.e("Adapter", "GpsCoordinates is null for the last drop-off stop");
                                }
                            } else {
                                Log.e("Adapter", "dropOffRouteStopsList is empty");
                            }
                        } else {
                            Log.e("Adapter", "dropOffStopDetails is null");
                        }
                    }
                    sharedPreferenceClass.set("gps_tracking_event_id", gps_tracking_event_id);
                    sharedPreferenceClass.set("tracking_event_initiated_by_app_name", tracking_event_initiated_by_app_name);
                    sharedPreferenceClass.set("trip_scheduled_start_datetime", trip_scheduled_start_datetime);

                    Intent intent = new Intent(context, MapViewCurrentTrackingStatus.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        holder.bt_stop_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // stopTracking();
                Intent intent = new Intent(context, StopTracking.class);
                context.startActivity(intent);
            }
        });
    }


}
