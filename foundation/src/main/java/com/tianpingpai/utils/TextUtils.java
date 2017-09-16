package com.tianpingpai.utils;

import org.json.JSONObject;

public class TextUtils {
    public static boolean isEmpty(CharSequence s){
        if("null".equals(s)){
            return true;
        }
        return android.text.TextUtils.isEmpty(s);
    }

    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }

    public static String getVlaue(String s){
        if(!isEmpty(s)){
            return s;
        }
        return "";
    }
}
