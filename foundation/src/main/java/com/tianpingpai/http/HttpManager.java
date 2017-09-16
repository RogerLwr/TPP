package com.tianpingpai.http;

import com.tianpingpai.core.ModelManager;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.utils.SingletonFactory;

public class HttpManager extends ModelManager<HttpEvent, HttpResult<?>> {
	public static HttpManager getInstance(){
		return SingletonFactory.getInstance(HttpManager.class);
	}
}
