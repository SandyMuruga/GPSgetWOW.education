package com.gpsgetwoweducation.pojo.socketcurrenttrackingdata;

public class SendLiveCoordinatesData {
    private String country_code;
    private String customer_id;
    private String login_user_id;
    private double latitude;
    private double longitude;
    private double location_latitude;
    private double location_longitude;
    private String latest_datetime;
    private String gps_tracking_event_id;

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

    public String getLogin_user_id() {
        return login_user_id;
    }

    public void setLogin_user_id(String login_user_id) {
        this.login_user_id = login_user_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }

    public String getLatest_datetime() {
        return latest_datetime;
    }

    public void setLatest_datetime(String latest_datetime) {
        this.latest_datetime = latest_datetime;
    }

    public String getGps_tracking_event_id() {
        return gps_tracking_event_id;
    }

    public void setGps_tracking_event_id(String gps_tracking_event_id) {
        this.gps_tracking_event_id = gps_tracking_event_id;
    }
}
