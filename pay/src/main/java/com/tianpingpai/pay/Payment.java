package com.tianpingpai.pay;

public class Payment {
    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    private double totalFee;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String orderId;

    public String getCouponIds() {
        return couponIds;
    }

    public void setCouponIds(String couponIds) {
        this.couponIds = couponIds;
    }

    private String couponIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    private String ticket;

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public String getValidationCode() {
        return validationCode;
    }

    private String validationCode;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private double balance;
}
