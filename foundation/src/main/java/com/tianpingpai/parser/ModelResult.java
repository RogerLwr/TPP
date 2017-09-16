package com.tianpingpai.parser;

import com.google.gson.annotations.SerializedName;

public class ModelResult<T> extends HttpResult<T> {

	@SerializedName("result")
	private T model;

	public void setModel(T m) {
		this.model = m;
	}

	public T getModel() {
		return model;
	}

	public boolean isSuccess() {
		return code == 0;
	}

	@SerializedName("statusCode")
	private int code;
	@SerializedName("statusDesc")
	private String desc;

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	@Override
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
