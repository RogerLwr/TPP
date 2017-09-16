package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class SimpleResult {
	@SerializedName("message")
	private String message;
	@SerializedName("status")
	private int status;
	
	public boolean isOK(){
		return status == 1;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
