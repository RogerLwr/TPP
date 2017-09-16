package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SellerModel implements Serializable {
	@SerializedName("user_id")
	private long id;
	@SerializedName("login_name")
	private String loginName;
	@SerializedName("display_name")
	private String displayName;
	@SerializedName("phone")
	private String phone;
	
	@SerializedName("address")
	private String address;
	
	@SerializedName("sale_name")
	private String saleName;
	
	@SerializedName("score")
	private double rating;//max is 10;
	
	private double minAmount;
	
	private double freight;

	private double money;// 满减优惠的钱

	private double startingPrice;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	private String remark;

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public double getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}
	public double getFreight() {
		return freight;
	}
	public void setFreight(double freight) {
		this.freight = freight;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getSaleName() {
		return saleName;
	}
	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(double startingPrice) {
		this.startingPrice = startingPrice;
	}

	@Override
	public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o instanceof SellerModel){
            return id == ((SellerModel)o).id;
        }
		return super.equals(o);
	}

    @Override
    public int hashCode() {
        return (int) id;
    }

	@Override
	public String toString() {
		return "SellerModel{" +
				"id=" + id +
				", loginName='" + loginName + '\'' +
				", displayName='" + displayName + '\'' +
				", phone='" + phone + '\'' +
				", address='" + address + '\'' +
				", saleName='" + saleName + '\'' +
				", rating=" + rating +
				", minAmount=" + minAmount +
				", freight=" + freight +
				", money=" + money +
				", startingPrice=" + startingPrice +
				", remark='" + remark + '\'' +
				'}';
	}
}
