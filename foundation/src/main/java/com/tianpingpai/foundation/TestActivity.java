package com.tianpingpai.foundation;

import android.app.Activity;
import android.os.Bundle;

public class TestActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
//		HttpRequest<ListResult<String>> req = new HttpRequest<ListResult<String>>("http://www.baidu.com", new ResultListener<ListResult<String>>() {
//			@Override
//			public void onResult(HttpRequest<ListResult<String>> request,
//					HttpResult<T> data) {
//				
//			}
//		});
		
//		ListParser<String> p = new ListParser<String>(String.class);
//		req.setParser(p);
//		VolleyDispatcher vd = new VolleyDispatcher();
//		vd.setContext(getApplicationContext());
//		vd.dispatch(req);
	}
}
