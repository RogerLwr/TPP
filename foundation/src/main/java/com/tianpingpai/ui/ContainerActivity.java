package com.tianpingpai.ui;

import android.os.Bundle;

public class ContainerActivity extends FragmentContainerActivity {

	public static String KEY_CONTENT = "contentClass";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getIntent().putExtra(FragmentContainerActivity.KEY_FRAGMENT_CLASS,getIntent().getSerializableExtra(KEY_CONTENT));
		super.onCreate(savedInstanceState);
	}
}
