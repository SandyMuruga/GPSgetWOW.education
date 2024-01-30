package com.gpsgetwoweducation.pojo.pasttrackingdata;

import java.util.List;

public class PastTrackingDataResponse {
    private boolean status;
    private String message;
    private List<PastTrackingDatas> data;

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

    public List<PastTrackingDatas> getData() {
        return data;
    }

    public void setData(List<PastTrackingDatas> data) {
        this.data = data;
    }
}
