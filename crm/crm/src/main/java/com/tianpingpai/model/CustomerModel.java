package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
//@SuppressWarnings("unused")
public class CustomerModel implements Serializable ,Cloneable{
	private static final long serialVersionUID = -5085499929043108560L;
	
	public static final int STATUS_NEED_APPROVE = 0;
	public static final int STATUS_APPROVED = 1;
	
	public static final int USER_TYPE_BUYER = 1;
	public static final int USER_TYPE_SELLER = 0;
	
	@SerializedName("id")
	private long id;

	@SerializedName("id")
	private int idInt;

	@SerializedName("sales")
	private int sales; // 日销售额id

	@SerializedName("purchase")
	private int purchase; //日采购额 id

	@SerializedName("categoryId")
	private int categoryId; // 业态类型id

	@SerializedName("seats")
	private int seats;  //座位数

	@SerializedName("region")
	private int region; //地区id

	@SerializedName("regionName")
	private String regionName;  // 地区名称

	@SerializedName("photo")
	private String imageUrl;

	public long getId() {
		if(id != 0){
			return id;
		}else{
			return idInt;
		}
	}
	public void setId(long id) {
		this.id = id;
	}
	@SerializedName("created_time")
	private String createdTime;
	
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	@SerializedName("user_id")
	private long userId;
	@SerializedName("login_name")
	private String loginName;
	@SerializedName("display_name")
	private String displayName;
	@SerializedName("password")
	private String password;
	@SerializedName("phone")
	private String phone;
	@SerializedName("role")
	private String role;
	@SerializedName("user_type")
	private int userType;
	@SerializedName("user_type")
	private String userTypeStr;
	@SerializedName("email")
	private String email;
	@SerializedName("sale_address")
	private String saleAddress;

    @SerializedName("address")
	private String address;
	
	@SerializedName("sale_name")
	private String saleName;
	
	@SerializedName("manage_name")
	private String marketerName;
	
	@SerializedName("market_id")
	private int marketId;

	@SerializedName("is_register")
	private String isRegister;

	@SerializedName("consignee")
	private String consignee;

	@SerializedName("grade")
	private int grade;

	public void setGrade(int grade){
		this.grade = grade;
	}
	public int getGrade(){
		return grade;
	}
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getConsignee() {
		return consignee;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getPurchase() {
		return purchase;
	}

	public void setPurchase(int purchase) {
		this.purchase = purchase;
	}

	public int getSales() {
		return sales;
	}

	public void setSales(int sales) {
		this.sales = sales;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	public String getRegionName() {
		return regionName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public int getMarketId() {
		return marketId;
	}
	public void setMarketId(int marketId) {
		this.marketId = marketId;
	}
	public String getSaleName() {
		return saleName;
	}
	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}
	public String getMarketerName() {
		return marketerName;
	}
	public void setMarketerName(String marketerName) {
		this.marketerName = marketerName;
	}
	@SerializedName("area_id")
	private int areaId;
	@SerializedName("status")
	private int status;

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

//	@SerializedName("receiveAddress")
//	private ArrayList<ReceiveAddress> receiveAddress;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSaleAddress() {
		return saleAddress;
	}
	public void setSaleAddress(String saleAddress) {
		this.saleAddress = saleAddress;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
//	public ArrayList<ReceiveAddress> getReceiveAddress() {
//		return receiveAddress;
//	}
//	public void setReceiveAddress(ArrayList<ReceiveAddress> receiveAddress) {
//		this.receiveAddress = receiveAddress;
//	}
	@Override
	public CustomerModel clone() throws CloneNotSupportedException {
		return (CustomerModel) super.clone();
	}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//	@Override
//	public String toString() {
//		return "CustomerModel{" +
//				"id=" + id +
//				", idInt=" + idInt +
//				", sales=" + sales +
//				", purchase=" + purchase +
//				", categoryId=" + categoryId +
//				", seats=" + seats +
//				", region=" + region +
//				", regionName='" + regionName + '\'' +
//				", imageUrl='" + imageUrl + '\'' +
//				", createdTime='" + createdTime + '\'' +
//				", userId=" + userId +
//				", loginName='" + loginName + '\'' +
//				", displayName='" + displayName + '\'' +
//				", password='" + password + '\'' +
//				", phone='" + phone + '\'' +
//				", role='" + role + '\'' +
//				", userType=" + userType +
//				", userTypeStr='" + userTypeStr + '\'' +
//				", email='" + email + '\'' +
//				", saleAddress='" + saleAddress + '\'' +
//				", address='" + address + '\'' +
//				", saleName='" + saleName + '\'' +
//				", marketerName='" + marketerName + '\'' +
//				", marketId=" + marketId +
//				", isRegister='" + isRegister + '\'' +
//				", consignee='" + consignee + '\'' +
//				", grade=" + grade +
//				", areaId=" + areaId +
//				", status=" + status +
//				", receiveAddress=" + receiveAddress +
//				'}';
//	}
}
