package com.tianpingpai.parser;


public abstract class HttpResult<T> {

	public static final int CODE_AUTH = 1;

	public HttpResult() {
		super();
	}

	abstract public int getCode();
	abstract public void setCode(int code);
	abstract public String getDesc();
	abstract public void setDesc(String desc);

}