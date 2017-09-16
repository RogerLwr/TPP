package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class OrderLog {
    @SerializedName("order_id")
    private long orderId;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("user_type")
    private String userType;
    @SerializedName("oper_dt")
    private String operDt;
    @SerializedName("oper_type")
    private String operType;
    @SerializedName("oper_desc")
    private String operDesc;
    @SerializedName("order_dt")
    private String oderDt;
    @SerializedName("s_phone")
    private String sellerPhone;
    @SerializedName("s_display_name")
    private String sellerDiaplayName;
    @SerializedName("order_opt")
    private String oderOpt;
    @SerializedName("b_phone")
    private String buyerPhone;
    @SerializedName("b_display_name")
    private String buyerName;

    @SerializedName("sale_name")
    private String sallerName;
    @SerializedName("mny")
    private double amount;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOperDt() {
        return operDt;
    }

    public void setOperDt(String operDt) {
        this.operDt = operDt;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getOperDesc() {
        return operDesc;
    }

    public void setOperDesc(String operDesc) {
        this.operDesc = operDesc;
    }

    public String getOrderTime() {
        return oderDt;
    }

    public void setOrderTime(String oderDt) {
        this.oderDt = oderDt;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerDiaplayName() {
        return sellerDiaplayName;
    }

    public void setSellerDiaplayName(String sellerDiaplayName) {
        this.sellerDiaplayName = sellerDiaplayName;
    }

    public String getOderOpt() {
        return oderOpt;
    }

    public void setOderOpt(String oderOpt) {
        this.oderOpt = oderOpt;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public double getAmount() {
        return amount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sallerName;
    }

    public void setSellerName(String sallerName) {
        this.sallerName = sallerName;
    }

    public String getOperationDesc() {
        return operDesc;
    }
}
