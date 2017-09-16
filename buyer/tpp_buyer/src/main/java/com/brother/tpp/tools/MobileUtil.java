package com.brother.tpp.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtil {
	
	public static boolean isMobileNumber(String mobileNum){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(mobileNum);
		return !(!isNum.matches() || mobileNum.length() != 11);
	}
	
	public static boolean isPWDNum(String pwd){
		return pwd.length() >= 5;
	}
}
