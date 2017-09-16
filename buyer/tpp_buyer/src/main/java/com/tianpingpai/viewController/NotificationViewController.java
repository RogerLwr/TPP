package com.tianpingpai.viewController;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;

import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@Layout(id = R.layout.vc_notification)
public class NotificationViewController extends BaseViewController {

    @Binding(id = R.id.notification_text_view)
    private TextView notificationTV;

    String notificationStr;

    public void setNotificationStr(String notificationStr) {
        this.notificationStr = notificationStr;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        notificationTV.setText(notificationStr);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.close_button)
    private View.OnClickListener onCloseClickListener = new View.OnClickListener() {
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
