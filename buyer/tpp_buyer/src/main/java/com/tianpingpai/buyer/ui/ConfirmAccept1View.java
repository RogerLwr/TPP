package com.tianpingpai.buyer.ui;

import android.view.View;

import com.tianpingpai.buyer.R;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.tools.OrderStatusTool;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@ActionBar(hidden = true)
@Layout(id = R.layout.dialog_confirm_accept)
public class ConfirmAccept1View extends BaseViewController {

    private int payType;
    private long orderId;
    private Double orderMny;
    private boolean isCloseActivity;

    private OrderStatusTool orderStatusTool;

    public void setOrderStatusTool(OrderStatusTool orderStatusTool){
        this.orderStatusTool = orderStatusTool;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    private OrderModel orderModel;

    public void setIsCloseActivity(boolean isCloseActivity) {
        this.isCloseActivity = isCloseActivity;
    }

    public void setOrderInfo(int payType,long orderId, Double orderMny){
        this.orderId = orderId;
        this.orderMny = orderMny;
        this.payType = payType;
    }
    @Override
    protected void onConfigureView() {
        super.onConfigureView();
    }

    @OnClick(R.id.accept_button)
    private View.OnClickListener paymentOfflineButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConfirmAccept2View vc = new ConfirmAccept2View();
            if(orderId != 0){
                vc.setOrderInfo(payType);
            }
            vc.setStatusTool(orderStatusTool);
            vc.setOrderId(orderId);
            vc.setActivity(getActivity());
            vc.setIsCloseActivity(isCloseActivity);
            getViewTransitionManager().pushViewController(vc);
        }
    };

    @OnClick(R.id.pay_and_accept_button)
    private View.OnClickListener payOnlineButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ConfirmAccept3View vc = new ConfirmAccept3View();
            vc.setOrderInfo(orderId, orderMny);
            vc.setActivity(getActivity());
            getViewTransitionManager().pushViewController(vc);
        }
    };

    @OnClick(R.id.cancel_accept_button)
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActionSheet as = (ActionSheet)getViewTransitionManager();
            as.dismiss();
        }
    };
}
