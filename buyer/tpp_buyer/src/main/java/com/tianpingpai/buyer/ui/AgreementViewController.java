package com.tianpingpai.buyer.ui;

import android.view.View;
import android.webkit.WebView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Statistics(page = "协议")
@ActionBar(title = "合作协议")
@Layout(id = R.layout.ui_agreement)
public class AgreementViewController extends BaseViewController {

	@Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
		setActionBarLayout(R.layout.ab_title_white);
		WebView webView = (WebView) rootView.findViewById(R.id.webview);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setUseWideViewPort(false);
		webView.loadUrl("file:///android_asset/agreement.html");
		showContent();
	}
}
