package com.tianpingpai.share;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public interface SharePlatform {
    @DrawableRes
    int getIcon();

    @StringRes
    int getName();

    void share(ShareContent content);

    boolean isValid();

    @StringRes
    int getInvalidHint();
}
