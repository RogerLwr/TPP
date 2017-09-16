package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class OrdersAdapter extends ModelAdapter<Model> {
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    private Activity activity;

    private CustomerModel customerModel;
    public void setCustomerModel(CustomerModel customerModel){
        this.customerModel = customerModel;
    }

    private int type = 0;
    public void setType(int type){
        this.type = type;
    }

    @SuppressWarnings("unused")
    private class OrderViewHolder implements ViewHolder<Model> {
        private View view;
        @Binding(id = R.id.order_id_text_view,format = "订单编号:{{order_id}}")
        private TextView orderIdTextView;
        @Binding(id = R.id.order_status_text_view,format = "{{status_show}}")
        private TextView orderStatusTextView;
        @Binding(id = R.id.buyer_name_text_view,format = "买家店铺:{{buyer_name}}")
        private TextView buyerNameTextView;
        @Binding(id = R.id.seller_name_text_view,format = "卖家姓名:{{s_user_name}}")
        private TextView sellerNameTextView;
        @Binding(id = R.id.store_name_text_view,format = "卖家店铺:{{sale_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.order_time_text_view,format = "下单时间:{{order_dt}}")
        private TextView orderTimeTextView;
        @Binding(id = R.id.deliver_type_text_view,format = "配送方式:{{deliver_name}}")
        private TextView deliverTypeTextView;
        @Binding(id = R.id.pay_type_text_view,format = "支付状态:{{pay_desc}}-{{pay_status}}")
        private TextView payTypeTextView;
        @Binding(id = R.id.order_amount_text_view,format = "订单金额:￥{{total_mny|money}}")
        private TextView orderAmountTextView;
        @Binding(id = R.id.amount_text_view,format = "支付金额:￥{{mny|money}}")
//        @Binding(id = R.id.amount_text_view)
        private TextView amountTextView;
        @Binding(id = R.id.buyer_manager_text_view)
        private TextView buyerManagerTextView;
        @Binding(id = R.id.seller_manager_text_view)
        private TextView sellerManagerTextView;
        @Binding(id = R.id.approve_button)
        private Model orderModel;

        private Binder binder = new Binder();

        OrderViewHolder(View v) {
            this.view = v;
            binder.bindView(this,this.view);
        }

        @Override
        public void setModel(Model model) {
            this.orderModel = model;
            binder.bindData(model);

            String sellerManagerName = model.getString("manage_s_name");
            if(TextUtils.isEmpty(sellerManagerName)){
                sellerManagerName = "无";
            }
            sellerManagerTextView.setText(sellerManagerName);

            String buyerManagerName = model.getString("manage_b_name");
            if(TextUtils.isEmpty(buyerManagerName)){
                buyerManagerName = "无";
            }
            buyerManagerTextView.setText(buyerManagerName);

            /*
            if(0==customerModel.getUserType()){
                String amountMny = "";
                if(1==customerModel.getGrade()){
                    amountMny= model.getString("mny");
                }else if(0==customerModel.getGrade()){
                    if(1==type){
                        amountMny= model.getString("mny");
                    }else{
                        amountMny= model.getString("total_mny");
                    }
                }
                amountTextView.setText("金额:￥"+amountMny);
            }else if(1==customerModel.getUserType()){
                String amountMny= model.getString("mny");
                amountTextView.setText("金额:￥"+amountMny);
            }
            */
        }

        @Override
        public View getView() {
            return this.view;
        }

    }

    @SuppressLint("InflateParams")
    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new OrderViewHolder(activity.getLayoutInflater().inflate(R.layout.item_order, null));
    }
}
