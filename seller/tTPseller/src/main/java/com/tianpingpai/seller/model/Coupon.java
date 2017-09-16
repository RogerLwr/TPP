package com.tianpingpai.seller.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LiuWenRong on 15/8/18. 订单提交成功 返回的 订单 优惠卷 接口数据
 */
public class Coupon implements Serializable{



    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("beginTime")
    private String beginTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("limiter")
    private double limit;

    @SerializedName("money")
    private double money;

    @SerializedName("rule")
    private String rule;

    @SerializedName("valid")
    private boolean valid;

    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", limiter=" + limit +
                ", money=" + money +
                ", rule='" + rule + '\'' +
                ", valid=" + valid +
                '}';
    }
}
