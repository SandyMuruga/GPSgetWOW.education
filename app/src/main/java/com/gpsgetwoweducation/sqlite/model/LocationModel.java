package com.gpsgetwoweducation.sqlite.model;

public class LocationModel {

    public static final String TABLE = "location";
    // Labels Table Columns names
    public static final String KEY_country_code = "country_code";
    public static final String KEY_customer_id = "customer_id";
    public static final String KEY_user_ide = "user_id";
    public static final String KEY_latest_datetime = "latest_datetime";
    public static final String KEY_latitude = "latitude";
    public static final String KEY_longitude = "longitude";

    private String country_code = "",
            customer_id = "",
            user_id = "",
            latest_datetime = "",
            latitude = "",
            longitude = "";

    public LocationModel() {
    }

    public LocationModel(String country_code, String customer_id, String user_id, String latest_datetime, String latitude, String longitude) {
        this.country_code = country_code;
        this.customer_id = customer_id;
        this.user_id = user_id;
        this.latest_datetime = latest_datetime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLatest_datetime() {
        return latest_datetime;
    }

    public void setLatest_datetime(String latest_datetime) {
        this.latest_datetime = latest_datetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
