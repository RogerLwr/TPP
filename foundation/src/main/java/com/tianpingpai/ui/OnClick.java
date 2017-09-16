package com.tianpingpai.ui;

import android.support.annotation.IdRes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented()
@Target(ElementType.FIELD)
public @interface OnClick {
    @IdRes int value();
}
