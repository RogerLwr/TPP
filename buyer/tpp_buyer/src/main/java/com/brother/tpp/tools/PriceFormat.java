package com.brother.tpp.tools;

import java.text.DecimalFormat;

public class PriceFormat {
	
	private static DecimalFormat df = new DecimalFormat("###0.00");
	
	public static float formatPrice(float total_price){
		String str_total_price = df.format(total_price);
		return Float.parseFloat(str_total_price);
	}
	
	public static String format(double price){
		int intVersion = (int) price;
		if(price - intVersion < 0.000001){
			return intVersion + "";
		}
		return df.format(price);
	}
}
