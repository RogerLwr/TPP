package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class SearchHistoryModel {
	@SerializedName("id")
	private long id;
	@SerializedName("name")
	private String name;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "SearchHistoryModel{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
