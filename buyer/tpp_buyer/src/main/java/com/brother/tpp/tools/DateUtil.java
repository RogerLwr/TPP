package com.brother.tpp.tools;

import com.tianpingpai.tools.TLog;

import java.text.SimpleDateFormat;

public class DateUtil {

	public static final String LOGIN_DATE_TWO = "yyyyMMddHHmmss";

	private static final String TAG = "DateUtil";

	/**
	 * 把日期转换为字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String DateToString(java.util.Date date, String format) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			TLog.w(TAG, "46-----------" + e);
		}
		return result;
	}
}
