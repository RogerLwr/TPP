package com.tianpingpai.utils;

import com.tianpingpai.core.ContextProvider;

public class DimensionUtil {
	public static int dip2px(float dipValue) {
		final float scale = ContextProvider.getContext().getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	
	public static float sp2px(float spValue){
		return ContextProvider.getContext().getResources().getDisplayMetrics().scaledDensity * spValue ;
	}
}
