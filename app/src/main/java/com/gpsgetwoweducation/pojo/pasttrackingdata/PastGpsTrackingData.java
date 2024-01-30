package com.gpsgetwoweducation.pojo.pasttrackingdata;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class PastGpsTrackingData implements Parcelable {
    private double x;
    private double y;
    private String gps_mobile_app_test_datetime;

    protected PastGpsTrackingData(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        gps_mobile_app_test_datetime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeString(gps_mobile_app_test_datetime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PastGpsTrackingData> CREATOR = new Creator<PastGpsTrackingData>() {
        @Override
        public PastGpsTrackingData createFromParcel(Parcel in) {
            return new PastGpsTrackingData(in);
        }

        @Override
        public PastGpsTrackingData[] newArray(int size) {
            return new PastGpsTrackingData[size];
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

    public String getGps_mobile_app_test_datetime() {
        return gps_mobile_app_test_datetime;
    }

    public void setGps_mobile_app_test_datetime(String gps_mobile_app_test_datetime) {
        this.gps_mobile_app_test_datetime = gps_mobile_app_test_datetime;
    }
}
