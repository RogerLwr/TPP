package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;

@Layout(id = R.layout.confirm_accept3_dialog)
@ActionBar(hidden = true)
public class ConfirmAccept3View extends BaseViewController {
    private long orderId;
    private Double orderMny;

    public void setOrderInfo(long orderId, Double orderMny) {
        this.orderId = orderId;
        this.orderMny = orderMny;
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        getView().findViewById(R.id.customerdialog_ok).setOnClickListener(payButtonListener);
        getView().findViewById(R.id.customerdialog_cancle).setOnClickListener(cancelButtonListener);
        ((TextView) getView().findViewById(R.id.tv_customerdialog_money)).setText(String.format("￥%s",PriceFormat.format(orderMny)));
    }

    private View.OnClickListener payButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent mIntent = new Intent(getActivity(), ContainerActivity.class);
            mIntent.putExtra(ContainerActivity.KEY_CONTENT, SelectPaymentViewController.class);
            //TODO
            mIntent.putExtra(SelectPaymentViewController.KEY_ORDER_ID, orderId + "");
            mIntent.putExtra(SelectPaymentViewController.KEY_GO_COMMENT,true);
            getActivity().startActivity(mIntent);

            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();

        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewTransitionManager().popViewController(ConfirmAccept3View.this);
        }
    };
}
