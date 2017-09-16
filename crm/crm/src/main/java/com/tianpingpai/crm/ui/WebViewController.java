package com.tianpingpai.crm.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@Statistics(page = "网页")//TODO extra
@Layout(id = R.layout.fragment_web)
public class WebViewController extends CrmBaseViewController{

    public static final String KEY_ACTION_BAR_STYLE = "actionBarStyle";

    public static final int ACTIONBAR_STYLE_DEFAULT = 0;
    public static final int ACTIONBAR_STYLE_HIDDEN = 1;

	public static final String KEY_URL = "key.Url";

	@SuppressLint("SetJavaScriptEnabled")
    @Override
	protected void onConfigureView(View rootView) {
		super.onConfigureView(rootView);
        String url = getActivity().getIntent().getStringExtra(KEY_URL);
        int actionBarStyle = getActivity().getIntent().getIntExtra(KEY_ACTION_BAR_STYLE, ACTIONBAR_STYLE_DEFAULT);
        if(actionBarStyle == ACTIONBAR_STYLE_DEFAULT){
            setActionBarLayout(R.layout.ab_title_white);
        }else if(actionBarStyle == ACTIONBAR_STYLE_HIDDEN){
            hideActionBar();
        }
		WebView webView = (WebView) rootView.findViewById(R.id.web_view);
		Log.e("url:",url);
		webView.loadUrl(url);
		webView.getSettings().setJavaScriptEnabled(true);
		showLoading();
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				Log.e("xx", "p" + newProgress);
				if (newProgress == 100) {
					hideLoading();
					showContent();
					setTitle(view.getTitle());
				}
			}
		});

		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.e("xx","75------------url:" + url);
				if (url != null && url.contains("crm/app/customer/my/exit")) {
					getActivity().finish();
				}
				if (url != null && url.contains("fake:://exit")) {
					getActivity().finish();
				}

				/*else if(url != null && url.contains("tel:")){

				}*/
				else if(url != null && url.startsWith("tel:")){
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					WebViewController.this.getActivity().startActivity(intent);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});

	}
}
