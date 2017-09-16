package com.tianpingpai.tools;

import android.app.Activity;
import android.content.Intent;

import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.ui.MakeCommentViewController;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;

import java.util.List;

public class OrderStatusTool {

    private String orderId;

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    private OrderModel orderModel;

    public void setModel(Model model) {
        this.model = model;
    }

    private Model model;

    public boolean closeView;

    private Activity activity;

    private static BaseViewController baseViewController;

    public OrderStatusTool(Activity activity) {
        this.activity = activity;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setBaseViewController(BaseViewController bvc) {
        baseViewController = bvc;
    }

    public void getOrderStatus() {
        String url = URLApi.getBaseUrl() + "/api/order/is_evalute";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("order_id", orderId);
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        baseViewController.showSubmitting();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            baseViewController.hideSubmitting();
            TLog.e("data====", data.getModel().toString());
            if (data.isSuccess()) {
                int status = data.getModel().getInt("is_evalute");
                if (status < 0) {
                    if (closeView) {
                        activity.finish();
                    }
                } else {
                    goCommentView(activity, orderModel, model);
                }
            }
        }
    };

    public static void goCommentView(Activity activity, OrderModel orderModel, Model model) {
        if (orderModel != null) {
            Intent intent = new Intent(activity, ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, MakeCommentViewController.class);
            intent.putExtra(MakeCommentViewController.KEY_ORDER_ID, orderModel.getId());
            intent.putExtra(MakeCommentViewController.KEY_STORE_NAME, orderModel.getSaleName());
            intent.putExtra(MakeCommentViewController.KEY_SELLER_ID, orderModel.getSellerId() + "");
            activity.startActivity(intent);
            activity.finish();
        }else if(model != null){
            List<Model> orders = model.getList("orders", Model.class);
            if(orders.size()>0){
                Model m = orders.get(0);
                Intent intent = new Intent(activity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, MakeCommentViewController.class);
                intent.putExtra(MakeCommentViewController.KEY_ORDER_ID, Long.parseLong(m.getString("orderId")));
                intent.putExtra(MakeCommentViewController.KEY_STORE_NAME, m.getString("shopName") + "");
                intent.putExtra(MakeCommentViewController.KEY_SELLER_ID, m.getLong("shopId") + "");
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }
}
