package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class SaleNumberModel {
	@SerializedName("orderMore")
	private String more;
	
	@SerializedName("orderToday")
	private String today;

	@SerializedName("orderWeek")
	private String week;

	@SerializedName("feeAll")
	private String feeAll;

	@SerializedName("feeToday")
	private String feeToday;

	@SerializedName("feeWeek")
	private String feeWeek;

	@SerializedName("feeMonth")
	private String feeMonth;

	@SerializedName("orderAll")
	private String all;


	public String getMore() {
		return more;
	}

//	public void setMore(String more) {
//		this.more = more;
//	}

	public String getToday() {
		return today;
	}

//	public void setToday(String today) {
//		this.today = today;
//	}

	public String getWeek() {
		return week;
	}

	public String getFeeAll() {
		return feeAll;
	}

	public String getFeeToday() {
		return feeToday;
	}

	public String getFeeWeek() {
		return feeWeek;
	}

	public String getFeeMonth() {
		return feeMonth;
	}

	public String getAll() {
		return all;
	}

//	public void setWeek(String week) {
//		this.week = week;
//	}
	
	
}
