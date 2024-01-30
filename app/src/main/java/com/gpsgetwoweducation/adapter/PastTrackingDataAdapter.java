package com.gpsgetwoweducation.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.gpsgetwoweducation.R;
import com.gpsgetwoweducation.activity.MapViewPastTrackingData;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastGpsTrackingData;
import com.gpsgetwoweducation.pojo.pasttrackingdata.PastTrackingDatas;
import com.gpsgetwoweducation.utilities.SharedPreferenceClass;
import com.gpsgetwoweducation.utils.DateUtils;

import java.util.List;

public class PastTrackingDataAdapter extends RecyclerView.Adapter<PastTrackingDataAdapter.ViewHolder> {
    private List<PastTrackingDatas> pastTrackingData;
    private SharedPreferenceClass sharedPreferenceClass;
    private Context context;

    public PastTrackingDataAdapter(Context context, List<PastTrackingDatas> pastTrackingData) {
        this.pastTrackingData = pastTrackingData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_tracking_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return pastTrackingData != null ? pastTrackingData.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tv_tracking_initiated_by, tv_tracking_start_time_date, tv_tracking_end_time_date;
        private AppCompatButton bt_map_view, bt_stop_tracking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tracking_initiated_by = itemView.findViewById(R.id.tv_tracking_initiated_by);
            tv_tracking_start_time_date = itemView.findViewById(R.id.tv_tracking_start_time_date);
            tv_tracking_end_time_date = itemView.findViewById(R.id.tv_tracking_end_time_date);

            bt_map_view = itemView.findViewById(R.id.bt_map_view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sharedPreferenceClass = new SharedPreferenceClass(context);
        PastTrackingDatas trackingStatusData = pastTrackingData.get(position);

        // Update the TextViews with relevant data
        holder.tv_tracking_initiated_by.setText(String.valueOf(trackingStatusData.getTracking_event_initiated_by_app_name()));
        holder.tv_tracking_start_time_date.setText(DateUtils.formatDateTime(trackingStatusData.getTrip_scheduled_start_datetime()));
        holder.tv_tracking_end_time_date.setText(DateUtils.formatDateTime(trackingStatusData.getTrip_end_datetime()));

        holder.bt_map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PastGpsTrackingData> trackingGpsData = trackingStatusData.getTracking_gps_data();
                if (trackingGpsData != null && !trackingGpsData.isEmpty()) {
                    Log.e("Adapter", "pickupStopDetails is not null");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("pastTrackingData", trackingStatusData);
                    Intent intent = new Intent(context, MapViewPastTrackingData.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    Log.e("Adapter", "pickupStopDetails is null or trackingGpsData is null/empty");
                    if (trackingGpsData == null) {
                        Log.e("Adapter", "trackingGpsData is null");
                    } else if (trackingGpsData.isEmpty()) {
                        Log.e("Adapter", "trackingGpsData is empty");
                    }
                    Toast.makeText(context, "No tracking data available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*holder.bt_map_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PastGpsTrackingData> trackingGpsData = trackingStatusData.getTracking_gps_data();
                if (trackingStatusData.getPickup_stop_details() != null && trackingGpsData != null && !trackingGpsData.isEmpty()) {
                    Log.e("Adapter", "pickupStopDetails is not null");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("pastTrackingData", trackingStatusData);
                    Intent intent = new Intent(context, MapViewPastTrackingData.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    Log.e("Adapter", "pickupStopDetails is null or trackingGpsData is null/empty");
                    Toast.makeText(context, "No tracking data available", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}
