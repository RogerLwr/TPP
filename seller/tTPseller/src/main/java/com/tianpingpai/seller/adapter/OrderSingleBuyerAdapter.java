package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

/**
 * 单个 下单客户(下单买家) 的适配器
 */
public class OrderSingleBuyerAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new OrderBuyerViewHolder();
    }

    @SuppressWarnings("unused")
    private class OrderBuyerViewHolder implements ViewHolder<Model>{
        private View view;

        @Binding(id = R.id.order_id_text_view, format = "订单编号: {{orderId}}")
        private TextView orderIdTextView;

        @Binding(id = R.id.status_text_view, format = "{{status}}")
        private TextView statusTextView;

        @Binding(id = R.id.deliver_type_text_view, format = "配送方式: {{delivery}}")
        private TextView deliverTypeTextView;

        @Binding(id = R.id.pay_status_text_view, format = "支付状态: {{payStatus}}")
        private TextView payStatusTextView;

        @Binding(id = R.id.deliver_time_text_view, format = "配送时间: {{deliveryDate}}")
        private TextView deliverTimeTextView;

        @Binding(id = R.id.address_text_view, format = "配送地址: {{deliveryAddress}}")
        private TextView addressTV;

        @Binding(id = R.id.amount_text_view, format = "¥{{prodMny}}")
        private TextView amountTV;

        @Binding(id = R.id.deliver_fee_text_view, format = "¥{{deliveryMny|money}}")
        private TextView deliverFeeTV;

        @Binding(id = R.id.coupon_amount_text_view, format = "-¥{{couponMny|money}}")
        private TextView couponAmountTextView;

        @Binding(id = R.id.total_amount_text_view, format = "¥{{mny|money}}")
        private TextView totalAmountTextView;

        @Binding(id = R.id.allowance_text_view, format = "含有平台补贴¥{{allowance|money}}")
        private TextView allowanceTV;

        @Binding(id = R.id.allowance_status_text_view, format = "{{allowanceStatus}}")
        private TextView allowanceStatusTV;

        private LinearLayout containOrderBuyer;

        Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_order_single_buyer,null);
            binder.bindView(this, view);
            containOrderBuyer = (LinearLayout) view.findViewById(R.id.contain_order_buyer);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
            if(model.getDouble("allowance") == 0){
                allowanceTV.setText("不含有平台补贴");
                allowanceStatusTV.setVisibility(View.GONE);
            }else{
                allowanceStatusTV.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
