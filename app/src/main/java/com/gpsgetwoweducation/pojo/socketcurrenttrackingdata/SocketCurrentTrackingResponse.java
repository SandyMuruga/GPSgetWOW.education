package com.gpsgetwoweducation.pojo.socketcurrenttrackingdata;

public class SocketCurrentTrackingResponse {
    private boolean success;
    private String message;
    private SocketCurrentTrackingData insertedData;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SocketCurrentTrackingData getInsertedData() {
        return insertedData;
    }

    public void setInsertedData(SocketCurrentTrackingData insertedData) {
        this.insertedData = insertedData;
    }
}
