package com.tianpingpai.buyer.model;

public class URLApi {

    public static int width = 130;
    public static int height = 130;// 从七牛下载图片的格式

	public static String getImageTP(int width, int height){
		return "?imageMogr2/auto-orient/thumbnail/" + width + "x" + height + "!/strip/quality/80/format/jpg/interlace/1";
	}

	public static final boolean IS_DEBUG = false;

	public static final boolean REPLACE_ACCESSTOKEN = false; //替换accessToken

	public static String getBaseUrl(){
		if(IS_DEBUG){
			return "http://test.tianpingpai.com";
//			return "http://release.tianpingpai.com";
		}else {
			return "http://api.tianpingpai.com";
		}
	}

	public static String getH5Base(){
		if(IS_DEBUG){
			return "http://test.tianpingpai.com";
		}else {
			return "http://m.tianpingpai.com";
		}
	}

	public static String dashBoard(){
		return getBaseUrl() + "/api/dashboard/main";
	}

	public static class Activity{
		public static String getDetail(){
			return getBaseUrl() + "/api/activity/get";
		}

		public static String join(){
			return getBaseUrl() + "/api/activity/join";
		}
	}
	
	public static class Mine{
		public static String getOrderCounts(){
			return getBaseUrl() + "/api/user/getOrderCounts.json";
		}
	}
}

