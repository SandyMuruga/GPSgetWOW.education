package com.gpsgetwoweducation.roomdb.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_gps_response_data")
public class GpsResponseData {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Integer mId;

    @ColumnInfo(name = "x")
    private double mX;

    @ColumnInfo(name = "y")
    private double mY;

    @ColumnInfo(name = "datetime")
    private String mDateTime;

    public GpsResponseData(double x, double y, String dateTime) {
        this.mX = x;
        this.mY = y;
        this.mDateTime = dateTime;
    }

    public GpsResponseData() {
    }

    @NonNull
    public Integer getMId() {
        return mId;
    }

    public void setMId(@NonNull Integer mId) {
        this.mId = mId;
    }

    public double getMX() {
        return mX;
    }

    public void setMX(double mX) {
        this.mX = mX;
    }

    public double getMY() {
        return mY;
    }

    public void setMY(double mY) {
        this.mY = mY;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }
}


