package com.tianpingpai.ui;

import android.support.annotation.LayoutRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Layout {
    @LayoutRes int id();
    @LayoutRes int actionBarLayout() default -1;
}
