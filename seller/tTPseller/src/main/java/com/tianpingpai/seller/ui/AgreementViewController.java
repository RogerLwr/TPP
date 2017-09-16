package com.tianpingpai.seller.ui;

import android.annotation.SuppressLint;
import android.view.View;
import android.webkit.WebView;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.js.JSActionBar;

@Statistics(page = "合作协议")

@ActionBar(layout = R.layout.ab_web,title = "合作协议")
@Layout(id = R.layout.ui_agreement)
public class AgreementViewController extends BaseViewController {

	@SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		WebView webView = (WebView) rootView.findViewById(R.id.web_view);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setUseWideViewPort(false);
		webView.loadUrl("file:///android_asset/agreement.html");
		showContent();
        webView.getSettings().setJavaScriptEnabled(true);

		JSActionBar actionBar = new JSActionBar();
        actionBar.setViewController(this);
        actionBar.setWebView(webView);
		webView.addJavascriptInterface(actionBar,"actionBar");
	}
}
