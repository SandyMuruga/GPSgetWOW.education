package com.gpsgetwoweducation.pojo.userlogin;

public class UserLoginResponse {
    private String message;
    private String statusCode;
    private LoginResponseData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginResponseData getData() {
        return data;
    }

    public void setData(LoginResponseData data) {
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int userApprovalStatus() {
        return Integer.parseInt(statusCode);
    }
}
