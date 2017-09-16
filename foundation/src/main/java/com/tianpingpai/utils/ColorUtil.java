package com.tianpingpai.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by LiuWenRong on 15/11/26.
 *
 */
public class ColorUtil {

    public static int getColorByString(String colorStr){

        int colorInt = 0;

        if(TextUtils.isEmpty(colorStr)){
            colorStr = "#cc22e2";
        }

        try {
            colorInt = Color.parseColor(colorStr);
        }catch (RuntimeException e){
            e.printStackTrace();
            colorInt = Color.parseColor("#cc22e2");
        }
        Log.e("xx", "26------------colorStr="+colorStr+",  colorInt="+colorInt);
        return colorInt;
    }

}
