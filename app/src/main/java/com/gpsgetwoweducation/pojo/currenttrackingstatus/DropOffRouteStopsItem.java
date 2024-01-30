package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class DropOffRouteStopsItem implements Parcelable {
	private int stopId;
	private int isRemoved;
	private String stop_name;
	private int routeId;
	private GpsCoordinates gps_coordinates;
	private String effectiveFromDatetime;
	private int pickupStopSequenceNumber;
	private int pickupTravelTimeFromPreviousStopInMinutes;
	private int dropStopSequenceNumber;
	private Object dropTravelTimeFromPreviousStopInMinutes;
	private int id;

	public DropOffRouteStopsItem() {
		// Initialize GpsCoordinates in the constructor
		this.gps_coordinates = new GpsCoordinates();
	}

	protected DropOffRouteStopsItem(Parcel in) {
		stopId = in.readInt();
		isRemoved = in.readInt();
		stop_name = in.readString();
		routeId = in.readInt();
		effectiveFromDatetime = in.readString();
		pickupStopSequenceNumber = in.readInt();
		pickupTravelTimeFromPreviousStopInMinutes = in.readInt();
		dropStopSequenceNumber = in.readInt();
		id = in.readInt();
		gps_coordinates = in.readParcelable(GpsCoordinates.class.getClassLoader());
	}

	public static final Parcelable.Creator<DropOffRouteStopsItem> CREATOR = new Parcelable.Creator<DropOffRouteStopsItem>() {
		@Override
		public DropOffRouteStopsItem createFromParcel(Parcel in) {
			return new DropOffRouteStopsItem(in);
		}

		@Override
		public DropOffRouteStopsItem[] newArray(int size) {
			return new DropOffRouteStopsItem[size];
		}
	};

	public String getEffectiveFromDatetime(){
		return effectiveFromDatetime;
	}

	public int getPickupTravelTimeFromPreviousStopInMinutes(){
		return pickupTravelTimeFromPreviousStopInMinutes;
	}

	public int getDropStopSequenceNumber(){
		return dropStopSequenceNumber;
	}

	public int getRouteId(){
		return routeId;
	}

	public int getStopId(){
		return stopId;
	}

	public int getIsRemoved(){
		return isRemoved;
	}

	public int getId(){
		return id;
	}

	public int getPickupStopSequenceNumber(){
		return pickupStopSequenceNumber;
	}

	public GpsCoordinates getGps_coordinates() {
		return gps_coordinates;
	}

	public void setGps_coordinates(GpsCoordinates gps_coordinates) {
		this.gps_coordinates = gps_coordinates;
	}

	public Object getDropTravelTimeFromPreviousStopInMinutes(){
		return dropTravelTimeFromPreviousStopInMinutes;
	}

	public String getStopName(){
		return stop_name;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(stopId);
		dest.writeInt(isRemoved);
		dest.writeString(stop_name);
		dest.writeInt(routeId);
		dest.writeString(effectiveFromDatetime);
		dest.writeInt(pickupStopSequenceNumber);
		dest.writeInt(pickupTravelTimeFromPreviousStopInMinutes);
		dest.writeInt(dropStopSequenceNumber);
		dest.writeInt(id);
		dest.writeParcelable(gps_coordinates, flags);
	}
}
