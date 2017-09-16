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
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;

@Layout(id = R.layout.ui_pay_success)
public class PaySuccessViewController extends BaseViewController {

    public static final String KEY_PAYED = "key.paid";
    TextView payMsgTV;
    private int payTypeScene = 0; //        场景支付时 区分跳转 0待发货和 1已完成订单
    public static final String KEY_PAY_TYPE = "Key.payTypeScene";
    private int orderTypeGroup = 0;
    public static final String KEY_ORDER_TYPE_GROUP = "key.OrderTypeGroup"; // 区分团购订单

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        Toolbar toolbar = (Toolbar)(setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.toolbar));
        toolbar.setNavigationIcon(null);
        payMsgTV = (TextView) rootView.findViewById(R.id.pay_msg_text_view);
        payTypeScene = getActivity().getIntent().getIntExtra(KEY_PAY_TYPE, 0);
        orderTypeGroup = getActivity().getIntent().getIntExtra(KEY_ORDER_TYPE_GROUP, 0);
        if(getActivity().getIntent().getBooleanExtra(KEY_PAYED,true)) {
            setTitle("支付成功");
        }else{
            setTitle("订单成功");
            payMsgTV.setText("订单成功! 接下来您可以:");
        }
        rootView.findViewById(R.id.buy_more_button).setOnClickListener(buyMoreButtonListener);
        rootView.findViewById(R.id.go_order_button).setOnClickListener(goOrderBtnListener);
        showContent();
    }

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
            String url;

            if(orderTypeGroup != 0){
                if(payTypeScene == 1){
                    url = String.format(URLApi.getWebBaseUrl() + "/groupbuy/upstream/orderlist?&delivery=3&accessToken=%s", UserManager.getInstance().getCurrentUser().getAccessToken());
                }else
                    url = String.format(URLApi.getWebBaseUrl() + "/groupbuy/upstream/orderlist?delivery=1&accessToken=%s", UserManager.getInstance().getCurrentUser().getAccessToken());
            }else{
                if(payTypeScene == 1){
                    url = String.format(URLApi.getWebBaseUrl() + "/saler/upstream/order/list?b_user_id=%s&delivery=3&accessToken=%s",UserManager.getInstance().getCurrentUser().getUserID(),UserManager.getInstance().getCurrentUser().getAccessToken());
                }else
                    url = String.format(URLApi.getWebBaseUrl() + "/saler/upstream/order/list?b_user_id=%s&delivery=1&accessToken=%s",UserManager.getInstance().getCurrentUser().getUserID(),UserManager.getInstance().getCurrentUser().getAccessToken());
            }

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
