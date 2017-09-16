package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@Layout(id = R.layout.vc_notification)
public class NotificationViewController extends BaseViewController {

    @Binding(id = R.id.title_text_view)
    private TextView titleTextView;
    @Binding(id = R.id.notification_text_view)
    private TextView notificationTV;

    String notificationStr;
    String title;

    public void setNotificationStrAndTitle(String notificationStr, String title) {
        this.notificationStr = notificationStr;
        this.title = title;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        titleTextView.setText(title);
        notificationTV.setText(notificationStr);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.close_button)
    View.OnClickListener onCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActionSheet as = (ActionSheet)getViewTransitionManager();
            as.dismiss();
        }
    };

    @Override
    public boolean onBackKeyDown(Activity a) {
        ActionSheet as = (ActionSheet)getViewTransitionManager();
        if(as.isShowing()){
            as.dismiss();
            return true;
        }
        return false;
    }
}
