package com.tianpingpai.user;

import com.google.gson.annotations.SerializedName;

public class UserModel {
	@SerializedName("marketer_id")
	private int id;
	
	@SerializedName("login_name")
	private String name;
	
	@SerializedName("display_name")
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@SerializedName("accessToken")
	private String accessToken;
	
	@SerializedName("login_name")
	private String loginName;
	
//	"password": "14e1b600b1fd579f47433b88e8d85291",
	@SerializedName("phone")
	private String phone;
	
	@SerializedName("department_id")
	private String departmentId;
	
	@SerializedName("position")
	private String position;
	
	@SerializedName("parent_id")
	private String parentId;

	@SerializedName("marketer_id")
	private int marketerId;

//	@SerializedName("level")
//	private String level;

	@SerializedName("level")
	private int level;
    
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getAccessToken() {
		return accessToken;
	}
	
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public void setMarketerId(int marketerId){
		this.marketerId = marketerId;
	}
	public int getMarketerId(){
		return marketerId;
	}

	@Override
	public String toString() {
		return "UserModel [id=" + id + ", name=" + name + "]";
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


	@Override
	public boolean equals(Object o) {
        if(o == this){
            return true;
        }
        if(o instanceof UserModel){
            UserModel om = (UserModel) o;
            return om.id == id;
        }
        return false;
	}

    @Override
    public int hashCode() {
        return id;
    }
}
