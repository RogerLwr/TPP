package com.tianpingpai.utils;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.tianpingpai.core.ContextProvider;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class Tools {
	private static DecimalFormat df = new DecimalFormat("###0.00");

	/** 格式化 距离   将米换算成 公里并保留两位小数
	 * @param
	 * @return
	 */
	public static String formatDistance(int distance){

		float fDistance = distance;
		float fDistanceKM = fDistance/1000;
		String strDistance = df.format(fDistanceKM);
		float floatDistance = Float.parseFloat(strDistance);
		Log.w("xx", "20-------------fDistance=" + fDistance + ",fDistanceKM=" + fDistanceKM + ",strDistance=" + strDistance + ",floatDistance=" + floatDistance);
		return strDistance;

	}

	/**
	 * 弹键盘
	 */
	public static void softInput(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager m = (InputMethodManager) ContextProvider.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 300);
	}

}
