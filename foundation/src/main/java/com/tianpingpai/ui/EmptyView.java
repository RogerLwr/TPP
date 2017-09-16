package com.tianpingpai.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyView {
    @LayoutRes int emptyLayout() default -1;
    @DrawableRes int imageResource() default -1;
    @StringRes int text() default -1;
}
