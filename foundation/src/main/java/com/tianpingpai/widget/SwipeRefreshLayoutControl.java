package com.tianpingpai.widget;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeRefreshLayoutControl {
    private SwipeRefreshLayout refreshLayout;
    public void setSwipeRefreshLayout(SwipeRefreshLayout refreshLayout){
        this.refreshLayout = refreshLayout;
        if(onRefreshListener != null){
            this.refreshLayout.setOnRefreshListener(onRefreshListener);
        }
    }

    public SwipeRefreshLayout getSwipeRefreshLayout(){
        return refreshLayout;
    }

    private Handler handler = new Handler();

    public void triggerRefresh(){
        if(refreshLayout == null){
            return;
        }
        if(refreshLayout.isRefreshing()){
            return;
        }
        refreshLayout.setRefreshing(true);
        if(onRefreshListener != null){
            onRefreshListener.onRefresh();
        }
    }

    public void triggerRefreshDelayed(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerRefresh();
            }
        },500);
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        this.onRefreshListener = listener;
        if(refreshLayout != null){
            refreshLayout.setOnRefreshListener(listener);
        }
    }
}
