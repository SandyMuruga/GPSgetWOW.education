package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CurrentSocketTrackingDataResponse implements Parcelable {
    private double x;
    private double y;
    private String gps_mobile_app_test_datetime;

    public CurrentSocketTrackingDataResponse(double x, double y, String gps_mobile_app_test_datetime) {
        this.x = x;
        this.y = y;
        this.gps_mobile_app_test_datetime = gps_mobile_app_test_datetime;
    }
    protected CurrentSocketTrackingDataResponse(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        gps_mobile_app_test_datetime = in.readString();
    }

    public static final Creator<CurrentSocketTrackingDataResponse> CREATOR = new Creator<CurrentSocketTrackingDataResponse>() {
        @Override
        public CurrentSocketTrackingDataResponse createFromParcel(Parcel in) {
            return new CurrentSocketTrackingDataResponse(in);
        }

        @Override
        public CurrentSocketTrackingDataResponse[] newArray(int size) {
            return new CurrentSocketTrackingDataResponse[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeString(gps_mobile_app_test_datetime);
    }
}
