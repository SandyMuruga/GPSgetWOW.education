package com.gpsgetwoweducation.pojo.registeredcustomer;

public class RegistredBusinessData {
    private String registered_educational_institution_name;
    private String sub_domain_name;
    private String customer_id;
    private String educational_institution_location_id;
    private String map_location_display_name;
    private String address_line_1;
    private String address_line_2;
    private String city_district_county;
    private GpsCoordinatesData gps_coordinates;

    private String country_code;

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getRegistered_educational_institution_name() {
        return registered_educational_institution_name;
    }

    public void setRegistered_educational_institution_name(String registered_educational_institution_name) {
        this.registered_educational_institution_name = registered_educational_institution_name;
    }

    public String getSub_domain_name() {
        return sub_domain_name;
    }

    public void setSub_domain_name(String sub_domain_name) {
        this.sub_domain_name = sub_domain_name;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getEducational_institution_location_id() {
        return educational_institution_location_id;
    }

    public void setEducational_institution_location_id(String educational_institution_location_id) {
        this.educational_institution_location_id = educational_institution_location_id;
    }

    public String getMap_location_display_name() {
        return map_location_display_name;
    }

    public void setMap_location_display_name(String map_location_display_name) {
        this.map_location_display_name = map_location_display_name;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getCity_district_county() {
        return city_district_county;
    }

    public void setCity_district_county(String city_district_county) {
        this.city_district_county = city_district_county;
    }

    public GpsCoordinatesData getGps_coordinates() {
        return gps_coordinates;
    }

    public void setGps_coordinates(GpsCoordinatesData gps_coordinates) {
        this.gps_coordinates = gps_coordinates;
    }
}
