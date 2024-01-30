package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class PickUpRouteStopsItem implements Parcelable {
	private int stop_id;
	private int is_removed;
	private String stop_name;
	private int route_id;
	private GpsCoordinates gps_coordinates;
	private String effective_from_datetime;
	private int pickup_stop_sequence_number;
	private String pickup_travel_time_from_previous_stop_in_minutes;
	private int drop_stop_sequence_number;
	private Object drop_travel_time_from_previous_stop_in_minutes;
	private int id;

	public PickUpRouteStopsItem() {
		// Initialize GpsCoordinates in the constructor
		this.gps_coordinates = new GpsCoordinates();
	}
	protected PickUpRouteStopsItem(Parcel in) {
		stop_id = in.readInt();
		is_removed = in.readInt();
		stop_name = in.readString();
		route_id = in.readInt();
		effective_from_datetime = in.readString();
		pickup_stop_sequence_number = in.readInt();
		pickup_travel_time_from_previous_stop_in_minutes = in.readString();
		drop_stop_sequence_number = in.readInt();
		id = in.readInt();
		gps_coordinates = in.readParcelable(GpsCoordinates.class.getClassLoader());
	}

	public static final Parcelable.Creator<PickUpRouteStopsItem> CREATOR = new Parcelable.Creator<PickUpRouteStopsItem>() {
		@Override
		public PickUpRouteStopsItem createFromParcel(Parcel in) {
			return new PickUpRouteStopsItem(in);
		}

		@Override
		public PickUpRouteStopsItem[] newArray(int size) {
			return new PickUpRouteStopsItem[size];
		}
	};

	public int getStop_id() {
		return stop_id;
	}

	public void setStop_id(int stop_id) {
		this.stop_id = stop_id;
	}

	public int getIs_removed() {
		return is_removed;
	}

	public void setIs_removed(int is_removed) {
		this.is_removed = is_removed;
	}

	public String getStop_name() {
		return stop_name;
	}

	public void setStop_name(String stop_name) {
		this.stop_name = stop_name;
	}

	public int getRoute_id() {
		return route_id;
	}

	public void setRoute_id(int route_id) {
		this.route_id = route_id;
	}

	public GpsCoordinates getGps_coordinates() {
		return gps_coordinates;
	}

	public void setGps_coordinates(GpsCoordinates gps_coordinates) {
		this.gps_coordinates = gps_coordinates;
		Log.d("MapViewCurrentTracking", "Set GPS Coordinates: " + gps_coordinates);
	}

	public String getEffective_from_datetime() {
		return effective_from_datetime;
	}

	public void setEffective_from_datetime(String effective_from_datetime) {
		this.effective_from_datetime = effective_from_datetime;
	}

	public int getPickup_stop_sequence_number() {
		return pickup_stop_sequence_number;
	}

	public void setPickup_stop_sequence_number(int pickup_stop_sequence_number) {
		this.pickup_stop_sequence_number = pickup_stop_sequence_number;
	}

	public String getPickup_travel_time_from_previous_stop_in_minutes() {
		return pickup_travel_time_from_previous_stop_in_minutes;
	}

	public void setPickup_travel_time_from_previous_stop_in_minutes(String pickup_travel_time_from_previous_stop_in_minutes) {
		this.pickup_travel_time_from_previous_stop_in_minutes = pickup_travel_time_from_previous_stop_in_minutes;
	}

	public int getDrop_stop_sequence_number() {
		return drop_stop_sequence_number;
	}

	public void setDrop_stop_sequence_number(int drop_stop_sequence_number) {
		this.drop_stop_sequence_number = drop_stop_sequence_number;
	}

	public Object getDrop_travel_time_from_previous_stop_in_minutes() {
		return drop_travel_time_from_previous_stop_in_minutes;
	}

	public void setDrop_travel_time_from_previous_stop_in_minutes(Object drop_travel_time_from_previous_stop_in_minutes) {
		this.drop_travel_time_from_previous_stop_in_minutes = drop_travel_time_from_previous_stop_in_minutes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(stop_id);
		dest.writeInt(is_removed);
		dest.writeString(stop_name);
		dest.writeInt(route_id);
		dest.writeString(effective_from_datetime);
		dest.writeInt(pickup_stop_sequence_number);
		dest.writeString(pickup_travel_time_from_previous_stop_in_minutes);
		dest.writeInt(drop_stop_sequence_number);
		dest.writeInt(id);
		dest.writeParcelable(gps_coordinates, flags);
	}
}
