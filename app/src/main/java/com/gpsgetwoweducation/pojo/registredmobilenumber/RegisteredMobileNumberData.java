package com.gpsgetwoweducation.pojo.registredmobilenumber;

import java.io.Serializable;

public class RegisteredMobileNumberData implements Serializable {
    private String registered_mobile_country_code;
    private String registered_mobile_no;
    private String user_id;
    private String user_registration_login_approval_status;
    private String number_of_failed_attempts_for_the_day;
    private String first_name;
    private String last_name;

    private boolean selected;
    public RegisteredMobileNumberData() {
        selected = false;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRegistered_mobile_country_code() {
        return registered_mobile_country_code;
    }

    public void setRegistered_mobile_country_code(String registered_mobile_country_code) {
        this.registered_mobile_country_code = registered_mobile_country_code;
    }

    public String getRegistered_mobile_no() {
        return registered_mobile_no;
    }

    public void setRegistered_mobile_no(String registered_mobile_no) {
        this.registered_mobile_no = registered_mobile_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_registration_login_approval_status() {
        return user_registration_login_approval_status;
    }

    public void setUser_registration_login_approval_status(String user_registration_login_approval_status) {
        this.user_registration_login_approval_status = user_registration_login_approval_status;
    }

    public String getNumber_of_failed_attempts_for_the_day() {
        return number_of_failed_attempts_for_the_day;
    }

    public void setNumber_of_failed_attempts_for_the_day(String number_of_failed_attempts_for_the_day) {
        this.number_of_failed_attempts_for_the_day = number_of_failed_attempts_for_the_day;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
