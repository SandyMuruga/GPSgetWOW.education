package com.gpsgetwoweducation.pojo.stoplivecoordinates;

public class StopTrackingDataResponse {
    private boolean status;
    private String message;
    private StopResponseData data;

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

    public StopResponseData getData() {
        return data;
    }

    public void setData(StopResponseData data) {
        this.data = data;
    }
}
