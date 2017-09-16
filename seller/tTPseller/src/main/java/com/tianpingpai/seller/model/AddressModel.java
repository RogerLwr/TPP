package com.tianpingpai.seller.model;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.Model;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private static final long serialVersionUID = 317012091427300738L;

    @SerializedName("id")
    private int id;

    @SerializedName("consignee")
    private String username;

    @SerializedName("phone")
    private String phone;

    private int areaId;// 商户城市地址ID
    @SerializedName("area")
    private String area;// 商户城市地址

    @SerializedName("address")
    private String address;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lng")
    private String lng;

    @SerializedName("detail")
    private String detail;


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "AddressModel [id=" + getId() + ", username="
                + getUserName() + ", phone=" + ", areaId="
                + getAreaId() + ", area=" + getArea() + ", address="
                + getAddress() + ", lat=" + lat + ", lng=" + lng
                + ", detail=" + detail + "]";
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String userTelNum) {
        this.phone = userTelNum;
    }

    public static AddressModel fromJSONModle(Model m) {
        AddressModel am = new AddressModel();
        am.id = m.getInt("id");
        am.username = m.getString("consignee");
        am.address = m.getString("address");
        am.detail = m.getString("detail");
        am.setPhone(m.getString("phone"));
        am.area = m.getString("area");
        am.lat = String.valueOf(m.getDouble("lat"));
        am.lng = String.valueOf(m.getDouble("lng"));

        return am;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
