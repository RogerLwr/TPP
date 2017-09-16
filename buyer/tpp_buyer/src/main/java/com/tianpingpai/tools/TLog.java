package com.tianpingpai.tools;

import android.util.Log;

import com.tianpingpai.buyer.model.URLApi;

/**
 * 公共日志工具类
 * @author Administrator
 *
 */
public class TLog {
	//公关日志的开关
	private static final boolean isDebug = URLApi.IS_DEBUG;
//	private static final boolean isDebug = false;
	
	/**
	 * 打印错误日志
	 * @param tag
	 * @param log
	 */
	public static void e(String tag , String log){
		if(isDebug){
			Log.e(tag, log);
		}
	}
	
	/**
	 * 打印警告日志
	 * @param tag
	 * @param log
	 */
	public static void w(String tag , String log){
		if(isDebug){
			Log.w(tag, log);
		}
	}
	
	/**
	 * 打印信息级别日志
	 * @param tag
	 * @param log
	 */
	public static void i(String tag , String log){
		if(isDebug){
			Log.i(tag, log);
		}
	}
	
	/**
	 * 打印低级别的日志
	 * @param tag
	 * @param log
	 */
	public static void d(String tag , String log){
		if(isDebug){
			Log.d(tag, log);
		}
	}
}
