package com.gpsgetwoweducation.pojo.pasttrackingdata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DropStopDetails implements Parcelable {
    private String stop_id;
    private String is_removed;
    private String stop_name;
    private String route_id;
    private GpsCoordinatesPast gps_coordinates;
    private String effective_from_datetime;
    private String pickup_stop_sequence_number;
    private String pickup_travel_time_from_previous_stop_in_minutes;
    private String drop_stop_sequence_number;
    private String drop_travel_time_from_previous_stop_in_minutes;

    protected DropStopDetails(Parcel in) {
        stop_id = in.readString();
        is_removed = in.readString();
        stop_name = in.readString();
        route_id = in.readString();
        gps_coordinates = in.readParcelable(GpsCoordinatesPast.class.getClassLoader());
        effective_from_datetime = in.readString();
        pickup_stop_sequence_number = in.readString();
        pickup_travel_time_from_previous_stop_in_minutes = in.readString();
        drop_stop_sequence_number = in.readString();
        drop_travel_time_from_previous_stop_in_minutes = in.readString();
    }

    public static final Creator<DropStopDetails> CREATOR = new Creator<DropStopDetails>() {
        @Override
        public DropStopDetails createFromParcel(Parcel in) {
            return new DropStopDetails(in);
        }

        @Override
        public DropStopDetails[] newArray(int size) {
            return new DropStopDetails[size];
        }
    };

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getIs_removed() {
        return is_removed;
    }

    public void setIs_removed(String is_removed) {
        this.is_removed = is_removed;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public GpsCoordinatesPast getGps_coordinates() {
        return gps_coordinates;
    }

    /*public void setGps_coordinates(GpsCoordinatesPast gps_coordinates) {
        this.gps_coordinates = gps_coordinates;
    }*/

    public void setGps_coordinates(GpsCoordinatesPast gps_coordinates) {
        if (gps_coordinates == null) {
            this.gps_coordinates = new GpsCoordinatesPast(); // Initialize a new instance if null
        } else {
            this.gps_coordinates = gps_coordinates;
        }
    }

    public String getEffective_from_datetime() {
        return effective_from_datetime;
    }

    public void setEffective_from_datetime(String effective_from_datetime) {
        this.effective_from_datetime = effective_from_datetime;
    }

    public String getPickup_stop_sequence_number() {
        return pickup_stop_sequence_number;
    }

    public void setPickup_stop_sequence_number(String pickup_stop_sequence_number) {
        this.pickup_stop_sequence_number = pickup_stop_sequence_number;
    }

    public String getPickup_travel_time_from_previous_stop_in_minutes() {
        return pickup_travel_time_from_previous_stop_in_minutes;
    }

    public void setPickup_travel_time_from_previous_stop_in_minutes(String pickup_travel_time_from_previous_stop_in_minutes) {
        this.pickup_travel_time_from_previous_stop_in_minutes = pickup_travel_time_from_previous_stop_in_minutes;
    }

    public String getDrop_stop_sequence_number() {
        return drop_stop_sequence_number;
    }

    public void setDrop_stop_sequence_number(String drop_stop_sequence_number) {
        this.drop_stop_sequence_number = drop_stop_sequence_number;
    }

    public String getDrop_travel_time_from_previous_stop_in_minutes() {
        return drop_travel_time_from_previous_stop_in_minutes;
    }

    public void setDrop_travel_time_from_previous_stop_in_minutes(String drop_travel_time_from_previous_stop_in_minutes) {
        this.drop_travel_time_from_previous_stop_in_minutes = drop_travel_time_from_previous_stop_in_minutes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(stop_id);
        dest.writeString(is_removed);
        dest.writeString(stop_name);
        dest.writeString(route_id);
        dest.writeParcelable(gps_coordinates, flags);
        dest.writeString(effective_from_datetime);
        dest.writeString(pickup_stop_sequence_number);
        dest.writeString(pickup_travel_time_from_previous_stop_in_minutes);
        dest.writeString(drop_stop_sequence_number);
        dest.writeString(drop_travel_time_from_previous_stop_in_minutes);
    }
}
