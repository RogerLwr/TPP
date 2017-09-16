package com.tianpingpai.http.volley;

import java.util.Map;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class StringRequest extends Request<String> {

	private Map<String, String> params = null;

	private Listener<String> responseListener;

	private Map<String,String> responseHeaders;

	public Map<String,String> getResponseHeaders(){
		return responseHeaders;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	private Map<String, String> headers;

	public StringRequest(int method, String url,Listener<String> responseListener, ErrorListener listener) {
		super(method, url, listener);
		this.responseListener = responseListener;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		responseHeaders = response.headers;
		return Response.success(new String(response.data), HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		responseListener.onResponse(response);
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		Log.e("xx","params" + params);
		return params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if(headers != null){
			return headers;
		}
		return super.getHeaders();
	}

	public void setParams(Map<String, String> params){
		this.params = params;
	}
}
