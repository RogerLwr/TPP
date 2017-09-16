package com.tianpingpai.http;

public class CookieModel {
	private String mName;
	private String mContent;
	private String mHost;
	private long mDateExpires;
	
	public String getName(){
		return mName;
	}
	
	public void setName(String name){
		this.mName = name;
	}
	
	public String getContent() {
		return mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	public String getHost() {
		return mHost;
	}
	public void setHost(String host) {
		this.mHost = host;
	}
	public long getExpires() {
		return mDateExpires;
	}
	public void setExpires(long expires) {
		this.mDateExpires = expires;
	}
}
