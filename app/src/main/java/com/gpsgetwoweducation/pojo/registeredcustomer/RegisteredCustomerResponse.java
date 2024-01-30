package com.gpsgetwoweducation.pojo.registeredcustomer;

import java.util.List;

public class RegisteredCustomerResponse {

    private boolean status;
    private String message;
    private List<RegistredBusinessData> data;

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

    public List<RegistredBusinessData> getData() {
        return data;
    }

    public void setData(List<RegistredBusinessData> data) {
        this.data = data;
    }


}
