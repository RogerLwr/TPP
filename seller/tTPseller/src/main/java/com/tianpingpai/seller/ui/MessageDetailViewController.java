package com.tianpingpai.seller.ui;

import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;

@ActionBar(title = "消息内容")
@Statistics(page = "消息详情")
@Layout(id = R.layout.ui_message_detail)
public class MessageDetailViewController extends BaseViewController {

    public static final String KEY_TITLE = "key.title";
    public static final String KEY_CONTENT = "key.content";

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
        TextView contentTextView = (TextView) rootView.findViewById(R.id.content_text_view);
        titleTextView.setText(getActivity().getIntent().getStringExtra(KEY_TITLE));
        contentTextView.setText(getActivity().getIntent().getStringExtra(KEY_CONTENT));
        showContent();
    }
}
