package com.tianpingpai.buyer.ui;


import android.view.View;

import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@ActionBar(titleRes = R.string.about_us)
@Statistics(page = "关于我们")
@Layout(id = R.layout.ui_about)
public class AboutViewController extends BaseViewController {

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
    }
}
