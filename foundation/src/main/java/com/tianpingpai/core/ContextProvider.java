package com.tianpingpai.core;

import android.app.Application;

public class ContextProvider {
	static Application sInstance;
	static String sBaseURL;
	
	public static void init(Application app){
		sInstance = app;
	}
	
	public static Application getContext(){
		return sInstance;
	}
	
	public static String getBaseURL(){
		return sBaseURL;
	}
	
	public static void setBaseURL(String url){
		sBaseURL = url;
	}
}
