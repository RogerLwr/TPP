package com.tianpingpai.ui;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ActionSheetTransitionManager implements ViewTransitionManager {
    @Override
    public void pushViewController(ViewController vc) {

    }

    @Override
    public void popViewController(ViewController vc) {

    }

    private Animation rtlInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 1, type, 0, type,
                0, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation rtlOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, -1, type,
                0, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;
}
