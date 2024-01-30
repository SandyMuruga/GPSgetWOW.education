package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class DropOffStopDetails implements Parcelable{
	private List<DropOffRouteStopsItem> dropOffRouteStops;
	private String drop_off_total_travel_time;

	protected DropOffStopDetails(Parcel in) {
	}

	public static final Creator<DropOffStopDetails> CREATOR = new Creator<DropOffStopDetails>() {
		@Override
		public DropOffStopDetails createFromParcel(Parcel in) {
			return new DropOffStopDetails(in);
		}

		@Override
		public DropOffStopDetails[] newArray(int size) {
			return new DropOffStopDetails[size];
		}
	};

	public List<DropOffRouteStopsItem> getDropOffRouteStops(){
		return dropOffRouteStops;
	}

	public void setDropOffRouteStops(List<DropOffRouteStopsItem> dropOffRouteStops) {
		this.dropOffRouteStops = dropOffRouteStops;
	}

	public String getDrop_off_total_travel_time() {
		return drop_off_total_travel_time;
	}

	public void setDrop_off_total_travel_time(String drop_off_total_travel_time) {
		this.drop_off_total_travel_time = drop_off_total_travel_time;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
	}
}