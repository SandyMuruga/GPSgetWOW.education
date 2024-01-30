package com.gpsgetwoweducation.pojo.currenttrackingstatus;

import java.util.List;

public class CurrentTrackingStatusResponse {
	private boolean status;
	private String message;
	private List<CurrentTrackingStatusData> data;

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

	public List<CurrentTrackingStatusData> getData() {
		return data;
	}

	public void setData(List<CurrentTrackingStatusData> data) {
		this.data = data;
	}
}