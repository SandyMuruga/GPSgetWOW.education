package com.gpsgetwoweducation.pojo.registredmobilenumber;

public class RegisteredMobileNumberResponse {
    private boolean status;
    private String message;
    private RegisteredLoginData data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RegisteredLoginData getData() {
        return data;
    }

    public void setData(RegisteredLoginData data) {
        this.data = data;
    }
}
