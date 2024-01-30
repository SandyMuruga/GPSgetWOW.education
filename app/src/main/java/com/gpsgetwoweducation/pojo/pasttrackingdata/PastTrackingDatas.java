package com.gpsgetwoweducation.pojo.pasttrackingdata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PastTrackingDatas implements Parcelable {
    private String gps_tracking_event_id;
    private List<PastGpsTrackingData> tracking_gps_data;
    private String tracking_event_initiated_by_app_id;
    private String trip_scheduled_start_datetime;
    private String is_pickup_trip;
    private String route_id;
    private List<PickupStopDetails> pickup_stop_details;
    private List<DropStopDetails> drop_off_stop_details;
    private String trip_end_datetime;
    private String tracking_event_initiated_by_app_name;

    protected PastTrackingDatas(Parcel in) {
        gps_tracking_event_id = in.readString();
        tracking_gps_data = in.createTypedArrayList(PastGpsTrackingData.CREATOR);
        tracking_event_initiated_by_app_id = in.readString();
        trip_scheduled_start_datetime = in.readString();
        is_pickup_trip = in.readString();
        route_id = in.readString();
        pickup_stop_details = in.createTypedArrayList(PickupStopDetails.CREATOR);
        drop_off_stop_details = in.createTypedArrayList(DropStopDetails.CREATOR);
        trip_end_datetime = in.readString();
        tracking_event_initiated_by_app_name = in.readString();
    }

    public static final Creator<PastTrackingDatas> CREATOR = new Creator<PastTrackingDatas>() {
        @Override
        public PastTrackingDatas createFromParcel(Parcel in) {
            return new PastTrackingDatas(in);
        }

        @Override
        public PastTrackingDatas[] newArray(int size) {
            return new PastTrackingDatas[size];
        }
    };

    public String getGps_tracking_event_id() {
        return gps_tracking_event_id;
    }

    public void setGps_tracking_event_id(String gps_tracking_event_id) {
        this.gps_tracking_event_id = gps_tracking_event_id;
    }

    public List<PastGpsTrackingData> getTracking_gps_data() {
        return tracking_gps_data;
    }

    public void setTracking_gps_data(List<PastGpsTrackingData> tracking_gps_data) {
        this.tracking_gps_data = tracking_gps_data;
    }

    public String getTracking_event_initiated_by_app_id() {
        return tracking_event_initiated_by_app_id;
    }

    public void setTracking_event_initiated_by_app_id(String tracking_event_initiated_by_app_id) {
        this.tracking_event_initiated_by_app_id = tracking_event_initiated_by_app_id;
    }

    public String getTrip_scheduled_start_datetime() {
        return trip_scheduled_start_datetime;
    }

    public void setTrip_scheduled_start_datetime(String trip_scheduled_start_datetime) {
        this.trip_scheduled_start_datetime = trip_scheduled_start_datetime;
    }

    public String getIs_pickup_trip() {
        return is_pickup_trip;
    }

    public void setIs_pickup_trip(String is_pickup_trip) {
        this.is_pickup_trip = is_pickup_trip;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public List<PickupStopDetails> getPickup_stop_details() {
        return pickup_stop_details;
    }

    public void setPickup_stop_details(List<PickupStopDetails> pickup_stop_details) {
        this.pickup_stop_details = pickup_stop_details;
    }

    public List<DropStopDetails> getDrop_off_stop_details() {
        return drop_off_stop_details;
    }

    public void setDrop_off_stop_details(List<DropStopDetails> drop_off_stop_details) {
        this.drop_off_stop_details = drop_off_stop_details;
    }

    public String getTrip_end_datetime() {
        return trip_end_datetime;
    }

    public void setTrip_end_datetime(String trip_end_datetime) {
        this.trip_end_datetime = trip_end_datetime;
    }

    public String getTracking_event_initiated_by_app_name() {
        return tracking_event_initiated_by_app_name;
    }

    public void setTracking_event_initiated_by_app_name(String tracking_event_initiated_by_app_name) {
        this.tracking_event_initiated_by_app_name = tracking_event_initiated_by_app_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(gps_tracking_event_id);
        dest.writeTypedList(tracking_gps_data);
        dest.writeString(tracking_event_initiated_by_app_id);
        dest.writeString(trip_scheduled_start_datetime);
        dest.writeString(is_pickup_trip);
        dest.writeString(route_id);
        dest.writeTypedList(pickup_stop_details);
        dest.writeTypedList(drop_off_stop_details);
        dest.writeString(trip_end_datetime);
        dest.writeString(tracking_event_initiated_by_app_name);
    }
}
