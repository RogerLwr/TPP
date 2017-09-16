package com.tianpingpai.viewController;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.ui.WebViewController;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.TextUtils;

@Layout(id = R.layout.vc_home_notification)
public class HomeNotificationViewController extends BaseViewController {

    @Binding(id = R.id.notification_text_view)
    private TextView notificationTV;
    @Binding(id = R.id.go_button)
    private TextView goBtn;

    String notificationStr;
    public void setNotificationStr(String notificationStr) {
        this.notificationStr = notificationStr;
    }

    String hrefString;

    public void setHrefString(String hrefString) {
        this.hrefString = hrefString;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        notificationTV.setText(notificationStr);
        Log.e("xx", "37-------href="+hrefString);
        if (TextUtils.isEmpty(hrefString) ){
            goBtn.setText("知道了");
        }else {
            goBtn.setText("去看看");
        }

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

    @SuppressWarnings("unused")
    @OnClick(R.id.go_button)
    private View.OnClickListener onGoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (goBtn.getText().toString().equals("知道了")){
                ActionSheet as = (ActionSheet)getViewTransitionManager();
                as.dismiss();
            }else {
                Intent i;
                i = new Intent(getActivity(), ContainerActivity.class);
                i.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                i.putExtra(WebViewController.KEY_URL, hrefString);
                getActivity().startActivity(i);
                ActionSheet as = (ActionSheet)getViewTransitionManager();
                as.dismiss();
            }
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
