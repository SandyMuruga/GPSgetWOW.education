package com.gpsgetwoweducation.pojo.insertgpstrackingdata;

import java.util.List;

public class InsertGpsTrackingData {
    private String app_login_user_id;
    private String app_type;
    private String latest_socket_id;
    private String latest_socket_datetime;
    private List<InsertLastlocatioDateTimeData> testing_live_location_gps_coordinates;

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

    public String getLatest_socket_id() {
        return latest_socket_id;
    }

    public void setLatest_socket_id(String latest_socket_id) {
        this.latest_socket_id = latest_socket_id;
    }

    public String getLatest_socket_datetime() {
        return latest_socket_datetime;
    }

    public void setLatest_socket_datetime(String latest_socket_datetime) {
        this.latest_socket_datetime = latest_socket_datetime;
    }

    public List<InsertLastlocatioDateTimeData> getTesting_live_location_gps_coordinates() {
        return testing_live_location_gps_coordinates;
    }

    public void setTesting_live_location_gps_coordinates(List<InsertLastlocatioDateTimeData> testing_live_location_gps_coordinates) {
        this.testing_live_location_gps_coordinates = testing_live_location_gps_coordinates;
    }
}
