package com.tianpingpai.http;

public interface OnPreDispatchListener {
    //
	void onPreDispatchRequest(HttpRequest<?> req);
}
