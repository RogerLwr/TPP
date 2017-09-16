package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class MarketModel {
	@SerializedName("id")
	private int id;
	@SerializedName("name")
	private String name;
	@SerializedName("address")
	private String address;
	@SerializedName("area_id")
	private int areaId;
	@SerializedName("weight")
	private int weight;
	@SerializedName("lat")
	private String lat;
	@SerializedName("lng")
	private String lng;
	@SerializedName("distance")
	private int distance;
	
	private boolean isDefault;
	
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	@Override
	public String toString() {
		return "MarketModel [id=" + id + ", name=" + name + ", address="
				+ address + ", areaId=" + areaId + ", weight=" + weight
				+ ", lat=" + lat + ", lng=" + lng + ", distance=" + distance
				+ ", isDefault=" + isDefault + "]";
	}
	
}
