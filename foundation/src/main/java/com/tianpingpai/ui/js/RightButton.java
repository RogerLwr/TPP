package com.tianpingpai.ui.js;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

public class RightButton {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    private TextView textView;

    @JavascriptInterface
    public void test(){
        Log.e("xx", "test");
    }

    @JavascriptInterface
    public void setText(final String text){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    @JavascriptInterface
    public void setImage(final String url){

    }
}
