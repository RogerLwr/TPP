package com.tianpingpai.buyer.model;

import com.tianpingpai.model.SellerModel;

public class ShopRemark {
	private long sellerId;
	private String remark;
	
	public long getSellerId() {
		return sellerId;
	}
	public void setSellerId(long sellerId) {
		this.sellerId = sellerId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSellerModel(SellerModel sellerModel) {
		this.sellerModel = sellerModel;
	}

	public SellerModel getSellerModel() {
		return sellerModel;
	}

	private SellerModel sellerModel;
}
