package com.tianpingpai.buyer.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;

public class ActivityModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1657896497201813594L;
	@SerializedName("id")
	private long id;
	@SerializedName("name")
	private String name;
	@SerializedName("introduction")
	private String introduction;
	@SerializedName("rule")
	private String rules;
	
	@SerializedName("total")
	private int total;
	
	@SerializedName("accessNumber")
	private double limitNumber;
	
	@SerializedName("unit")
	private String unit;
	
	@SerializedName("price")
	private double price;
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	@SerializedName("endTime")
	private String endTime;
	
	@SerializedName("currentTime")
	private String currentTime;
	
	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public double getLimitNumber() {
		return limitNumber;
	}
	public void setLimitNumber(double limitNumber) {
		this.limitNumber = limitNumber;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public ArrayList<String> getImages() {
		return images;
	}
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	@SerializedName("images")
	private ArrayList<String> images;
	
	@Override
	public String toString() {
		return "ActivityModel [id=" + id + ", name=" + name + ", introduction="
				+ introduction + ", rules=" + rules + "product" + product + "]";
	}
	
	@SerializedName("saler")
	private SellerModel seller;
	
	public SellerModel getSeller() {
		return seller;
	}
	public void setSeller(SellerModel seller) {
		this.seller = seller;
	}
	
	@SerializedName("product")
	private String productName;

	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

	@SerializedName("productExt")
	private ProductModel product;
//	
	public ProductModel getProduct() {
		return product;
	}
	public void setProduct(ProductModel product) {
		this.product = product;
	}
	
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
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
}
