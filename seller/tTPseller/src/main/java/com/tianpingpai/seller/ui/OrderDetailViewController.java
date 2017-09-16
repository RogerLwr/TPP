package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.ProductItemAdapter;
import com.tianpingpai.seller.dialog.ChangeValueDialog;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;

import java.util.ArrayList;

@SuppressWarnings("unused")
@ActionBar(title = "订单详情",rightText = "打印订单")
@Statistics(page = "订单详情")
@Layout(id = R.layout.ui_order_detail)
public class OrderDetailViewController extends BaseViewController{

    public static final String KEY_ORDER_ID = "key.orderId";
    public static final String KEY_ORDER_PAY_DESC = "key.order_pay_desc";
    @Binding(id = R.id.name_text_view, format = "{{recevier_name}}   {{telephone}}")
    private TextView nameTextView;
    @Binding(id = R.id.store_name_text_view, format = "店铺: {{b_shop_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.address_text_view, format = "地址: {{address}}")
    private TextView addressTextView;
    @Binding(id = R.id.order_id_text_view, format = "订单编号: {{order_id}}")
    private TextView orderIdTextView;
    @Binding(id = R.id.order_status_text_view, format = "{{status_name}}")
    private TextView statusTextView;
    @Binding(id = R.id.deliver_type_text_view, format = "配送方式: {{deliver_name}}")
    private TextView deliverTypeTextView;
    @Binding(id = R.id.order_time_text_view, format = "下单时间: {{order_dt}}")
    private TextView orderTimeTextView;
    @Binding(id = R.id.deliver_time_text_view, format = "配送时间: {{deliver_dt}}")
    private TextView deliverTimeTextView;

    @Binding(id = R.id.has_coupon_text_view, format = "使用优惠券: {{hasCoupon}}")
    private TextView hasCouponTextView;

    @Binding(id = R.id.remark_text_view, format = "{{remark}}")
    private TextView remarkTextView;
    @Binding(id = R.id.payment_type_text_view)
    private TextView payTypeTextView;
    @Binding(id = R.id.amount_text_view, format = "¥ {{prod_mny | money}}")
    private TextView amountTextView;
    @Binding(id = R.id.coupon_amount_text_view, format = "-¥ {{coupon_mny | money}}")
    private TextView couponAmountTextView;
    @Binding(id = R.id.youhui_mny_text_view, format = "-¥ {{youhui_mny | money}}")
    private TextView youHuiMnyTextView;
    @Binding(id = R.id.balance_text_view, format = "-¥ {{balance | money}}")
    private TextView balanceTextView;
    @Binding(id = R.id.deliver_fee_text_view, format = "¥ {{deliver_mny | money}}")
    private TextView deliverFeeTextView;

    @Binding(id = R.id.coupon_amount_container)
    private View couponAmountContainer;
    @Binding(id = R.id.youhui_mny_container)
    private View youHuiMnyContainer;
    @Binding(id = R.id.balance_container)
    private View balanceContainer;

    //    @Binding(id = R.id.total_amount_text_view, format = "￥: {{mny | money}}")
//    private TextView totalAmountTextView;

    @Binding(id = R.id.total_amount_text_view, format = "实付款: ￥{{act_mny | money}}")
    private TextView totalAmountTextView;

    private View bottomPanelContainer;

    @Binding(id = R.id.reject_button)
    private TextView rejectButton;

    @Binding(id = R.id.action_button)
    private TextView actionButton;
    @Binding(id = R.id.content_scroll_view)
    private ScrollView contentScrollView;
    @Binding(id = R.id.remark_container)
    private View remarkContainer;

    private ProductItemAdapter adapter = new ProductItemAdapter();

    private long orderID;
    private int orderStatus;
    private int payType;
    private Model orderModel;
    private TextView printButton;
    private ModelStatusListener<ModelEvent, OrderModel> orderStatusListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            loadData();
        }
    };

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener printButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(orderModel == null){
                return;
            }
            Intent printIntent = new Intent(getActivity(), ContainerActivity.class);
            printIntent.putExtra(ContainerActivity.KEY_CONTENT, PrintViewController.class);
            orderModel.set("tradeOrder", new Model());
            printIntent.putExtra(PrintViewController.KEY_CONTENT, orderModel.toJsonString());
            getActivity().startActivity(printIntent);
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
    private Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (orderModel != null) {
                printButtonListener.onClick(null);
            }
            return false;
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderID = a.getIntent().getLongExtra(KEY_ORDER_ID, -1);
        String payDesc = a.getIntent().getStringExtra(KEY_ORDER_PAY_DESC);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
//        View actionBarView = setActionBarLayout(R.layout.ab_title_white);
//        TextView printBtn = (TextView)actionBarView.findViewById(R.id.ab_right_button);
//        printBtn.setOnClickListener(printButtonListener);
        ListView productListView = (ListView) rootView.findViewById(R.id.product_list_view);
        productListView.setAdapter(adapter);

        bottomPanelContainer = rootView.findViewById(R.id.bottom_panel_container);
        UserManager.getInstance().registerListener(userLoginListener);
        loadData();
        OrderManager.getInstance().registerListener(orderStatusListener);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrderManager.getInstance().unregisterListener(orderStatusListener);
        UserManager.getInstance().unregisterListener(userLoginListener);
    }

    private void loadData() {
        String url = URLApi.GET_ORDER_DETAIL_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null && user.getGrade() == UserModel.GRADE_1){
            url = URLApi.SaleOrder.ORDER_DETAIL_INFO;
        }
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("order_id", orderID + "");
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            hideLoading();
            if (data.isSuccess()) {
                Model model = data.getModel();
                orderModel = model;

//                double totalAmount = model.getDouble("mny")-model.getDouble("youhui_mny")-model.getDouble("balance");
//                model.set("total_amount", totalAmount);

                getBinder().bindData(data.getModel());

                if(model.getDouble("coupon_mny") == 0){
                    couponAmountContainer.setVisibility(View.GONE);
                }

                if(model.getDouble("youhui_mny") == 0){
                    youHuiMnyContainer.setVisibility(View.GONE);
                }

                if(model.getDouble("balance") == 0){
                    balanceContainer.setVisibility(View.GONE);
                }

                adapter.setModels((ArrayList<Model>) model.getList("prod_list", Model.class));
                orderStatus = model.getInt("status");
                payType = model.getInt("pay_type");
                String paymentType = model.getInt("pay_type") == 0 ? "在线支付" : "货到付款";
                payTypeTextView.setText(String.format("付款方式: %s", paymentType));
                updateBottomPanel();//                printButton.setEnabled(true);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contentScrollView.scrollTo(0, 0);
                        showContent();
                    }
                }, 100);
                String remark = model.getString("remark");
                if (remark == null || TextUtils.isEmpty(remark.trim())) {
                    remarkContainer.setVisibility(View.GONE);
                }
            } else {
                ResultHandler.handleError(data, OrderDetailViewController.this);
            }
        }
    };

    private Handler handler = new Handler();

    private void updateBottomPanel() {
        if (orderStatus == CommonUtil.NOTACCEPT_SELLER) {
            bottomPanelContainer.setVisibility(View.VISIBLE);
            actionButton.setText(R.string.accept_and_send_order);
            rejectButton.setText(R.string.reject_order);
            rejectButton.setVisibility(View.VISIBLE);
        } else if (orderStatus == CommonUtil.NOT_DELEVIERGOODS) {
            bottomPanelContainer.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.GONE);
            actionButton.setText("完成发货");
        } else if (orderStatus == OrderModel.STATUS_SENT) {
            rejectButton.setVisibility(View.GONE);

            if (payType == 1) {
                actionButton.setVisibility(View.VISIBLE);
            } else {
                actionButton.setVisibility(View.GONE);
            }
            actionButton.setText(R.string.change_price);
        } else {
            bottomPanelContainer.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.reject_button)
    private View.OnClickListener rejectButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, RejectOrderViewController.class);
            intent.putExtra(RejectOrderViewController.KEY_ORDER_ID, orderID);
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.action_button)
    private View.OnClickListener actionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (orderStatus == CommonUtil.NOTACCEPT_SELLER) {
                setStatus(OrderModel.STATUS_SENT);//TODO
            } else if (orderStatus == OrderModel.STATUS_NOT_SENT) {
                setStatus(OrderModel.STATUS_SENT);//TODO
            } else if (orderStatus == OrderModel.STATUS_RECEIVED) {
                setStatus(OrderModel.STATUS_RECEIVED);//TODO
            } else if (orderStatus == OrderModel.STATUS_SENT) {
                showChangePriceDialog();
            }
        }
    };

    private void setStatus(int targetStatus) {
        String url = URLApi.ACCEPT_ORDER_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null && user.getGrade() == UserModel.GRADE_1){
            url = URLApi.SaleOrder.UPDATE_ORDER_STATUS;
        }
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, acceptListener);
        req.setMethod(HttpRequest.POST);

        req.addParam("order_id", String.valueOf(orderID));
        req.addParam("user_type", String.valueOf(UserModel.USER_TYPE_SELLER));
        req.addParam("status", String.valueOf(targetStatus));
        req.setErrorListener(new CommonErrorHandler(OrderDetailViewController.this));
        req.setParser(new ModelParser<>(Void.class));

        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private HttpRequest.ResultListener<ModelResult<Void>> acceptListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            hideLoading();
            if (data.isSuccess()) {
                OrderModel orderModel = new OrderModel();
                orderModel.setId(orderID);
                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate, orderModel);
                final ActionSheetDialog dialog = new ActionSheetDialog();
                dialog.setActionSheet(getActionSheet(true));
                getActionSheet(true).setCancelable(false);
                dialog.setTitle("您已经确认订单\n" +
                        "请按照买家要求时间进行配送");
                dialog.setPositiveButtonText("关闭");
                dialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
                dialog.setCancelButtonHidden(true);
                dialog.show();

            } else {
                ResultHandler.handleError(data, OrderDetailViewController.this);
            }
        }
    };

    private void showChangePriceDialog() {
        ChangeValueDialog cvd = new ChangeValueDialog();
        cvd.setOrderID(orderID);
        cvd.show(getActivity().getSupportFragmentManager(), "");
    }
}
