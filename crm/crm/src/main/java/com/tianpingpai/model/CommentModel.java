package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;
@SuppressWarnings("unused")
public class CommentModel {
	@SerializedName("id")
	private long id;
	@SerializedName("customer_id")
	private int customerId;
	@SerializedName("marketer_id")
	private int marketerId;
	@SerializedName("marketer_name")
	private String marketerName;
	@SerializedName("content")
	private String content;
	@SerializedName("created_time")
	private String createdTime;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    @SerializedName("position")
    private String address;

	public double getLatitude() {
		double lat = 0;
		try {
			lat = Double.parseDouble(latitude);
		}catch (Exception e){

		}
		return lat;
	}

	public double getLongitude() {
        double value = 0;
        try{
            value = Double.parseDouble(longitude);
        }catch (Exception e){

        }
		return value;
	}

	public String getAddress() {
		return address;
	}

	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getMarketerId() {
		return marketerId;
	}
	public void setMarketerId(int marketerId) {
		this.marketerId = marketerId;
	}
	public String getMarketerName() {
		return marketerName;
	}
	public void setMarketerName(String marketerName) {
		this.marketerName = marketerName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
