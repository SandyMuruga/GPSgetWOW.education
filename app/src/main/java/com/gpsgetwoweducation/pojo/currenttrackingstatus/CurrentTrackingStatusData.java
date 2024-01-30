package com.gpsgetwoweducation.pojo.currenttrackingstatus;

public class CurrentTrackingStatusData {
    private int vehicle_id;
    private int route_id;
    private int trip_id;
    private String trip_scheduled_start_datetime;
    private int is_pickup_trip;
    private int tracking_device_type;
    private int tracking_device_id_or_tracking_user_id;
    private int user_id_of_driver;
    private int user_id_of_attendant;
    private int gps_tracking_event_id;
    private int global_tracking_event_id;
    private PickupStopDetails pickup_stop_details;
    private DropOffStopDetails drop_off_stop_details;
    private String trip_scheduled_end_datetime;
    private String tracking_event_initiated_by_app_name;
    private boolean is_disabled_stop_tracking;

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_scheduled_start_datetime() {
        return trip_scheduled_start_datetime;
    }

    public void setTrip_scheduled_start_datetime(String trip_scheduled_start_datetime) {
        this.trip_scheduled_start_datetime = trip_scheduled_start_datetime;
    }

    public int getIs_pickup_trip() {
        return is_pickup_trip;
    }

    public void setIs_pickup_trip(int is_pickup_trip) {
        this.is_pickup_trip = is_pickup_trip;
    }

    public int getTracking_device_type() {
        return tracking_device_type;
    }

    public void setTracking_device_type(int tracking_device_type) {
        this.tracking_device_type = tracking_device_type;
    }

    public int getTracking_device_id_or_tracking_user_id() {
        return tracking_device_id_or_tracking_user_id;
    }

    public void setTracking_device_id_or_tracking_user_id(int tracking_device_id_or_tracking_user_id) {
        this.tracking_device_id_or_tracking_user_id = tracking_device_id_or_tracking_user_id;
    }

    public int getUser_id_of_driver() {
        return user_id_of_driver;
    }

    public void setUser_id_of_driver(int user_id_of_driver) {
        this.user_id_of_driver = user_id_of_driver;
    }

    public int getUser_id_of_attendant() {
        return user_id_of_attendant;
    }

    public void setUser_id_of_attendant(int user_id_of_attendant) {
        this.user_id_of_attendant = user_id_of_attendant;
    }

    public int getGps_tracking_event_id() {
        return gps_tracking_event_id;
    }

    public void setGps_tracking_event_id(int gps_tracking_event_id) {
        this.gps_tracking_event_id = gps_tracking_event_id;
    }

    public int getGlobal_tracking_event_id() {
        return global_tracking_event_id;
    }

    public void setGlobal_tracking_event_id(int global_tracking_event_id) {
        this.global_tracking_event_id = global_tracking_event_id;
    }

    public PickupStopDetails getPickup_stop_details() {
        return pickup_stop_details;
    }

    public void setPickup_stop_details(PickupStopDetails pickup_stop_details) {
        this.pickup_stop_details = pickup_stop_details;
    }

    public DropOffStopDetails getDrop_off_stop_details() {
        return drop_off_stop_details;
    }

    public void setDrop_off_stop_details(DropOffStopDetails drop_off_stop_details) {
        this.drop_off_stop_details = drop_off_stop_details;
    }

    public String getTrip_scheduled_end_datetime() {
        return trip_scheduled_end_datetime;
    }

    public void setTrip_scheduled_end_datetime(String trip_scheduled_end_datetime) {
        this.trip_scheduled_end_datetime = trip_scheduled_end_datetime;
    }

    public String getTracking_event_initiated_by_app_name() {
        return tracking_event_initiated_by_app_name;
    }

    public void setTracking_event_initiated_by_app_name(String tracking_event_initiated_by_app_name) {
        this.tracking_event_initiated_by_app_name = tracking_event_initiated_by_app_name;
    }

    public boolean isIs_disabled_stop_tracking() {
        return is_disabled_stop_tracking;
    }

    public void setIs_disabled_stop_tracking(boolean is_disabled_stop_tracking) {
        this.is_disabled_stop_tracking = is_disabled_stop_tracking;
    }
}
