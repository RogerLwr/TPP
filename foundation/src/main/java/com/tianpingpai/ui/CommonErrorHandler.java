package com.tianpingpai.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;

public class CommonErrorHandler implements HttpRequest.ErrorListener {
	
	public CommonErrorHandler(BaseViewController f){
		this.vc = f;
	}

	private BaseViewController vc;

	SwipeRefreshLayout swipeLayout;
	
	public void setSwipeRefreshLayout(SwipeRefreshLayout sl){
		this.swipeLayout = sl;
	}
	
	@Override
	public void onError(HttpRequest<?> request, HttpError error) {
		Toast.makeText(
				ContextProvider.getContext(),
				"" + error.getErrorMsg(),
				Toast.LENGTH_SHORT).show();
		if(swipeLayout != null){
			swipeLayout.setRefreshing(false);
		}
		vc.hideLoading();
		vc.hideSubmitting();
		vc.showNetworkError();
	}
}
