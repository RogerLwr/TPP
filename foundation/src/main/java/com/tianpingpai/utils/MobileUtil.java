package com.tianpingpai.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机信息与用户信息工具类
 * @author Administrator
 *
 */
public class MobileUtil {
	
	/**
	 * 验证手机号码是否为数字
	 * @param mobileNum
	 * @return
	 */
	public static boolean isMoblieNum(String mobileNum){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(mobileNum);
		if (!isNum.matches() || mobileNum.length() != 11) {
			return false;
		}
		return true;
	}
	
	/**
	 * 判断密码的长度是否大于等于6
	 * @param pwd
	 * @return
	 */
	public static boolean isPWDNum(String pwd){
		if(pwd.length() < 6){
			return false;
		}
		return true;
	}
	
	public static String getDateFormlong(long times){
		Date date = new Date(times);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(date);
	}
}
