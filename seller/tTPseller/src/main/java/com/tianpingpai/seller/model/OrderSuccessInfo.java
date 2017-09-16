package com.tianpingpai.seller.model;

import java.io.Serializable;
import java.util.List;

public class OrderSuccessInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String order_ids;
	private int pay_type;
	private String type_name;
	private String status_name;
	private float mny;
//	private List<Order> orders;
//	private List<Coupon> coupons;

	public String getOrder_ids() {
		return order_ids;
	}
	public void setOrder_ids(String order_ids) {
		this.order_ids = order_ids;
	}
//	public int getPay_type() {
//		return pay_type;
//	}
//	public void setPay_type(int pay_type) {
//		this.pay_type = pay_type;
//	}
//	public String getType_name() {
//		return type_name;
//	}
//	public void setType_name(String type_name) {
//		this.type_name = type_name;
//	}
//	public String getStatus_name() {
//		return status_name;
//	}
//	public void setStatus_name(String status_name) {
//		this.status_name = status_name;
//	}
//	public float getMny() {
//		return mny;
//	}
//	public void setMny(float mny) {
//		this.mny = mny;
//	}
//
//	public List<Order> getOrders() {
//		return orders;
//	}
//
//	public void setOrders(List<Order> orders) {
//		this.orders = orders;
//	}
//
//	public List<Coupon> getCoupons() {
//		return coupons;
//	}
//
//	public void setCoupons(List<Coupon> coupons) {
//		this.coupons = coupons;
//	}

	@Override
	public String toString() {
		return "order_ids='" + order_ids + '\'' +
				", pay_type=" + pay_type +
				", type_name='" + type_name + '\'' +
				", status_name='" + status_name + '\'' +
				", mny=" + mny +
				'}';
	}
}
