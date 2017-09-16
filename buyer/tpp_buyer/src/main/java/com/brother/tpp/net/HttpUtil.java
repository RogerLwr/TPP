package com.brother.tpp.net;

import com.tianpingpai.tools.TLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 网络接口请求的协议
 * @author Administrator
 *
 */
public class HttpUtil {
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static String TAG ="HttpUtil";
	static{
		client.setTimeout(9000);//设置网络超时，默认是9s
	}
	
	public static void get(String url,
			AsyncHttpResponseHandler response){
		client.get( url, response);
	}

	public static void get(String url,
			RequestParams params,AsyncHttpResponseHandler response){
		client.get(url, params, response);
	}
	
	public static void get(String url
			, JsonHttpResponseHandler response){
		client.get(url, response);
	}
	
	public static void get(String url,
			RequestParams params,JsonHttpResponseHandler response){
		TLog.w(TAG, "73-------api---result---"+url+"?"+params);
		client.get(url, params, response);
	}
	
	public  static void get(String url,
			BinaryHttpResponseHandler response){
		client.get( url, response);
	}
	
	public static void post(String url,
			AsyncHttpResponseHandler response){
		client.post(url, response);
	}

	public static void post(String url,RequestParams params,
			AsyncHttpResponseHandler response){
		client.post(url,params, response);
	}
	
	public static void post(String url,
			JsonHttpResponseHandler response){
		client.post(url, response);
	}
	
	public static void post(String url,RequestParams params,
			JsonHttpResponseHandler response){
		TLog.w(TAG, "137-----api---result===="+url+"?"+params);
		client.post(url,params, response);
	}
	public  static void post(String url,
			BinaryHttpResponseHandler response){
		client.post( url, response);
	}
	
}
