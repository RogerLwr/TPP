package com.tianpingpai.fragment;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.widget.QRCodeView;

public class QRCodeDisplayViewController extends BaseViewController {

    public static final String KEY_TEXT = "text";

    private String text;
    float originalBrightness;

    {
        setLayoutId(R.layout.fragment_qr_code);
    }

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        text = a.getIntent().getStringExtra(KEY_TEXT);

        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        originalBrightness = lp.screenBrightness;
        lp.screenBrightness = 0.92f;
        a.getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.screenBrightness = originalBrightness;
        a.getWindow().setAttributes(lp);
    }

    @Override
    public void didSetContentView(Activity a) {
        super.didSetContentView(a);
        setActionBarLayout(R.layout.ab_title_white);
        setTitle("二维码");
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        QRCodeView qrCodeView = (QRCodeView) rootView.findViewById(R.id.qr_code_view);
        qrCodeView.setData(text);
        qrCodeView.setData(text);
    }
}
