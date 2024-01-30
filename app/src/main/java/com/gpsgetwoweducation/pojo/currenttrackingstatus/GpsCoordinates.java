package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class GpsCoordinates implements Parcelable {
	private double x;
	private double y;

	public GpsCoordinates() {
		// Empty constructor required by Parcelable
	}

	protected GpsCoordinates(Parcel in) {
		x = in.readDouble();
		y = in.readDouble();
	}

	public static final Parcelable.Creator<GpsCoordinates> CREATOR = new Creator<GpsCoordinates>() {
		@Override
		public GpsCoordinates createFromParcel(Parcel in) {
			return new GpsCoordinates(in);
		}

		@Override
		public GpsCoordinates[] newArray(int size) {
			return new GpsCoordinates[size];
		}
	};

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
	}
}
