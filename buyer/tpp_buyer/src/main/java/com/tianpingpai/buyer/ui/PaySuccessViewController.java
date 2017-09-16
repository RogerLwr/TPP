package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.manager.NoticeManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@Layout(id = R.layout.ui_pay_success)
public class PaySuccessViewController extends BaseViewController{

    public static final String KEY_PAYED = "key.paid";

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        Toolbar toolbar = (Toolbar)(setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.toolbar));
        toolbar.setNavigationIcon(null);
        TextView payMsgTV = (TextView) rootView.findViewById(R.id.pay_msg_text_view);
        if(getActivity().getIntent().getBooleanExtra(KEY_PAYED,true)) {
            setTitle("支付成功");
        }else{
            setTitle("订单成功");
            payMsgTV.setText("订单成功! 接下来您可以:");
        }
        showContent();
        NoticeManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,new Model());
    }

    @OnClick(R.id.buy_more_button)
    private View.OnClickListener buyMoreButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(MainViewController.Key_pos, 0);
            i.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            getActivity().setResult(Activity.RESULT_OK, i);
            getActivity().startActivity(i);
            getActivity().finish();
        }
    };

    @OnClick(R.id.go_order_button)
    private View.OnClickListener goOrderBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrdersViewController.class);
            intent.putExtra(OrdersViewController.KEY_ORDER_FORM, "1,2"); //待收货
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    };

    @Override
    public boolean onBackKeyDown(Activity a) {
        return true;
    }
}
