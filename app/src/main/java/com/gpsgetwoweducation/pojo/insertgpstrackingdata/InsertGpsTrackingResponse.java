package com.gpsgetwoweducation.pojo.insertgpstrackingdata;

import java.util.List;

public class InsertGpsTrackingResponse {
    private boolean status;
    private String message;
    private List<InsertGpsTrackingData> data;

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

    public List<InsertGpsTrackingData> getData() {
        return data;
    }

    public void setData(List<InsertGpsTrackingData> data) {
        this.data = data;
    }
}
