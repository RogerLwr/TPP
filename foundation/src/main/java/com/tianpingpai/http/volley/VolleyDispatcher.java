package com.tianpingpai.http.volley;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.BuildConfig;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.Parser;
import com.tianpingpai.http.OnPreDispatchListener;
import com.tianpingpai.http.RequestDispatcher;
import com.tianpingpai.http.util.ErrorReporter;
import com.tianpingpai.utils.NetworkUtil;
import com.tianpingpai.utils.SingletonFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VolleyDispatcher extends RequestDispatcher {
    private RequestQueue requestQueue;
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private OnPreDispatchListener mOnPreDispatchListener;

    public OnPreDispatchListener getOnPreDispatchListener() {
        return mOnPreDispatchListener;
    }

    public void setOnPreDispatchListener(
            OnPreDispatchListener mOnPreDispatchListener) {
        this.mOnPreDispatchListener = mOnPreDispatchListener;
    }

    public static VolleyDispatcher getInstance() {
        return SingletonFactory.getInstance(VolleyDispatcher.class);
    }

    public VolleyDispatcher() {
        requestQueue = Volley.newRequestQueue(ContextProvider.getContext());
        requestQueue.start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public <T> void dispatch(final HttpRequest<T> req) {
        if (mOnPreDispatchListener != null) {
            mOnPreDispatchListener.onPreDispatchRequest(req);
        }
        String url = req.getUrl();
        if(BuildConfig.DEBUG){
            Log.e("xx", "62----volley url===:" + url);
        }
        Log.e("xx", "65---volley url===:" + url);
        Log.e("xx", "header="+req.getHeaders());
        final long start = System.currentTimeMillis();
        StringRequest stringRequest = new StringRequest(req.getMethod(), url,
                new Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.e("data======",response);
                        String s = response;
                        mThreadPool.submit(new Runnable() {
                            @SuppressWarnings("unchecked")
                            @Override
                            public void run() {
                                Parser<T, String> parser = (Parser<T, String>) req
                                        .getParser();

                                long networkUsed = System.currentTimeMillis() - start;
                                HashMap<String,String> map = new HashMap<>();
                                map.put("volley","" + networkUsed);
                                map.put("type", NetworkUtil.getCurrentNetType());
                                MobclickAgent.onEvent(ContextProvider.getContext(), "http", map);

                                try {
                                    final T result = parser.parse(response);
                                    long time = System.currentTimeMillis() - start;
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                req.getListener().onResult(req,
                                                        result);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                throw e;
                                            }
                                        }
                                    }, Math.max(0, Math.abs(time - req.getMinLoadingTime())));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ErrorReporter.reportError(req,response,e);
                                }
                            }
                        });
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (req.getErrorListener() != null) {

                    long networkUsed = System.currentTimeMillis() - start;
                    HashMap<String,String> map = new HashMap<>();

                    final HttpError err = new HttpError();
                    err.setErrorMsg(error.getMessage());
                    if (error instanceof TimeoutError) {
                        err.setErrorMsg("网络超时！");
                        map.put("cause","timeout");
                    } else if (error instanceof NoConnectionError) {
                        err.setErrorMsg("连接服务器失败，请检查您的网络设置！");
                        map.put("cause", "no connection");
                    } else if (error instanceof ServerError) {
                        err.setErrorMsg("服务器异常！");
                        map.put("cause","server error");
                        if(error.networkResponse != null) {
                            map.put("code", "" + error.networkResponse.statusCode);
                        }
                        map.put("url",req.getUrl());
                    }
                    map.put("type", NetworkUtil.getCurrentNetType());
                    map.put("volley","" + networkUsed);
                    MobclickAgent.onEvent(ContextProvider.getContext(), "http", map);

                    err.setCause(error.getCause());
                    long time = System.currentTimeMillis() - start;
                    long delay = Math.max(0, Math.abs(time - req.getMinLoadingTime()));
                    if(delay > 200){
                        delay = 0;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            req.getErrorListener().onError(req, err);
                        }
                    },delay);
                    Log.e("xx", "errorDelay:" + delay);
                }

                Log.e("xx", "volley error:" + error);
                Log.e("xx", "volley url:" + req.getUrl());
            }
        });
        stringRequest.setParams(req.getParams());
        stringRequest.setHeaders(req.getHeaders());
        if (req.getMethod() == HttpRequest.POST) {
            stringRequest.setRetryPolicy(NoRetryPolicy.getsInstance());
        }
        requestQueue.add(stringRequest);
    }
}
