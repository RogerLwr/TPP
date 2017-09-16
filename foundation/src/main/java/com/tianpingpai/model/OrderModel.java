package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderModel implements Serializable{
	private static final long serialVersionUID = 3703512809542353658L;
	public static final int STATUS_NO_PAYED = 0;
	public static final int STATUS_PENDING = 1; // 待卖家确认
	public static final int STATUS_NOT_SENT = 2; // 待发货
	//TODO
	public static final int STATUS_SENT = 3;  //  已发货(待收货)
	public static final int STATUS_RECEIVED = 4;  //已收货 待评价
	public static final int STATUS_CHECKED = 5;
	public static final int STATUS_CANCEL = 6;
	public static final int STATUS_CANCELING = 7;
	public static final int STATUS_CANCELED = 8;
	public static final int STATUS_COMMENTED = 9;
    public static final int PAY_TYPE_ON_LINE = 0;

    @SerializedName("order_id")
	private long id;//订单id
	@SerializedName("s_user_id")
	private long sellerId;//卖家id
	@SerializedName("mny")
	private double amount;//订单金额
	@SerializedName("total_mny")
	private double totalMny;//
	@SerializedName("deliver_mny")
	private double deliver_mny; //运费
	@SerializedName("deliver_mny")
	private double deliverMny; //配送费
	@SerializedName("coupon_mny")
	private double couponMny; //满减活动优惠金额
	@SerializedName("pay_desc")
	private String pay_desc;

	@SerializedName("hasCoupon")
	private String hasCoupon;

	public void setBuyerShopName(String buyerShopName) {
		this.buyerShopName = buyerShopName;
	}

	public double getCouponMny() {
		return couponMny;
	}

	public void setCouponMny(double couponMny) {
		this.couponMny = couponMny;
	}

	public double getDeliverMny() {
		return deliverMny;
	}

	public void setDeliverMny(double deliverMny) {
		this.deliverMny = deliverMny;
	}

	public double getTotalMny() {
		return totalMny;
	}

	public void setTotalMny(double totalMny) {
		this.totalMny = totalMny;
	}

	@SerializedName("deliver_dt")
	private String deliverTime;
	//remark
	@SerializedName("status")
	private int status;//0,未确认 1,未发货 2,已发货 3,已收货 4,已结账
	@SerializedName("status_show")
	private String statusShow;

	@SerializedName("status_name")
	private String statusName;

	@SerializedName("b_user_id")
	private String buyerId;//买家Id
	//deliver_num
	@SerializedName("order_dt")
	private String orderTime;//下单时间
	@SerializedName("recevier_name")
	private String receiverName;//收货人
	@SerializedName("address")
	private String address;//address
	@SerializedName("telephone")
	private String telephone;//telephone
	
	@SerializedName("buyer_name")
	private String buyerName;
	
	@SerializedName("s_user_name")
	private String sellerName;
	
	@SerializedName("deliver_name")
	private String deliverTypeName;

	@SerializedName("pay_type")
	private int payType;

	public void setPaymentDesc(String paymentDesc) {
		this.paymentDesc = paymentDesc;
	}

	@SerializedName("pay_desc")
    private String paymentDesc;

    public String getBuyerShopName() {
		return buyerShopName;
	}

	@SerializedName("b_shop_name")
	private String buyerShopName;
	
	@SerializedName("telephone")
	private String buyerPhone;
	
	@SerializedName("saler_phone")
	private String sellerPhone;
	
	@SerializedName("remark")
	private String remark;

	public double getDeliver_mny() {
		return deliver_mny;
	}

	public void setDeliver_mny(double deliver_mny) {
		this.deliver_mny = deliver_mny;
	}

	public String getHasCoupon() {
		return hasCoupon;
	}

	public void setHasCoupon(String hasCoupon) {
		this.hasCoupon = hasCoupon;
	}

	public int getPayType() {
		return payType;
	}

	public String getPayMethodDesc(){
		return payType == 0 ? "在线支付" : "货到付款";
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBuyerPhone() {
		return buyerPhone;
	}

	public void setBuyerPhone(String buyerPhone) {
		this.buyerPhone = buyerPhone;
	}

	public String getSellerPhone() {
		return sellerPhone;
	}

	public void setSellerPhone(String sellerPhone) {
		this.sellerPhone = sellerPhone;
	}

	public String getDeliverTypeName() {
		return deliverTypeName;
	}

	public void setDeliverTypeName(String deliverTypeName) {
		this.deliverTypeName = deliverTypeName;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	@SerializedName("sale_name")
	private String saleName;

	@SerializedName("saler_addr")
	private String salerAddr;

	public String getSalerAddr() {
		return salerAddr;
	}

	public void setSalerAddr(String salerAddr) {
		this.salerAddr = salerAddr;
	}

	@SerializedName("manage_b_name")
	private String buyerManagerName;
	
	@SerializedName("manage_s_name")
	private String sellerManagerName;
	
	@SerializedName("prod_list")
	private ArrayList<ProductModel> productList;

	@SerializedName("is_evalute")
	private int commentStatus ;

	public void setCommentStatus(int commentStatus){
		this.commentStatus = commentStatus;
	}

	public int getCommentStatus(){
		return commentStatus;
	}
	
	public String getBuyerManagerName() {
		return buyerManagerName;
	}

	public void setBuyerManagerName(String buyerManagerName) {
		this.buyerManagerName = buyerManagerName;
	}

	public String getSellerManagerName() {
		return sellerManagerName;
	}

	public void setSellerManagerName(String sellerManagerName) {
		this.sellerManagerName = sellerManagerName;
	}

	public ArrayList<ProductModel> getProductList() {
		return productList;
	}

	public void setProductList(ArrayList<ProductModel> productList) {
		this.productList = productList;
	}

	public String getSaleName() {
		return saleName;
	}

	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}

	public long getSellerId() {
		return sellerId;
	}

	public void setSellerId(long sellerId) {
		this.sellerId = sellerId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(String deliverTime) {
		this.deliverTime = deliverTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId(){
		return this.id;
	}
	
	public String getStatusDescription(){
		if(statusName != null){
			return statusName;
		}
		switch (status) {
		case STATUS_NO_PAYED:
			return "待付款";
		case STATUS_PENDING:
			return "待确认";
		case STATUS_NOT_SENT:
			return "待发货";
		case STATUS_SENT:
			return "已发货";
		case STATUS_RECEIVED:
			return "已收货";
		case STATUS_CHECKED:
			return "已结账";
		case STATUS_CANCEL:
			return "取消";
		case STATUS_CANCELING:
			return "取消中";
		case STATUS_CANCELED:
			return "已取消";
		case STATUS_COMMENTED:
			return "已评价";
		default:
			return "未知";
		}
	}
	public String getStatusShow() {
		return statusShow;
	}

	public void setStatusShow(String statusShow) {
		this.statusShow = statusShow;
	}

	public String getPaymentDesc() {
        return paymentDesc;
    }

	public String getPay_desc() {
		return pay_desc;
	}

	public void setPay_desc(String pay_desc) {
		this.pay_desc = pay_desc;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof OrderModel)) return false;

		OrderModel that = (OrderModel) o;

		return id == that.id;

	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}
