package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.OrderStatusTool;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;

@Layout(id = R.layout.confirm_accept2_dialog)
@ActionBar(hidden = true)
public class ConfirmAccept2View extends BaseViewController {

    private boolean isCloseActivity;  //订单列表 则不关闭 订单详情进来 则关闭当前页面

    public void setIsCloseActivity(boolean isCloseActivity) {
        this.isCloseActivity = isCloseActivity;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;
    private int payType;

    public void setStatusTool(OrderStatusTool statusTool) {
        this.statusTool = statusTool;
    }

    private OrderStatusTool statusTool;

    public void setOrderInfo(int payType) {
        this.payType = payType;
    }

    private View confirmButton;

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        confirmButton = getView().findViewById(R.id.customerdialog_ok);
        confirmButton.setOnClickListener(confirmAcceptButtonListener);
        getView().findViewById(R.id.customerdialog_cancle).setOnClickListener(cancelButtonListener);
    }

    private View.OnClickListener confirmAcceptButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            confirmAccept();
            confirmButton.setClickable(false);
        }
    };

    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (payType == 0) {
                ActionSheet as = (ActionSheet) getViewTransitionManager();
                as.dismiss();
            } else {
                getViewTransitionManager().popViewController(ConfirmAccept2View.this);
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setAction(CommonUtil.COMPLETED_ACTION);
                bundle.putInt("orderState", CommonUtil.COMPLETE_WAIT_COMMENT);
                intent.putExtras(bundle);
                OrderModel orderModel = new OrderModel();
                orderModel.setId(orderId);
                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, orderModel);
                ActionSheet as = (ActionSheet) getViewTransitionManager();
                as.dismiss();
                TLog.e("xx", "92-----------isClose----"+isCloseActivity);
                if(getActivity() != null && isCloseActivity){
//                    getActivity().finish();
                }
                statusTool.getOrderStatus();
            }
        }
    };

    private void confirmAccept() {
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(URLUtil.ACCEPTORTHER_URL, listener);
        req.setMethod(HttpRequest.POST);
        ModelParser<Model> parser = new ModelParser<>(Model.class);
        req.setParser(parser);
        req.addParam("order_id", "" + orderId);
        req.addParam("user_type", "" + UserModel.USER_TYPE_BUYER);
        req.addParam("status", "" + CommonUtil.COMPLETE_WAIT_COMMENT);
        VolleyDispatcher.getInstance().dispatch(req);
    }
}
