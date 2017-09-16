package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable{
	private static final long serialVersionUID = 5090765446358234990L;
	public static final int STATUS_MARKED = 1;
	public static final int STATUS_IN_CART = 2;
	public static final int STATUS_IN_ORDER = 3;
	public static final int STATUS_IN_ORDER_IN_TIME = 4;

	//促销
	@SerializedName("attr_stand")
	private String attrStand;
	@SerializedName("attr_weight")
	private String attrWeight;
	@SerializedName("pid")
	private  long pid;
	@SerializedName("attr_list")
	private transient List<Model> attrList;
	@SerializedName("color")
	private String color;
	@SerializedName("prod_status")
	private String prodStatus;
	@SerializedName("sales_status")
	private String salesStatus;
	@SerializedName("stock")
	private int stock;
	@SerializedName("start_time")
	private String startTime;
	@SerializedName("end_time")
	private String endTime;
	@SerializedName("limit")
	private String limit;

	@SerializedName("prod_img")
	private String imageUrl;
	
	@SerializedName("id")
	private long id;
	@SerializedName("order_id")
	private long orderId;
	@SerializedName("prod_name")
	private String name;
	@SerializedName("sale_num")
	private int saleNumber;

	/**
	 * 上下架 状态  1 上架  0 下架
	 */
	@SerializedName("status")
	private int status;
//	@SerializedName("price")
//	private double price;
	@SerializedName("coupon_price")
	private double couponPrice;
	@SerializedName("description")
	private String description;
	@SerializedName("unit")
	private String unit;
	@SerializedName("sale_user_id")
	private int sellerId;
	@SerializedName("sale_name")
	private String sellerName;

	/**
	 * 一级分类
	 */
	@SerializedName("fcategory_id")
	private int fCategoryId;
	@SerializedName("fcategory_name")
	private String fCategoryName;

	/**
	 * 二级分类
	 */
	@SerializedName("pcategory_id")
	private int secondCategoryID;
	@SerializedName("pcategory_name")
	private String pCategoryName;

	/**
	 * 当前商品所属分类 (2级或者3级分类)
	 */
	@SerializedName("category_id")
	private int categoryId;
	@SerializedName("category_name")
	private String categoryName;

	public String getAttrStand() {
		return attrStand;
	}

	public String getAttrWeight() {
		return attrWeight;
	}

	public void setAttrWeight(String attrWeight) {
		this.attrWeight = attrWeight;
	}

	public void setAttrStand(String attrStand) {
		this.attrStand = attrStand;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public List<Model> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<Model> attrList) {
		this.attrList = attrList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getProdStatus() {
		return prodStatus;
	}

	public void setProdStatus(String prodStatus) {
		this.prodStatus = prodStatus;
	}

	public String getSalesStatus() {
		return salesStatus;
	}

	public void setSalesStatus(String salesStatus) {
		this.salesStatus = salesStatus;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	private int dbId = -1;//������ݿ��е�id
	
	private int cartStatus;
	
	public int getCartStatus() {
		return cartStatus;
	}
	public void setCartStatus(int cartStatus) {
		this.cartStatus = cartStatus;
	}
	public int getDbId() {
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	


	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public int getCategoryId() {
		return categoryId;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String url) {
		this.imageUrl = url;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	

	
	public int getSecondCategoryID() {
		return secondCategoryID;
	}
	public void setSecondCategoryID(int secondCategoryID) {
		this.secondCategoryID = secondCategoryID;
	}


	
	public int getFirstCategoryId() {
		return fCategoryId;
	}
	public void setFristCategoryId(int pCategoryId) {
		this.fCategoryId = pCategoryId;
	}

	private int multiShopId;

	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public int getMultiShopId() {
		return multiShopId;
	}

	public void setMultiShopId(int multiShopId) {
		this.multiShopId = multiShopId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SerializedName("prod_price")
	private double productPrice;

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	@SerializedName("remark")
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	private String comment;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrderId() {
		return orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getSaleNumber() {
		return saleNumber;
	}

	public void setSaleNumber(int saleNumber) {
		this.saleNumber = saleNumber;
	}

//	public double getPrice() {
//		return price;
//	}

//	public void setPrice(double price) {
//		this.price = price;
//	}

	@SerializedName("prod_num")
	private int productNum;

	public int getProductNum() {
		return productNum;
	}
	public void setProductNum(int productNum) {
		this.productNum = productNum;
	}
	
	public double getCouponPrice() {
		return couponPrice;
	}
	public void setCouponPrice(double couponPrice) {
		this.couponPrice = couponPrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getfCategoryId() {
		return fCategoryId;
	}

	public void setfCategoryId(int fCategoryId) {
		this.fCategoryId = fCategoryId;
	}

	public String getfCategoryName() {
		return fCategoryName;
	}

	public void setfCategoryName(String fCategoryName) {
		this.fCategoryName = fCategoryName;
	}

	public String getpCategoryName() {
		return pCategoryName;
	}

	public void setpCategoryName(String pCategoryName) {
		this.pCategoryName = pCategoryName;
	}

	@Override
	public String toString() {
		return "ProductModel{" +
				"pid=" + pid +
				", attrList=" + attrList +
				", color='" + color + '\'' +
				", prodStatus='" + prodStatus + '\'' +
				", salesStatus='" + salesStatus + '\'' +
				", stock=" + stock +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", limit='" + limit + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				", id=" + id +
				", orderId=" + orderId +
				", name='" + name + '\'' +
				", saleNumber=" + saleNumber +
				", status=" + status +
				", couponPrice=" + couponPrice +
				", description='" + description + '\'' +
				", unit='" + unit + '\'' +
				", sellerId=" + sellerId +
				", sellerName='" + sellerName + '\'' +
				", fCategoryId=" + fCategoryId +
				", fCategoryName='" + fCategoryName + '\'' +
				", secondCategoryID=" + secondCategoryID +
				", pCategoryName='" + pCategoryName + '\'' +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", dbId=" + dbId +
				", cartStatus=" + cartStatus +
				", multiShopId=" + multiShopId +
				", productPrice=" + productPrice +
				", remark='" + remark + '\'' +
				", comment='" + comment + '\'' +
				", productNum=" + productNum +
				'}';
	}
}
