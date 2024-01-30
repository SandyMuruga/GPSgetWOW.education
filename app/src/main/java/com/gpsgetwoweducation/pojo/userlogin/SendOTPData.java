package com.gpsgetwoweducation.pojo.userlogin;

public class SendOTPData {
    private String mobile_country_code;
    private String mobile_no;
    private String app_name;

    public String getMobile_country_code() {
        return mobile_country_code;
    }
    public void setMobile_country_code(String mobile_country_code) {
        this.mobile_country_code = mobile_country_code;
    }
    public String getMobile_no() {
        return mobile_no;
    }
    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}
