package com.tianpingpai.ui;

import android.view.View;

import com.tianpingpai.model.Model;

import java.lang.reflect.Field;
import java.util.Map;

public interface ViewBinder<A> {
    void bindView(Binder binder,Object object,Field field,A annotation,View view);
    void bindData(A annotation,Map<String,Object> map);
}
