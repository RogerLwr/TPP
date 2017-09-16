package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;

public class CommentModel {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("saler_id")
    private int salerId;
    @SerializedName("order_id")
    private long orderId;
    @SerializedName("username")
    private String username;
    @SerializedName("content")
    private String content;
    @SerializedName("grade")
    private int grade;
    @SerializedName("speed")
    private int speed;
    @SerializedName("quality")
    private int quality;
    @SerializedName("service")
    private int service;
    @SerializedName("reply_user_id")
    private int replyUserId;
    @SerializedName("reply_username")
    private String replyUserName;
    @SerializedName("createdTime")
    private String createTime;
    @SerializedName("status")
    private int status;
    @SerializedName("score")
    private int score;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getSalerId() {
        return salerId;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public int getGrade() {
        return grade;
    }

    public int getSpeed() {
        return speed;
    }

    public int getQuality() {
        return quality;
    }

    public int getService() {
        return service;
    }

    public int getReplyUserId() {
        return replyUserId;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setSalerId(int salerId) {
        this.salerId = salerId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", userId=" + userId +
                ", salerId=" + salerId +
                ", orderId=" + orderId +
                ", username='" + username + '\'' +
                ", grade=" + grade +
                ", speed=" + speed +
                ", quality=" + quality +
                ", service=" + service +
                ", replyUserId=" + replyUserId +
                ", replyUserName='" + replyUserName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", status=" + status +
                ", score=" + score +
                '}';
    }

    public void setService(int service) {
        this.service = service;
    }

    public void setReplyUserId(int replyUserId) {
        this.replyUserId = replyUserId;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
