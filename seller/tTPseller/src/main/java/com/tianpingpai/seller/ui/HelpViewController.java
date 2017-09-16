package com.tianpingpai.seller.ui;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@ActionBar(title = "帮助")
@Statistics(page = "帮助")
@Layout(id = R.layout.ui_help)
public class HelpViewController extends BaseViewController{

    @Binding(id = R.id.help_opera)
    private LinearLayout operaLinear;
    @Binding(id = R.id.help_service)
    private LinearLayout serviceLinear;
    @Binding(id = R.id.help_addservice)
    private LinearLayout addServiceLinear;

    private TextView operaTextView, serviceTextView, addServiceTextView;

    private View.OnClickListener operaClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            operaTextView.setVisibility(View.VISIBLE);
            serviceTextView.setVisibility(View.GONE);
            addServiceTextView.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener serviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            operaTextView.setVisibility(View.GONE);
            serviceTextView.setVisibility(View.VISIBLE);
            addServiceTextView.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener addServiceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            operaTextView.setVisibility(View.GONE);
            serviceTextView.setVisibility(View.GONE);
            addServiceTextView.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        operaTextView = (TextView) rootView.findViewById(R.id.help_opera_content_text_view);
        serviceTextView = (TextView) rootView.findViewById(R.id.help_service_content);
        addServiceTextView = (TextView) rootView.findViewById(R.id.help_addservice_content);

        operaLinear.setOnClickListener(operaClickListener);
        serviceLinear.setOnClickListener(serviceClickListener);
        addServiceLinear.setOnClickListener(addServiceClickListener);
    }
}
