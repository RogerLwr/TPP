package com.tianpingpai.seller.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author Administrator
 */
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int USER_TYPE_BUYER = 1;
    public static final int USER_TYPE_SELLER = 0;

    public static final int GRADE_1 = 1;
    public static final int GRADE_3 = 0;

    private String phone;
    @SerializedName("display_name")
    private String nickName;//用户昵称
    @SerializedName("user_id")
    private String userID;//用户id

    private String password;//用户密码
    @SerializedName("user_type")
    private int userType;//0为卖家，1为买家
    private double balance;//用户余额
    @SerializedName("accessToken")
    private String accessToken;//用户登录凭证

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    @SerializedName("grade")
    private int grade;

//	public ErrorInfo errorInfo = new ErrorInfo();
//	@SerializedName("receiveAddress")
//	public ArrayList<AddressModel> addreses = new ArrayList<AddressModel>();

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "UserModel [phone=" + phone + ", nickName=" + nickName
                + ", userID=" + userID + ", password=" + password
                + ", userType=" + userType + ", balance=" + balance
                + ", accessToken=" + accessToken + ", errorInfo="
                + ", addreses=" + "]";
    }

}
