package com.tianpingpai.buyer.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.ProductModel;

public class SellerProductDetail extends ProductModel {

	private static final long serialVersionUID = 5322463323906263007L;
	
	@SerializedName("sale_name")
	private String sellerName;
	@SerializedName("sale_addr")
	private String sellerAddress;
	@SerializedName("shop_desc")
	private String shopDescription;
	@SerializedName("score")
	private float rating;
	@SerializedName("sales")
	private int salesNumber;
	@SerializedName("addr")
	private String origin;
	@SerializedName("history_prices")
	private List<HistoryPrice> historyPrices;
	public class HistoryPrice{
		@SerializedName("price_list")
		public List<PriceList> priceList;
		public class PriceList{
			@SerializedName("price")
			public float price;
			@SerializedName("date")
			public String date;
			
			@Override
			public String toString() {
				return "PriceList [price=" + price + ", date=" + date + "]";
			}
			
		}
		@Override
		public String toString() {
			return "HistoryPrice [priceList=" + priceList + "]";
		}
		
	}
	
	public List<HistoryPrice> getHistoryPrices() {
		return historyPrices;
	}
	public void setHistoryPrices(List<HistoryPrice> historyPrices) {
		this.historyPrices = historyPrices;
	}
	
	@Override
	public String toString() {
		return "SellerProductDetail [sellerName=" + sellerName
				+ ", sellerAddress=" + sellerAddress + ", shopDescription="
				+ shopDescription + ", rating=" + rating + ", salesNumber="
				+ salesNumber + ", origin=" + origin + ", historyPrices="
				+ historyPrices + ", pCatId=" + pCatId + "]";
	}

	@SerializedName("pcategory_id")
	private int pCatId;
	public int getpCatId() {
		return pCatId;
	}
	public void setpCatId(int pCatId) {
		this.pCatId = pCatId;
	}
	
	@Override
	public int getCategoryId() {
		return pCatId;
	}
	
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public int getSalesNumber() {
		return salesNumber;
	}
	public void setSalesNumber(int salesNumber) {
		this.salesNumber = salesNumber;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerAddress() {
		return sellerAddress;
	}
	public void setSellerAddress(String sallerAddress) {
		this.sellerAddress = sallerAddress;
	}
	public String getShopDescription() {
		return shopDescription;
	}
	public void setShopDescription(String shopDescription) {
		this.shopDescription = shopDescription;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
}
