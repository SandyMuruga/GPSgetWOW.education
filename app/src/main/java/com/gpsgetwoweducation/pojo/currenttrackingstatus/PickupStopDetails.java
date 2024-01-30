package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PickupStopDetails implements Parcelable {
	private String pickup_total_travel_time;
	private List<PickUpRouteStopsItem> pickUpRouteStops;

	protected PickupStopDetails(Parcel in) {
		pickup_total_travel_time = in.readString();
		pickUpRouteStops = in.createTypedArrayList(PickUpRouteStopsItem.CREATOR);
	}

	public static final Creator<PickupStopDetails> CREATOR = new Creator<PickupStopDetails>() {
		@Override
		public PickupStopDetails createFromParcel(Parcel in) {
			return new PickupStopDetails(in);
		}

		@Override
		public PickupStopDetails[] newArray(int size) {
			return new PickupStopDetails[size];
		}
	};

	public String getPickup_total_travel_time() {
		return pickup_total_travel_time;
	}

	public void setPickup_total_travel_time(String pickup_total_travel_time) {
		this.pickup_total_travel_time = pickup_total_travel_time;
	}

	public List<PickUpRouteStopsItem> getPickUpRouteStops() {
		return pickUpRouteStops;
	}

	public void setPickUpRouteStops(List<PickUpRouteStopsItem> pickUpRouteStops) {
		this.pickUpRouteStops = pickUpRouteStops;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeString(pickup_total_travel_time);
		dest.writeTypedList(pickUpRouteStops);
	}
}