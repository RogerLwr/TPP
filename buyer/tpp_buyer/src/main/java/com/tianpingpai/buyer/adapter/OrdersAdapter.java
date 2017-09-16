package com.tianpingpai.buyer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.ui.ConfirmAccept1View;
import com.tianpingpai.buyer.ui.ConfirmAccept2View;
import com.tianpingpai.buyer.ui.LoginViewController;
import com.tianpingpai.buyer.ui.MakeCommentViewController;
import com.tianpingpai.buyer.ui.OrderDetailViewController;
import com.tianpingpai.buyer.ui.OrderTrackViewController;
import com.tianpingpai.buyer.ui.SelectPaymentViewController;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.tools.OrderStatusTool;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;

public class OrdersAdapter extends ModelAdapter<OrderModel> {
    FragmentActivity mActivity;

    private BaseViewController baseViewController;

    public void setBaseViewController(BaseViewController baseViewController){
        this.baseViewController = baseViewController;
    }

    public void setActivity(FragmentActivity a) {
        this.mActivity = a;
    }

    @SuppressWarnings("unused")
    private class OrderViewHolder implements ViewHolder<OrderModel> {

        private OrderModel model;
        private Binder binder = new Binder();

        private View view;
        @Binding(id = R.id.order_id_text_view,format = "订单号:{{order_id}}")
        private TextView orderIdTextView;
        @Binding(id = R.id.order_status_text_view)
        private TextView orderStatusTextView;
        @Binding(id = R.id.sale_shop_name_text_view,format = "买家姓名:{{b_shop_name}}")
        private TextView buyerNameTextView;
        @Binding(id = R.id.store_name_text_view,format = "店铺名称:{{sale_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.order_time_text_view,format = "下单时间:{{order_dt}}")
        private TextView orderTimeTextView;
        @Binding(id = R.id.deliver_time_text_view,format = "配送时间:{{deliver_dt}}")
        private TextView deliverTimeTextView;
        @Binding(id = R.id.deliver_type_text_view,format = "配送方式:{{deliver_name}}")
        private TextView deliverTypeTextView;
        @Binding(id = R.id.amount_text_view,format = "支付金额:￥{{mny|money}}(含运费¥{{deliver_mny|money}})")
        private TextView amountTextView;
        @Binding(id = R.id.pay_desc_text_view,format = "支付方式: {{__payMethod}}")
        private TextView payDescTextView;
        @Binding(id = R.id.left_button)
        private TextView leftButton;
        @Binding(id = R.id.right_button)
        private TextView rightButton;
        private View contentDividerLine;


        OrderViewHolder(View v) {
            this.view = v;
            binder.bindView(this,view);
            rightButton = (TextView) v.findViewById(R.id.right_button);
            leftButton = (TextView) v.findViewById(R.id.left_button);
            contentDividerLine = v.findViewById(R.id.content_divider_line);
            leftButton.setOnClickListener(orderformOpera2Listener);
        }

        private View.OnClickListener orderformOpera2Listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, MakeCommentViewController.class);
                intent.putExtra(MakeCommentViewController.KEY_ORDER_ID, model.getId());
                intent.putExtra(MakeCommentViewController.KEY_STORE_NAME, model.getSaleName());
                intent.putExtra(MakeCommentViewController.KEY_SELLER_ID, model.getSellerId() + "");
                mActivity.startActivity(intent);
            }
        };

        private View.OnClickListener confirmReceivedButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderStatusTool tool = new OrderStatusTool(getActivity());
                tool.setOrderModel(model);
                tool.setBaseViewController(baseViewController);
                tool.setOrderId(model.getId()+ "");
                if (model.getPayType() == 0) {
//                    OrderStatusTool tool = new OrderStatusTool(getActivity());
//                    tool.setOrderModel(model);
//                    tool.setBaseViewController(baseViewController);
//                    tool.setOrderId(model.getId()+ "");
                    //TODO
                    toConfirmAccept2View(model,tool);
                } else {
                    toConfirmAccept1View(model,tool);
                }
            }
        };

        @Override
        public void setModel(final OrderModel model) {
            this.model = model;
            binder.bindData(model);
            orderStatusTextView.setText(model.getStatusDescription());//TODO
            String payMethod = "支付方式: " + (model.getPayType()== 0 ? "在线支付":"货到付款");
            payDescTextView.setText(payMethod);
            int status = model.getStatus();
            if (status == OrderModel.STATUS_RECEIVED || status == OrderModel.STATUS_SENT) {
                leftButton.setVisibility(View.VISIBLE);
            } else {
                leftButton.setVisibility(View.GONE);
            }

            if (status == CommonUtil.COMPLETE_WAIT_COMMENT) {
                int commentStatus = model.getCommentStatus();
                if(commentStatus < 0){
                    leftButton.setVisibility(View.GONE);
                }
                leftButton.setText("前去评价");
                leftButton.setTag(model.getId());
            }
            if(status == OrderModel.STATUS_PENDING || status == OrderModel.STATUS_NOT_SENT){
                contentDividerLine.setVisibility(View.GONE);
            }

            leftButton.setOnClickListener(orderformOpera2Listener);
            if (status == CommonUtil.NOT_PAY_MONEY) {
                rightButton.setText("付款");
                rightButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(mActivity, ContainerActivity.class);
                        mIntent.putExtra(ContainerActivity.KEY_CONTENT, SelectPaymentViewController.class);
                        mIntent.putExtra(SelectPaymentViewController.KEY_ORDER_ID, model.getId() + "");

                        mActivity.startActivity(mIntent);
                    }
                });

            } else if (status == CommonUtil.WAIT_ACCEPT_GOODS) {
                leftButton.setText("确认收货");
                leftButton.setOnClickListener(confirmReceivedButtonListener);
                rightButton.setText("订单跟踪");
                rightButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ContainerActivity.class);
                        if (UserManager.getInstance().isLoggedIn()) {
                            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderTrackViewController.class);
                            intent.putExtra(OrderTrackViewController.KEY_ORDER_ID, model.getId());
                            intent.putExtra(OrderTrackViewController.KEY_SALER_PHONE, model.getSellerPhone());
                            intent.putExtra(OrderTrackViewController.KEY_SALER_TIME, model.getOrderTime());
                        } else {
                            intent = new Intent(getActivity(), ContainerActivity.class);
                            intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                        }
                        mActivity.startActivity(intent);
                    }
                });
            } else {
                rightButton.setVisibility(View.GONE);
                rightButton.setText("查看订单");
//                rightButton.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.btn_order_bg));
                rightButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ContainerActivity.class);
                        if (UserManager.getInstance().isLoggedIn()) {
                            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderDetailViewController.class);
                            intent.putExtra(OrderDetailViewController.KEY_ORDER_ID, model.getId());
                        } else {
                            intent = new Intent(getActivity(), ContainerActivity.class);
                            intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                        }
                        getActivity().startActivity(intent);
                    }

                });
            }
        }

        @Override
        public View getView() {
            return this.view;
        }
    }


    private void toConfirmAccept1View(OrderModel orderModel,OrderStatusTool orderStatusTool) {

        ActionSheet actionSheet = new ActionSheet();
        ConfirmAccept1View confirmAccept1View = new ConfirmAccept1View();
        confirmAccept1View.setOrderStatusTool(orderStatusTool);
        confirmAccept1View.setOrderInfo(orderModel.getPayType(), orderModel.getId(), orderModel.getAmount());
        confirmAccept1View.setActivity(mActivity);
        confirmAccept1View.setIsCloseActivity(false);
        Log.e("xx", "mActivity::" + mActivity);
        actionSheet.setViewController(confirmAccept1View);
        actionSheet.show();

    }

    private void toConfirmAccept2View(OrderModel orderModel, OrderStatusTool tool) {
        ActionSheet actionSheet = new ActionSheet();
        ConfirmAccept2View confirmAccept2View = new ConfirmAccept2View();
        confirmAccept2View.setStatusTool(tool);
        confirmAccept2View.setOrderInfo(orderModel.getPayType());
        confirmAccept2View.setOrderId(orderModel.getId());
        confirmAccept2View.setActivity(mActivity);
        confirmAccept2View.setIsCloseActivity(false);
        actionSheet.setViewController(confirmAccept2View);
        actionSheet.show();
    }

    private Activity getActivity() {
        return mActivity;
    }

    @SuppressLint("InflateParams")
    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<OrderModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new OrderViewHolder(inflater.inflate(R.layout.item_orders, null));
    }
}
