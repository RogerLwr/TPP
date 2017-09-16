package com.tianpingpai.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tianpingpai.utils.DimensionUtil;

import java.util.LinkedList;

public class ActionSheet implements ViewTransitionManager {

    public static final int STYLE_BOTTOM_TO_TOP = 2;
    public static final int STYLE_RIGHT_TO_LEFT = 4;

    private static final int TRANSLATE_DURATION = 300;
    private static final int ALPHA_DURATION = 400;

    public void setDimBackground(boolean dimBackground) {
        this.dimBackground = dimBackground;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    private boolean cancelable = true;
    private boolean dimBackground = true;
    private boolean mDismissed = true;
    private ActionSheetListener mListener;
    private View mView;
    private RelativeLayout mPanel;
    private ViewGroup mGroup;
    private View mBackgroundView;
    private Handler mHandler = new Handler();

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    private FragmentActivity activity;

    public void setStyle(int style) {
        this.style = style;
    }

    private int style = STYLE_BOTTOM_TO_TOP;

    public boolean isShowing() {
        return !mDismissed;
    }

    private int height = DimensionUtil.dip2px(300);

    public void setHeight(int height) {
        this.height = height;
    }

    private int width = FrameLayout.LayoutParams.MATCH_PARENT;

    public void setWidth(int width) {
        this.width = width;
    }

    public ViewController getRootViewController() {
        return mRootViewController;
    }

    public void setViewController(ViewController vc) {
        this.mRootViewController = vc;
        this.mRootViewController.setViewTransitionManager(this);
        if(activity != null){
            mRootViewController.setActivity(activity);
        }
        mView = null;
        vcStack.clear();
        vcStack.push(vc);
    }

    private ViewController mRootViewController;
    private LinkedList<ViewController> vcStack = new LinkedList<>();

    public int getStackSize(){
        return vcStack.size();
    }

    @Deprecated
    public void show(@SuppressWarnings("unused") FragmentManager manager, @SuppressWarnings("unused") String tag) {
        if (!mDismissed) {
            return;
        }

        show();
    }

    public void show() {
        if(isInAnimation){
            return;
        }
        mDismissed = false;
        ViewGroup v = (ViewGroup) getActivity().findViewById(android.R.id.content);
        v.addView(onCreateView());
    }

    public void dismiss() {
        if (isInAnimation || mDismissed) {
            return;
        }
        mDismissed = true;
        onDestroyView();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup v = (ViewGroup) getActivity().findViewById(android.R.id.content);
                v.removeView(mView);
                isInAnimation = false;
            }
        }, TRANSLATE_DURATION);
    }

    public FragmentActivity getActivity() {
        return mRootViewController.getActivity();
    }

    public View onCreateView() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = getActivity().getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
        if(mView == null){
            mView = createView();
            mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
            mBackgroundView.setClickable(true);
            mBackgroundView.setOnClickListener(bgOnClickListener);
        }
        if (dimBackground) {
            mBackgroundView.startAnimation(createAlphaInAnimation());
        }

        Animation animation = null;
        if (style == STYLE_BOTTOM_TO_TOP) {
            animation = createBottomToTopAnimation();
        } else if (style == STYLE_RIGHT_TO_LEFT) {
            animation = rtlInAnimation();
        }
        if(animation != null){
            animation.setAnimationListener(showDismissAnimationListener);
            mPanel.startAnimation(animation);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if(parent != null){
            parent.removeView(mView);
        }
        return mView;
    }

    private Animation.AnimationListener showDismissAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isInAnimation = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isInAnimation = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation createBottomToTopAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTopToBottomOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
                0, type, 1);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createLeftToRightOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 1, type,
                0, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }


    private Animation createAlphaOutAnimation() {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private View createView() {
        RelativeLayout parent = new RelativeLayout(getActivity());
        parent.setLayoutParams(new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mBackgroundView = new View(getActivity());
        mBackgroundView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        if (dimBackground) {
            mBackgroundView.setBackgroundColor(Color.argb(136, 0, 0, 0));
        }
        mBackgroundView.setOnClickListener(mBackgroundViewListener);

        mPanel = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width, height);
//        params.gravity = Gravity.BOTTOM;
        if (style == STYLE_RIGHT_TO_LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (style == STYLE_BOTTOM_TO_TOP) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        mPanel.setLayoutParams(params);
        //TODO add vc's view

        mRootViewController.createView(getActivity().getLayoutInflater(), mPanel);
        mRootViewController.getView().setClickable(true);
        mPanel.addView(mRootViewController.getView(), getLayoutParams());

        parent.addView(mBackgroundView);
        parent.addView(mPanel);
        return parent;
    }


    public void onDestroyView() {
        if(mView == null){
            return;
        }
        Animation animation = null;
        switch (style) {
            case STYLE_BOTTOM_TO_TOP:
                animation = createTopToBottomOutAnimation();
                break;
            case STYLE_RIGHT_TO_LEFT:
                animation = createLeftToRightOutAnimation();
                break;
            default:
                animation = createTopToBottomOutAnimation();
        }
        animation.setAnimationListener(showDismissAnimationListener);
        mPanel.startAnimation(animation);
        mBackgroundView.startAnimation(createAlphaOutAnimation());
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGroup.removeView(mView);
            }
        }, ALPHA_DURATION);
        if (mListener != null) {
            mListener.onDismiss(this, true);
        }
    }


    private boolean getCancelableOnTouchOutside() {
        return cancelable;
    }

    public void setActionSheetListener(ActionSheetListener listener) {
        mListener = listener;
    }

    public RelativeLayout.LayoutParams getLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width, height);
        if (style == STYLE_RIGHT_TO_LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        return params;
    }

    @Override
    public void pushViewController(final ViewController vc) {
        if (isInAnimation) {
            return;
        }
        final ViewController last = vcStack.peekLast();
        last.getView().startAnimation(rtlOutAnimation());
        vc.createView(vc.getActivity().getLayoutInflater(), mPanel);
        vc.setViewTransitionManager(this);

        vcStack.addLast(vc);
        mPanel.addView(vc.getView(), getLayoutParams());

        Animation inAnimation = rtlInAnimation();
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                vc.getView().setEnabled(false);
                isInAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vc.animationFinished();
                isInAnimation = false;
                vc.getView().setEnabled(true);
                vc.animationFinished();
                mPanel.removeView(last.getView());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        vc.getView().startAnimation(inAnimation);
    }

    private boolean isInAnimation;

    @Override
    public void popViewController(final ViewController vc) {
        if (isInAnimation) {
            return;
        }
        if (vcStack.isEmpty()) {
            return;
        }
        vcStack.remove(vc);
        ViewController secondLast = vcStack.peekLast();
        vc.getView().startAnimation(ltrOutAnimation());

        if (secondLast == null || secondLast.getView() == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) secondLast.getView().getParent();
        if (parent != null) {
            parent.removeView(secondLast.getView());
        }
        mPanel.addView(secondLast.getView(), getLayoutParams());
        Animation animation = ltrInAnimation();

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isInAnimation = true;
                vc.getView().setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isInAnimation = false;
                vc.getView().setEnabled(true);
                mPanel.removeView(vc.getView());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        secondLast.getView().startAnimation(animation);
    }

    public interface ActionSheetListener {
        void onDismiss(ActionSheet actionSheet, boolean isCancel);
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

    private Animation ltrInAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, -1, type, 0, type,
                0, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation ltrOutAnimation() {
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 1, type,
                0, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private View.OnClickListener mBackgroundViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener bgOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getCancelableOnTouchOutside()) {
                dismiss();
            }
        }
    };

    public boolean handleBack(@SuppressWarnings("unused") Activity a) {
        if (cancelable) {
            if (isShowing()) {
                if(getStackSize() > 1){
                    popViewController(vcStack.peekLast());
                    return true;//TODO
                }
                dismiss();
                return true;
            }
            return false;
        } else {
            return isShowing();
        }
    }
}