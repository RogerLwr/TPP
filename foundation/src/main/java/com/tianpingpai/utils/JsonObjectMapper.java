package com.tianpingpai.utils;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.Model;

import java.lang.reflect.Field;


public class JsonObjectMapper {
    public static void map(Model m,Object o){
        Field[] fields = o.getClass().getDeclaredFields();
        for(Field f:fields){
            SerializedName sn = f.getAnnotation(SerializedName.class);
            if(sn != null){
                String key = sn.value();
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                try {
                    f.set(o,m.get(key));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                f.setAccessible(accessible);
            }
        }
    }
}
