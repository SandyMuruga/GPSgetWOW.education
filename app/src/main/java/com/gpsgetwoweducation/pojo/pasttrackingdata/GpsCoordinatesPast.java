package com.gpsgetwoweducation.pojo.pasttrackingdata;

import android.os.Parcel;
import android.os.Parcelable;

public class GpsCoordinatesPast implements Parcelable {
    private double x;
    private double y;

    public GpsCoordinatesPast() {
        // Default constructor
    }

    protected GpsCoordinatesPast(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
    }

    public static final Creator<GpsCoordinatesPast> CREATOR = new Creator<GpsCoordinatesPast>() {
        @Override
        public GpsCoordinatesPast createFromParcel(Parcel in) {
            return new GpsCoordinatesPast(in);
        }

        @Override
        public GpsCoordinatesPast[] newArray(int size) {
            return new GpsCoordinatesPast[size];
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }
}
