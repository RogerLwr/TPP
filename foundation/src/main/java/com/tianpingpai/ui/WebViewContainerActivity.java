package com.tianpingpai.ui;

import android.os.Bundle;

/**
 * 针对网页 购物车修改商品数量，弹出键盘的时候页面没法上移
 */
public class WebViewContainerActivity extends FragmentContainerActivity {

	public static String KEY_CONTENT = "contentClass";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra(FragmentContainerActivity.KEY_FRAGMENT_CLASS,getIntent().getSerializableExtra(KEY_CONTENT));
		super.onCreate(savedInstanceState);
	}
}
