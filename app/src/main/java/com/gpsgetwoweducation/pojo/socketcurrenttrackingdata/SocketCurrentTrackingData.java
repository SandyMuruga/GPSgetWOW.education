package com.gpsgetwoweducation.pojo.socketcurrenttrackingdata;

import java.util.List;

public class SocketCurrentTrackingData {
    private String gps_tracking_event_id;
    private List<SocketGpsTrackingData> tracking_gps_data;
    private boolean location_reached;

    public String getGps_tracking_event_id() {
        return gps_tracking_event_id;
    }

    public void setGps_tracking_event_id(String gps_tracking_event_id) {
        this.gps_tracking_event_id = gps_tracking_event_id;
    }

    public List<SocketGpsTrackingData> getTracking_gps_data() {
        return tracking_gps_data;
    }

    public void setTracking_gps_data(List<SocketGpsTrackingData> tracking_gps_data) {
        this.tracking_gps_data = tracking_gps_data;
    }

    public boolean isLocation_reached() {
        return location_reached;
    }

    public void setLocation_reached(boolean location_reached) {
        this.location_reached = location_reached;
    }
}
