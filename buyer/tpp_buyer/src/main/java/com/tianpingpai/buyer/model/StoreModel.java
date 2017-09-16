package com.tianpingpai.buyer.model;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.CommentModel;

import java.io.Serializable;
import java.util.ArrayList;

public class StoreModel {
	
	public static class Notification implements Serializable{
		private static final long serialVersionUID = 1L;
		private String content;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	
	@SerializedName("shop_id")
	private int id;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}

	@SerializedName("shop_name")
	private String name;
	@SerializedName("phone")
	private String phone;
	@SerializedName("shop_type")
	private int type;
	@SerializedName("shop_addr")
	private String address;
	@SerializedName("favorite_num")
	private int favoriteNumer;
	@SerializedName("good_comment")
	private int goodCommentNumber;
	@SerializedName("order_num")
	private int orderNumber;
	@SerializedName("score")
	private double rating;
	@SerializedName("shop_desc")
	private String shopDesc;
	@SerializedName("notifications")
	private ArrayList<Notification> notifications;
	@SerializedName("minAmount")
	private int minAmount;
	@SerializedName("freight")
	private int freight;

	@SerializedName("startingPrice")
	private int startingPrice;
	@SerializedName("comment_num")
	private int commentNumber;
	@SerializedName("shop_img")
	private String image;
	@SerializedName("category_desc")
	private String categoryDesc;
	@SerializedName("quality_desc")
	private String qualityDesc;
	@SerializedName("quality_info")
	private String qualityInfo;
	@SerializedName("response_desc")
	private String responseDesc;
	@SerializedName("response_info")
	private String responseInfo;//?
	@SerializedName("license")
	private String lisence;
	
	@SerializedName("include_shops")
	private ArrayList<StoreModel> subStores;

	public ArrayList<CommentModel> getRecentComments() {
		return recentComments;
	}

	public void setRecentComments(ArrayList<CommentModel> recentComments) {
		this.recentComments = recentComments;
	}

	@SerializedName("recent_comment")
	private ArrayList<CommentModel> recentComments;
	@SerializedName("promotions")
	private ArrayList<Promotion> promotions;

	public ArrayList<Promotion> getPromotions() {
		return promotions;
	}

	public void setPromotions(ArrayList<Promotion> promotions) {
		this.promotions = promotions;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getFavoriteNumer() {
		return favoriteNumer;
	}
	public void setFavoriteNumer(int favoriteNumer) {
		this.favoriteNumer = favoriteNumer;
	}
	public int getGoodCommentNumber() {
		return goodCommentNumber;
	}
	public void setGoodCommentNumber(int goodCommentNumber) {
		this.goodCommentNumber = goodCommentNumber;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getShopDesc() {
		return shopDesc;
	}
	public void setShopDesc(String shopDesc) {
		this.shopDesc = shopDesc;
	}
	public ArrayList<Notification> getNotifications() {
		return notifications;
	}
	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}
	public int getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}
	public int getFreight() {
		return freight;
	}
	public void setFreight(int freight) {
		this.freight = freight;
	}
	public int getCommentNumber() {
		return commentNumber;
	}
	public void setCommentNumber(int commentNumber) {
		this.commentNumber = commentNumber;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCategoryDesc() {
		return categoryDesc;
	}
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}
	public String getQualityDesc() {
		return qualityDesc;
	}
	public void setQualityDesc(String qualityDesc) {
		this.qualityDesc = qualityDesc;
	}
	public String getQualityInfo() {
		return qualityInfo;
	}
	public void setQualityInfo(String qualityInfo) {
		this.qualityInfo = qualityInfo;
	}
	public String getResponseDesc() {
		return responseDesc;
	}
	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}
	public String getResponseInfo() {
		return responseInfo;
	}
	public void setResponseInfo(String responseInfo) {
		this.responseInfo = responseInfo;
	}
	public String getLisence() {
		return lisence;
	}
	public void setLisence(String lisence) {
		this.lisence = lisence;
	}

	public ArrayList<StoreModel> getSubStores() {
		return subStores;
	}

	public void setSubStores(ArrayList<StoreModel> subStores) {
		this.subStores = subStores;
	}

	public int getStartingPrice() {
		return startingPrice;
	}

	public void setStartingPrice(int startingPrice) {
		this.startingPrice = startingPrice;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public String toString() {
		return "StoreModel{" +
				"address='" + address + '\'' +
				", id=" + id +
				", name='" + name + '\'' +
				", phone='" + phone + '\'' +
				", type=" + type +
				", favoriteNumer=" + favoriteNumer +
				", goodCommentNumber=" + goodCommentNumber +
				", orderNumber=" + orderNumber +
				", rating=" + rating +
				", shopDesc='" + shopDesc + '\'' +
				", notifications=" + notifications +
				", minAmount=" + minAmount +
				", freight=" + freight +
				", commentNumber=" + commentNumber +
				", image='" + image + '\'' +
				", categoryDesc='" + categoryDesc + '\'' +
				", qualityDesc='" + qualityDesc + '\'' +
				", qualityInfo='" + qualityInfo + '\'' +
				", responseDesc='" + responseDesc + '\'' +
				", responseInfo='" + responseInfo + '\'' +
				", lisence='" + lisence + '\'' +
				", subStores=" + subStores +
				", recentComments=" + recentComments +
				", promotions=" + promotions +
				'}';
	}
}
