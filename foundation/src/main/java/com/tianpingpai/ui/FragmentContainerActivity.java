package com.tianpingpai.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.tianpingpai.foundation.R;
import com.umeng.analytics.MobclickAgent;

public class FragmentContainerActivity extends FragmentActivity {

    public static final String KEY_FRAGMENT_CLASS = "key.FragmentClass";

    public interface FragmentContainerCallback {
        Class<?> onFirstActivityCreated(FragmentContainerActivity a);
    }

    public static void setFragmentContainerCallback(FragmentContainerCallback c) {
        sCallback = c;
    }

    private static FragmentContainerCallback sCallback;
    private Fragment mFragment;
    private ViewController mViewController;
    private ActivityEventListener mEventListener;
    private Statistics mStatistics;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> contentClass = (Class<?>) getIntent().getSerializableExtra(KEY_FRAGMENT_CLASS);
        Object content = null;
        if (contentClass == null) {
            if (sCallback != null) {
                contentClass = sCallback.onFirstActivityCreated(this);
            }
        }
        if (contentClass != null) {
            try {
                content = contentClass.newInstance();
                if (content instanceof Fragment) {
                    mFragment = (Fragment) content;
                } else if (content instanceof ViewController) {
                    mViewController = (ViewController) content;
                    mViewController.setActivity(this);
                }
            } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (content == null) {
            throw new RuntimeException();
        }

        mStatistics = content.getClass().getAnnotation(Statistics.class);
        if (content instanceof ActivityEventListener) {
            mEventListener = (ActivityEventListener) content;
        }

        if (mEventListener != null) {
            mEventListener.onActivityCreated(this);
            mEventListener.willSetContentView(this);
        }

        if (mFragment != null) {
            setContentView(R.layout.activity_main);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).commit();
        }

        if (mViewController != null) {
            mViewController.setActivity(this);
            mViewController.createView(getLayoutInflater(), null);
            setContentView(mViewController.getView());
        }

        if (mEventListener != null) {
            mEventListener.didSetContentView(this);
        }

        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStatistics != null) {
            MobclickAgent.onPageStart(mStatistics.page());
            MobclickAgent.onResume(this);
        }
        mEventListener.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mStatistics != null) {
            MobclickAgent.onPageEnd(mStatistics.page());
            MobclickAgent.onPause(this);
        }
        mEventListener.onActivityPaused(this);
    }

    @Override
    protected void onDestroy() {
        if (mEventListener != null) {
            mEventListener.onActivityDestroyed(this);
        }
        if (mFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mFragment).commitAllowingStateLoss();
        }

        mFragment = null;
        mViewController = null;
        mEventListener = null;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEventListener != null) {
                boolean result = mEventListener.onBackKeyDown(this);
                if (!result) {
                    finish();
                }
                return result;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("xx", "mLi:" + mEventListener);
        if (mEventListener != null) {
            mEventListener.onActivityResult(this, requestCode, resultCode, data);
        }
    }
}
