package com.tianpingpai.ui;

import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.model.Model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Binder {

    private HashMap<Class<? extends Annotation>, ViewBinder> binderMap = new HashMap<>();

    public void registerViewBinder(Class<? extends Annotation> ac, ViewBinder vb) {
        binderMap.put(ac, vb);
    }

    {
        registerViewBinder(OnClick.class, new OnClickBinder());
        registerViewBinder(Binding.class, new BindingBinder());
    }

    Field[] fields;

    @SuppressWarnings("unchecked")
    private void bindFields(Object obj, View view) {
        fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            Annotation[] annotations = f.getAnnotations();
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    ViewBinder viewBinder = binderMap.get(annotation.annotationType());
                    if (viewBinder != null) {
                        viewBinder.bindView(this, obj, f, annotation, view);
                    }
                }
            }
        }
    }

    public void bindView(Object obj, View view) {
        bindFields(obj, view);
//        bindMethods(obj, view);
    }

    @SuppressWarnings("unchecked")
    public void bindData(Object model) {

        Map<String, Object> map;
        if (model instanceof Model) {
            Model m = (Model) model;
            map = m.getAll();
        } else {
            map = new HashMap<>();
            Field[] fields = model.getClass().getDeclaredFields();
            for (Field f : fields) {
                SerializedName sn = f.getAnnotation(SerializedName.class);
                if (sn != null) {
                    try {
                        f.setAccessible(true);
                        Object value = f.get(model);
                        map.put(sn.value(), value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (Field f : fields) {
            Annotation[] annotations = f.getAnnotations();
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    ViewBinder viewBinder = binderMap.get(annotation.annotationType());
                    if (viewBinder != null) {
                        viewBinder.bindData(annotation, map);
                    }
                }
            }
        }
    }
}
