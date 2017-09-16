package com.tianpingpai.buyer.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/** 个人中心 订单 数量飘红的类
 * @author lwr
 *
 */
public class OrderStatusCount implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 数量
	 */
	@SerializedName("count")
	private int count;
	/**
	 * 状态码  详见常量工具类CommonUtil
	 */
	@SerializedName("status")
	private int status;
	/**
	 * 状态名  如 已收货,待付款
	 */
	@SerializedName("status_name")
	private String status_name;
	@Override
	public String toString() {
		return "OrderStatusCount [count=" + count + ", status=" + status
				+ ", status_name=" + status_name + "]";
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatus_name() {
		return status_name;
	}
	public void setStatus_name(String status_name) {
		this.status_name = status_name;
	}
	
	
	
}
