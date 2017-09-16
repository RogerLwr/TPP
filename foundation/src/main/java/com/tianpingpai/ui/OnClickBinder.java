package com.tianpingpai.ui;

import android.view.View;

import java.lang.reflect.Field;
import java.util.Map;

public class OnClickBinder implements ViewBinder<OnClick> {
    @Override
    public void bindView(Binder binder,Object obj, Field field, OnClick onClick, View view) {
        View v = view.findViewById(onClick.value());
        if(v != null){
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                final View.OnClickListener oldListener = (View.OnClickListener) field.get(obj);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oldListener.onClick(v);
                    }
                });
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void bindData(OnClick annotation, Map<String,Object> model) {

    }
}
