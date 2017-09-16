package com.tianpingpai.seller.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.model.OrderModel;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class OrdersAdapter extends ModelAdapter<OrderModel> {
    private class OrderViewHolder implements ViewHolder<OrderModel> {
        private View view;
        @Binding(id = R.id.order_id_text_view,format = "订单编号: {{order_id}}")
        private TextView orderIdTextView;
        @Binding(id = R.id.order_status_text_view)
        private TextView orderStatusTextView;
        @Binding(id = R.id.buyer_name_text_view)
        private TextView buyerNameTextView;
        @Binding(id = R.id.store_name_text_view,format = "店铺名称: {{b_shop_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.order_time_text_view,format = "下单时间:{{order_dt}}")
        private TextView orderTimeTextView;
        @Binding(id = R.id.deliver_time_text_view,format = "配送时间: {{deliver_dt}}")
        private TextView deliverTimeTextView;
        @Binding(id = R.id.deliver_type_text_view,format = "配送方式: {{deliver_name}}")
        private TextView deliverTypeTextView;
        @Binding(id = R.id.amount_text_view,format = "金额: ￥{{total_mny | money}}")
        private TextView amountTextView;
        @Binding(id = R.id.deliver_address_text_view,format = "配送地址: {{address}}")
        private TextView deliverAddressTextView;
        @Binding(id = R.id.pay_type_text_view)
        private TextView payTypeTextView;

        private Binder binder = new Binder();

        private OrderViewHolder(View v) {
            this.view = v;
            binder.bindView(this,view);
        }

        @Override
        public void setModel(OrderModel model) {
            binder.bindData(model);
            orderStatusTextView.setText(model.getStatusDescription());//TODO
            String buyerName = model.getReceiverName();
            int limit = 6;
            if(buyerName != null ){
                if(buyerName.length() > limit){
                    buyerName = buyerName.substring(0,limit - 1) + ".." + buyerName.substring(buyerName.length() - 1);
                }
            }
            if(buyerName != null){
                buyerNameTextView.setText("" + buyerName + "   " + model.getBuyerPhone());
            }else{
                buyerNameTextView.setText("" + model.getBuyerPhone());
            }
            payTypeTextView.setText(String.format("支付方式: %s", model.getPayMethodDesc()));
        }

        @Override
        public View getView() {
            return this.view;
        }
    }


    @SuppressLint("InflateParams")
    @Override
    protected com.tianpingpai.ui.ModelAdapter.ViewHolder<OrderModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new OrderViewHolder(inflater.inflate(R.layout.item_orders, null));
    }
}
