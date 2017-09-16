package com.tianpingpai.pay.vc;

import android.view.View;
import android.widget.TextView;

import com.tianpingpai.pay.PayService;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.pay.R;
import com.tianpingpai.pay.alipay.AlipayPlatform;
import com.tianpingpai.pay.weixin.WeixinPayConfig;
import com.tianpingpai.pay.weixin.WeixinPlatform;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

public class PaySelectionViewController extends BaseViewController {

    private ActionSheet actionSheet;

    public void setCancelButtonListener(View.OnClickListener cancelButtonListener) {
        this.cancelButtonListener = cancelButtonListener;
    }

    private Payment payment;

    public PaySelectionViewController(){
        setLayoutId(R.layout._pay_ui_pay_selection);
    }

    @Override
    protected void onConfigureView() {
        getView().findViewById(R.id.cancel_button).setOnClickListener(cancelButtonListener);
        getView().findViewById(R.id.ab_close_button).setOnClickListener(cancelButtonListener);
        getView().findViewById(R.id.alipay_button).setOnClickListener(alipayButtonListener);
        getView().findViewById(R.id.weixin_pay_button).setOnClickListener(weixinPayButtonListener);
        TextView totalFeeTextView = (TextView) getView().findViewById(R.id.total_fee_text_view);
        if(payment != null) {
            totalFeeTextView.setText("支付金额:" + payment.getTotalFee());//TODO format
        }
    }

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            actionSheet.dismiss();
            //TODO close ongoing transition;
        }
    };

    private View.OnClickListener alipayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlipayPlatform alipayPlatform = PayService.getInstance().getAlipayPlatform();
            alipayPlatform.pay(payment,actionSheet.getActivity());
        }
    };

    private View.OnClickListener weixinPayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WeixinPayConfig config = new WeixinPayConfig();
            config.setAPIKEY("");
            config.setAppId("");

            WeixinPlatform weixinPlatform = PayService.getInstance().getWeixinPlatform();
            weixinPlatform.pay(payment,actionSheet.getActivity());
        }
    };

    //getters/setters
    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
