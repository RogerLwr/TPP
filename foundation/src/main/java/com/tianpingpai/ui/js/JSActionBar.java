package com.tianpingpai.ui.js;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.web.WVJBWebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSActionBar {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ArrayList<RightButton> rightButtons = new ArrayList<>();

    public JSActionBar(){

    }

    private BaseViewController viewController;

    public void setWebView(WebView webView) {
        this.webView = webView;
//        webView.addJavascriptInterface(this,"actionBar");
    }

    public void setWebClient(WVJBWebViewClient client){
        client.registerHandler("__actionBar_hide", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                hide();
            }
        });

        client.registerHandler("__actionBar_show", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                show();
            }
        });

        client.registerHandler("__actionBar_setTitle", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                setTitle(data.toString());
            }
        });

        client.registerHandler("__actionBar_setRightButtons", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                JSONArray array = (JSONArray) data;
                setRightButtons(array);
            }
        });

        client.registerHandler("__button_setText", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                Log.e("xx","button.setText :" + data);
                JSONObject jobj = (JSONObject) data;
                try {
                    String name = jobj.getString("name");
                    String text = jobj.getString("text");
                    RightButton rightButton = getRightButton(name);
                    rightButton.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        client.registerHandler("toast", new WVJBWebViewClient.WVJBHandler() {
            @Override
            public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
                Toast.makeText(ContextProvider.getContext(),"" + data,Toast.LENGTH_LONG).show();
            }
        });
    }

    private WebView webView;

    public void setViewController(BaseViewController vc){
        this.viewController = vc;
    }

    @JavascriptInterface
    public void setTitle(final String title){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                viewController.setTitle(title);
            }
        });
    }

    @JavascriptInterface
    public void hide(){
        Log.e("jsab","hide");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                viewController.hideActionBar();
            }
        });
    }

    @JavascriptInterface
    public void show(){
        Log.e("jsab","show");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                viewController.showActionBar();
            }
        });
    }

    @JavascriptInterface
    public void setRightButtons(final String obj){
        Log.e("xx", "right:" + obj);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout rightButtonContainer = (LinearLayout) viewController.getView().findViewById(R.id.ab_right_button_container);
                rightButtonContainer.removeAllViews();
                rightButtons.clear();
                try {
                    JSONArray buttons = new JSONArray(obj);
                    for(int i = 0;i < buttons.length();i++){
                        JSONObject jobj = buttons.getJSONObject(i);
                        RightButton button = new RightButton();
                        TextView tv = (TextView) View.inflate(ContextProvider.getContext(), R.layout.view_ab_right_button,null);
                        button.setTextView(tv);
                        rightButtons.add(button);
                        button.setName(jobj.getString("name"));
                        button.setText(jobj.getString("text"));
                        tv.setTag(jobj.getString("name"));
                        tv.setClickable(true);
                        tv.setOnClickListener(rightButtonOnClickListener);

                        rightButtonContainer.addView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @JavascriptInterface
    public void setRightButtons(final JSONArray buttons){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout rightButtonContainer = (LinearLayout) viewController.getView().findViewById(R.id.ab_right_button_container);
                rightButtonContainer.removeAllViews();
                rightButtons.clear();
                try {
                    for(int i = 0;i < buttons.length();i++){
                        JSONObject jobj = buttons.getJSONObject(i);
                        RightButton button = new RightButton();
                        View rightButton = View.inflate(ContextProvider.getContext(), R.layout.view_ab_right_button,null);
                        TextView tv = (TextView) rightButton.findViewById(R.id.text_view);
                        button.setTextView(tv);
                        rightButtons.add(button);
                        button.setName(jobj.getString("name"));
                        button.setText(jobj.getString("text"));
                        tv.setTag(jobj.getString("name"));
                        tv.setClickable(true);
                        tv.setOnClickListener(rightButtonOnClickListener);

                        rightButtonContainer.addView(rightButton,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @JavascriptInterface
    public RightButton getRightButton(String name){
        for(RightButton rb:rightButtons){
            if(name.equals(rb.getName())){
                return rb;
            }
        }
        return null;
    }

    private View.OnClickListener rightButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = (String) v.getTag();
            Log.e("xx","name:"  + name);
            webView.loadUrl("javascript:actionBar_rightButtonCallback('" + name + "')");
        }
    };
}
