package com.tianpingpai.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.utils.SingletonFactory;

public class Storage {

    public static Storage getInstance(){
        return SingletonFactory.getInstance(Storage.class);
    }

    private static final String KEY_STORAGE = "storage";
    SharedPreferences global = ContextProvider.getContext().getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);

    public String getString(String key){
        return global.getString(key,null);
    }

    public void putString(String key,String value){
        global.edit().putString(key,value).commit();
    }
}
