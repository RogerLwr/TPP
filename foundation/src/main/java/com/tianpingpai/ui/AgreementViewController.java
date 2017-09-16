package com.tianpingpai.ui;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;

import com.tianpingpai.foundation.R;

public class AgreementViewController extends BaseViewController {

	{
		setLayoutId(R.layout.fragment_agreement);
	}

	@Override
	public void didSetContentView(Activity a) {
		super.didSetContentView(a);
		setActionBarLayout(R.layout.ab_title_white);
		setTitle("合作协议");
	}
	
	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		WebView webView = (WebView) rootView.findViewById(R.id.webview);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setUseWideViewPort(false);
		webView.loadUrl("file:///android_asset/agreement.html");
		showContent();
	}
}
