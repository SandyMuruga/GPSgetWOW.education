package com.gpsgetwoweducation.pojo.registredmobilenumber;

import java.io.Serializable;
import java.util.List;

public class RegisteredLoginData implements Serializable {
    private String customer_id;
    private String country_code;
    private boolean isMobileNumberRegisteredByMultipleUser;
    private List<RegisteredMobileNumberData> user_login_data;

    public List<RegisteredMobileNumberData> getUser_login_data() {
        return user_login_data;
    }

    public void setUser_login_data(List<RegisteredMobileNumberData> user_login_data) {
        this.user_login_data = user_login_data;
    }

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

    public boolean isMobileNumberRegisteredByMultipleUser() {
        return isMobileNumberRegisteredByMultipleUser;
    }

    public void setMobileNumberRegisteredByMultipleUser(boolean mobileNumberRegisteredByMultipleUser) {
        isMobileNumberRegisteredByMultipleUser = mobileNumberRegisteredByMultipleUser;
    }
}
