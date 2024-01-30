package com.gpsgetwoweducation.pojo.insertgpstrackingdata;

public class SendGpsDataBody {
    private String customer_id;
    private String country_code;
    private String time_zone_iana_string;
    private String app_login_user_id;
    private String app_type;
    private String latitude;
    private String longitude;
    private String last_tested_live_location_datetime;

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getTime_zone_iana_string() {
        return time_zone_iana_string;
    }

    public void setTime_zone_iana_string(String time_zone_iana_string) {
        this.time_zone_iana_string = time_zone_iana_string;
    }

    public String getApp_login_user_id() {
        return app_login_user_id;
    }

    public void setApp_login_user_id(String app_login_user_id) {
        this.app_login_user_id = app_login_user_id;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
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

    public String getLast_tested_live_location_datetime() {
        return last_tested_live_location_datetime;
    }

    public void setLast_tested_live_location_datetime(String last_tested_live_location_datetime) {
        this.last_tested_live_location_datetime = last_tested_live_location_datetime;
    }
}
