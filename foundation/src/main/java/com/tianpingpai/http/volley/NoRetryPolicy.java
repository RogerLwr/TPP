package com.tianpingpai.http.volley;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

public class NoRetryPolicy implements RetryPolicy{

    private static NoRetryPolicy sInstance = new NoRetryPolicy();

    public static NoRetryPolicy getsInstance(){
        return sInstance;
    }

    @Override
    public int getCurrentTimeout() {
        return 10000;
    }

    @Override
    public int getCurrentRetryCount() {
        return 0;
    }

    @Override
    public void retry(VolleyError error) throws VolleyError {
        throw error;
    }
}
