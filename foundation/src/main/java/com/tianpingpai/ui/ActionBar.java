package com.tianpingpai.ui;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented()
@Target(ElementType.TYPE)
public @interface ActionBar {
    String title() default "";
    String rightText() default "";
    @StringRes int titleRes() default -1;
    @LayoutRes int layout() default -1;
    boolean hidden() default false;
}
