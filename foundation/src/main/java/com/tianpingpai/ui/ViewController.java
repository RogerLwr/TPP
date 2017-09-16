package com.tianpingpai.ui;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ViewController {
    void setActivity(FragmentActivity activity);

    ViewTransitionManager getViewTransitionManager();

    void setViewTransitionManager(ViewTransitionManager viewTransitionManager);
    void setLayoutId(int id);
    View getView();
    void setView(View view);
    int getLayoutId();

    void createView(LayoutInflater inflater, ViewGroup container);

    void animationFinished();

    void destroyView();

    void resume();

    void pause();

    FragmentActivity getActivity();
}
