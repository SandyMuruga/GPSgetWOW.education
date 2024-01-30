package com.gpsgetwoweducation.pojo.stoplivecoordinates;

public class StopLiveCoordinatesData {
    private String country_code;
    private String customer_id;
    private String login_user_id;
    private String latitude;
    private String longitude;
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
