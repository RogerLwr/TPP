package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.ProductItemAdapter;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.tools.OrderStatusTool;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Statistics(page = "订单详情")
@ActionBar(title = "订单详情")
@Layout(id = R.layout.ui_order_detail)
public class OrderDetailViewController extends BaseViewController {
    public static final String KEY_ORDER_ID = "key.orderId";
    public static final String KEY_ORDER_PAY_DESC = "key.order_pay_desc";
    @Binding(id = R.id.store_name_text_view, format = "{{recevier_name}}")
    private TextView nameTextView;
    @Binding(id = R.id.phone_text_view, format = "{{telephone}}")
    private TextView phoneTextView;
    @Binding(id = R.id.address_edit_text, format = "{{address}}")
    private TextView addressTextView;

    @Binding(id = R.id.saler_tel_text_view, format = "商家电话: {{saler_phone}}")
    private TextView salerTelTV;
    @Binding(id = R.id.sale_name_prod_list_text_view, format = "{{sale_name}}")
    private TextView salerNameTV;
    @Binding(id = R.id.order_id_text_view, format = "订单编号: {{order_id}}")
    private TextView orderIdTextView;
    @Binding(id = R.id.order_status_text_view, format = "{{status_name}}")
    private TextView statusTextView;
    @Binding(id = R.id.sale_shop_name_text_view, format = "店铺名称: {{sale_name}}")
    private TextView buyerNameTextView;
    @Binding(id = R.id.deliver_type_text_view, format = "配送方式: {{deliver_name}}")
    private TextView deliverTypeTextView;
    @Binding(id = R.id.order_time_text_view, format = "下单时间: {{order_dt}}")
    private TextView orderTimeTextView;
    @Binding(id = R.id.deliver_time_text_view, format = "配送时间: {{deliver_dt}}")
    private TextView deliverTimeTextView;
    @Binding(id = R.id.remark_text_view, format = "买家留言: {{remark}}")
    private TextView remarkTextView;
    @Binding(id = R.id.amount_text_view, format = "￥{{prod_mny | money}}")
    private TextView amountTextView;
    @Binding(id = R.id.payment_type_text_view, format = "支付方式: {{_paymentType}}")
    private TextView payTypeTextView;

    @Binding(id = R.id.deliver_fee_text_view, format = "￥{{deliver_mny | money}}")
    private TextView deliverFeeTextView;
    @Binding(id = R.id.coupon_amount_text_view, format = "-￥{{coupon_mny | money}}")
    private TextView couponAmountTextView;
    @Binding(id = R.id.total_amount_text_view, format = "￥{{mny | money}}")
    private TextView totalAmountTextView;
    @Binding(id = R.id.has_coupon_text_view, format = " {{__hasCoupon}}")
    private TextView hasCouponTextView;

    @Binding(id = R.id.trade_order_mny_text_view, format = "已优惠{{coupon_mny | money}}元")
    private TextView tradeOrderMnyTextView;

    @Binding(id = R.id.operate_button)
    private TextView operateBtn;
    @Binding(id = R.id.action_button)
    private TextView actionBtn;

    @Binding(id = R.id.content_scroll_view)
    private ScrollView contentScrollView;

    private ProductItemAdapter adapter = new ProductItemAdapter();

    private long orderID;
    private int orderStatus;
    private Model orderModel;
    private FragmentActivity mActivity;
    private ModelStatusListener<ModelEvent, OrderModel> orderStatusListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            loadData();
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userLoginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if (event == UserEvent.Login && model != null) {
                loadData();
            }
        }
    };
    private Model model;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        mActivity = (FragmentActivity) a;
        orderID = a.getIntent().getLongExtra(KEY_ORDER_ID, -1);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView productListView = (ListView) rootView.findViewById(R.id.product_list_view);
        productListView.setAdapter(adapter);
        View bottomPanelContainer = rootView.findViewById(R.id.bottom_panel_container);
        UserManager.getInstance().registerListener(userLoginListener);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrderManager.getInstance().unregisterListener(orderStatusListener);
        UserManager.getInstance().unregisterListener(userLoginListener);
    }

    private void loadData() {
        String url = URLUtil.GETORDERFORMDES_URL;
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("order_id", orderID + "");
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new CommonErrorHandler(this));
        showLoading();
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();

            if (data.isSuccess()) {
                model = data.getModel();
                orderModel = model;
                adapter.setModels((ArrayList<Model>) model.getList("prod_list", Model.class));
                orderStatus = model.getInt("status");

                String paymentType = model.getInt("pay_type") == 0 ? "在线支付" : "货到付款";
                data.getModel().set("_paymentType", paymentType);
                String hasCoupon = model.getString("hasCoupon");
                model.set("__hasCoupon", hasCoupon);
                getBinder().bindData(data.getModel());

                String remark = model.getString("remark");
                if(TextUtils.isEmpty(remark)){
                    remarkTextView.setText("买家留言: (无)");
                }
                // 在bindData之后调用 否则 按钮没有文字
                setBtnByOrderStatus(orderStatus);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contentScrollView.scrollTo(0, 0);
                        showContent();
                    }
                }, 100);
            } else {
                ResultHandler.handleError(data, OrderDetailViewController.this);
            }
            showContent();
        }
    };

    private Handler handler = new Handler();

    private void setBtnByOrderStatus(int orderStatus) {
        Log.e("xx", "227-------orderStatus=" + orderStatus);
        switch (orderStatus) {
            case OrderModel.STATUS_NO_PAYED:
                operateBtn.setText("取消订单");
                actionBtn.setText("付款");
                break;
            case OrderModel.STATUS_PENDING:
            case OrderModel.STATUS_NOT_SENT:
                operateBtn.setText("取消订单");
                actionBtn.setVisibility(View.GONE);
                break;
            case OrderModel.STATUS_SENT:
                operateBtn.setText("取消订单");
                actionBtn.setText("确认收货");
                break;
            case OrderModel.STATUS_RECEIVED:
                operateBtn.setVisibility(View.GONE);
                actionBtn.setText("前去评价");
                if(orderModel.getInt("is_evalute")< 0){
                    actionBtn.setVisibility(View.GONE);
                }
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case OrderModel.STATUS_COMMENTED:
                operateBtn.setVisibility(View.GONE);
                actionBtn.setVisibility(View.GONE);
                break;

        }
    }

    @OnClick(R.id.operate_button)
    private View.OnClickListener onOperateBtnClickListener = new View.OnClickListener() {
        // 取消订单和订单追踪
        @Override
        public void onClick(View v) {

            int payType = model.getInt("pay_type");
            TLog.e("xx", "289-----------payType=" + payType);
            if(payType == OrderModel.PAY_TYPE_ON_LINE){
                final ActionSheetDialog dialog2 = new ActionSheetDialog();
                final String servicePhone = getActivity().getResources().getString(R.string.service_line);
                dialog2.setActionSheet(getActionSheet(true));
                dialog2.setTitle("目前在线支付的订单需要人工审核才能取消详情请拨打" + servicePhone);
                dialog2.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.CALL");
                        intent.setData(Uri.parse("tel:" + servicePhone));

                        getActivity().startActivity(intent);
                        dialog2.dismiss();
                    }
                });
                dialog2.setPositiveButtonText("拨打电话");
                dialog2.show();
                return;
            }


            switch (orderStatus) {
                case OrderModel.STATUS_NO_PAYED:
//                    operateBtn.setText("取消订单");
//                    actionBtn.setText("付款");
                case OrderModel.STATUS_PENDING:
                case OrderModel.STATUS_NOT_SENT:
//                    operateBtn.setText("取消订单");
//                    actionBtn.setVisibility(View.GONE);
                    final ActionSheetDialog dialog = new ActionSheetDialog();
                    dialog.setActionSheet(getActionSheet(true));
                    dialog.setTitle("确定要取消订单吗？");
                    dialog.setPositiveButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            delOrder(model);
                        }
                    });
                    dialog.show();
                    break;
                case OrderModel.STATUS_SENT:
//                    operateBtn.setText("取消订单");
//                    actionBtn.setText("确认收货");
                    final ActionSheetDialog dialog2 = new ActionSheetDialog();
                    final String servicePhone = getActivity().getResources().getString(R.string.service_line);
                    dialog2.setActionSheet(getActionSheet(true));
                    dialog2.setTitle("卖家已经发货,如需取消订单\n请拨打客服电话:" + servicePhone);
                    dialog2.setPositiveButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.CALL");
                            intent.setData(Uri.parse("tel:" + servicePhone));

                            getActivity().startActivity(intent);
                            dialog2.dismiss();
                        }
                    });
                    dialog2.setPositiveButtonText("拨打电话");
                    dialog2.show();
                    break;
                case OrderModel.STATUS_RECEIVED:
//                    operateBtn.setVisibility(View.GONE);
//                    actionBtn.setText("前去评价");
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case OrderModel.STATUS_COMMENTED:
                    break;

            }
        }
    };

    @OnClick(R.id.action_button)
    private View.OnClickListener actionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (orderStatus) {
                case OrderModel.STATUS_NO_PAYED:
                    if (model != null) {
                        Intent mIntent = new Intent(mActivity, ContainerActivity.class);
                        mIntent.putExtra(ContainerActivity.KEY_CONTENT, SelectPaymentViewController.class);
                        mIntent.putExtra(SelectPaymentViewController.KEY_ORDER_ID, model.getLong("order_id") + "");
                        mActivity.startActivity(mIntent);
                    }
                    break;
                case OrderModel.STATUS_PENDING:
                case OrderModel.STATUS_NOT_SENT:
                    operateBtn.setText("取消订单");
                    actionBtn.setVisibility(View.GONE);
                    break;
                case OrderModel.STATUS_SENT:
                    //确认收货
                    OrderModel m = new OrderModel();
                    m.setId(orderModel.getLong("order_id"));
                    m.setSaleName(orderModel.getString("sale_name"));
                    m.setSellerId(Long.parseLong(orderModel.getString("s_user_id")));
                    m.setId(orderModel.getLong("order_id"));
//                    BuyerApplication.orderModel = m;
                    OrderStatusTool tool = new OrderStatusTool(getActivity());
                    tool.closeView = true;
                    tool.setOrderModel(m);
                    tool.setModel(orderModel);
                    tool.setOrderId(orderModel.getLong("order_id") + "");
                    tool.setBaseViewController(OrderDetailViewController.this);

                    if (orderModel.getInt("pay_type") == 0) {
                        toConfirmAccept2View(orderModel.getInt("pay_type"),tool); //直接跳 确认收货
                    } else {
                        toConfirmAccept1View(orderModel.getInt("pay_type"), orderModel.getLong("order_id"), orderModel.getDouble("mny"),tool); //跳选择 的 确认收货的方式
                    }
                    break;
                case OrderModel.STATUS_RECEIVED:
                    //前去评价
                    if (model != null) {
                        Intent intent = new Intent(mActivity, ContainerActivity.class);
                        intent.putExtra(ContainerActivity.KEY_CONTENT, MakeCommentViewController.class);
                        intent.putExtra(MakeCommentViewController.KEY_ORDER_ID, model.getLong("order_id"));
                        intent.putExtra(MakeCommentViewController.KEY_STORE_NAME, model.getString("sale_name"));
                        intent.putExtra(MakeCommentViewController.KEY_SELLER_ID, model.getLong("s_user_id") + "");
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case OrderModel.STATUS_COMMENTED:
                    operateBtn.setVisibility(View.GONE);
                    actionBtn.setVisibility(View.GONE);
                    break;

            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> delOrderListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideSubmitting();
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    OrderModel orderModel = new OrderModel();
                    orderModel.setId(orderID);
                    OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, orderModel);
                    getActivity().finish();
                }
            } else {
                ResultHandler.handleError(data, OrderDetailViewController.this);
            }
        }
    };

    private void delOrder(Model model) {
        String url = URLApi.getBaseUrl() + "/api/order/buyer_cancel.json";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, delOrderListener);
        req.setParser(new GenericModelParser());
        req.setMethod(HttpRequest.POST);
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user != null) {
            req.addParam("user_id", user.getUserID());
        } else {
            Toast.makeText(ContextProvider.getContext(), "你未登录,请返回登录", Toast.LENGTH_LONG).show();
            return;
        }
        req.addParam("order_id", String.valueOf(model.getLong("order_id")));
        req.addParam("user_type", "" + UserModel.USER_TYPE_BUYER);
        VolleyDispatcher.getInstance().dispatch(req);
        showSubmitting();
    }

    private void toConfirmAccept1View(int payType, long orderId, Double orderMny, OrderStatusTool orderStatusTool) {
        ActionSheet actionSheet = getActionSheet(true);
        ConfirmAccept1View confirmAccept1View = new ConfirmAccept1View();
        confirmAccept1View.setOrderStatusTool(orderStatusTool);
        confirmAccept1View.setActivity(getActivity());
        confirmAccept1View.setOrderInfo(payType, orderId, orderMny);
        confirmAccept1View.setIsCloseActivity(true);
        actionSheet.setViewController(confirmAccept1View);
        actionSheet.show();

    }

    private void toConfirmAccept2View(int payType,OrderStatusTool tool) {
        ActionSheet actionSheet = getActionSheet(true);
        ConfirmAccept2View confirmAccept2View = new ConfirmAccept2View();
        confirmAccept2View.setOrderInfo(payType);
        confirmAccept2View.setStatusTool(tool);
        confirmAccept2View.setOrderId(orderID);
        confirmAccept2View.setIsCloseActivity(true);
        confirmAccept2View.setActivity(getActivity());
        actionSheet.setViewController(confirmAccept2View);
        actionSheet.show();
    }
}
