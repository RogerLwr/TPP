package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@Layout(id = R.layout.view_controller_status)
public class StatusViewController extends BaseViewController {

    public static final String KEY_PAYED = "key.paid";
    TextView payMsgTV;

    Binder binder = new Binder();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        binder.bindView(this, rootView);

        Toolbar toolbar = (Toolbar)(setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.toolbar));
        toolbar.setNavigationIcon(null);
        payMsgTV = (TextView) rootView.findViewById(R.id.pay_msg_text_view);
        if(getActivity().getIntent().getBooleanExtra(KEY_PAYED,true)) {
            setTitle("提交成功");
        }else{
            setTitle("提交失败");
            payMsgTV.setText("订单成功! 接下来您可以:");
        }
        rootView.findViewById(R.id.buy_more_button).setOnClickListener(buyMoreButtonListener);
        rootView.findViewById(R.id.go_order_button).setOnClickListener(goOrderBtnListener);
        showContent();
    }

    @OnClick(R.id.operate_button)
    View.OnClickListener operateButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener buyMoreButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(i);
            getActivity().finish();
        }
    };
    private View.OnClickListener goOrderBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent nextIntent = new Intent(getActivity(), ContainerActivity.class);
            nextIntent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
            String url = String.format(URLApi.getWebBaseUrl() + "/saler/upstream/order/list?b_user_id=%s&delivery=1?accessToken=%s",UserManager.getInstance().getCurrentUser().getUserID(),UserManager.getInstance().getCurrentUser().getAccessToken());

            nextIntent.putExtra(WebViewController.KEY_URL,url);
            nextIntent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
            getActivity().startActivity(nextIntent);
            getActivity().finish();
        }
    };

    @Override
    public boolean onBackKeyDown(Activity a) {
        return true;
    }
}
