package com.tianpingpai.seller.ui;


import android.view.View;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;

@ActionBar(titleRes = R.string.about_us)
@Layout(id = R.layout.ui_about)
public class AboutViewController extends BaseViewController{

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
    }
}
