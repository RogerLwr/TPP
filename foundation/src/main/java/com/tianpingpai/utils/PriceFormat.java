package com.tianpingpai.utils;

import java.text.DecimalFormat;

public class PriceFormat {
	
	private static DecimalFormat df = new DecimalFormat("###0.0");
	
	/** 格式化 价格 保证小数点后 最多2位
	 * @param total_price
	 * @return
	 */
	public static float formatPrice(float total_price){
		
		String str_total_price = df.format(total_price);
		
		float float_total_price = Float.parseFloat(str_total_price);
		
		return float_total_price;
	}
	
	public static String format(double price){
		int intVersion = (int) price;
		if(price - intVersion < 0.000001){
			return intVersion + "";
		}
		return df.format(price);
	}
	
}
