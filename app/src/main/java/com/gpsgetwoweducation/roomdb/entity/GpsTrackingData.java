package com.gpsgetwoweducation.roomdb.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_gps_tracking")
public class GpsTrackingData implements Parcelable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @ColumnInfo(name = "country_code")
    private String mCountryCode;

    @ColumnInfo(name = "customer_id")
    private String mCustomerId;

    @ColumnInfo(name = "login_user_id")
    private String mLoginUserId;

    @ColumnInfo(name = "latest_datetime")
    private String mLatestDatetime;

    @ColumnInfo(name = "latitude")
    private String mLatitude;

    @ColumnInfo(name = "longitude")
    private String mLongitude;

    @ColumnInfo(name = "gps_tracking_event_id")
    private String mGpsTrackingEventId;

    @Ignore
    public GpsTrackingData() {
    }

    public GpsTrackingData(Integer mId, String mCountryCode, String mCustomerId, String mLoginUserId, String mLatestDatetime, String mLatitude, String mLongitude, String mGpsTrackingEventId) {
        this.mId = mId;
        this.mCountryCode = mCountryCode;
        this.mCustomerId = mCustomerId;
        this.mLoginUserId = mLoginUserId;
        this.mLatestDatetime = mLatestDatetime;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mGpsTrackingEventId = mGpsTrackingEventId;
    }

    protected GpsTrackingData(Parcel in) {
        mId = in.readInt();
        mCountryCode = in.readString();
        mCustomerId = in.readString();
        mLoginUserId = in.readString();
        mLatestDatetime = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
        mGpsTrackingEventId = in.readString();
    }

    public static final Creator<GpsTrackingData> CREATOR = new Creator<GpsTrackingData>() {
        @Override
        public GpsTrackingData createFromParcel(Parcel in) {
            return new GpsTrackingData(in);
        }

        @Override
        public GpsTrackingData[] newArray(int size) {
            return new GpsTrackingData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mCountryCode);
        parcel.writeString(mCustomerId);
        parcel.writeString(mLoginUserId);
        parcel.writeString(mLatestDatetime);
        parcel.writeString(mLatitude);
        parcel.writeString(mLongitude);
        parcel.writeString(mGpsTrackingEventId);
    }

    public Integer getMId() {
        return mId;
    }

    public void setMId(Integer mId) {
        this.mId = mId;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String mCountryCode) {
        this.mCountryCode = mCountryCode;
    }

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String mCustomerId) {
        this.mCustomerId = mCustomerId;
    }

    public String getLoginUserId() {
        return mLoginUserId;
    }

    public void setLoginUserId(String mLoginUserId) {
        this.mLoginUserId = mLoginUserId;
    }

    public String getLatestDatetime() {
        return mLatestDatetime;
    }

    public void setLatestDatetime(String mLatestDatetime) {
        this.mLatestDatetime = mLatestDatetime;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }
    public void setLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getGpsTrackingEventId() {
        return mGpsTrackingEventId;
    }

    public void setGpsTrackingEventId(String mGpsTrackingEventId) {
        this.mGpsTrackingEventId = mGpsTrackingEventId;
    }
}
