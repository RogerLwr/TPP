package com.tianpingpai.http;

public abstract class RequestDispatcher {
	public abstract<T> void dispatch(HttpRequest<T> r);
}
