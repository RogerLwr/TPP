package com.tianpingpai.buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class SubOrdersAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new SubOrderViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    class SubOrderViewHolder implements ViewHolder<Model> {

        private View view;
        @Binding(id = R.id.store_name_text_view, format = "{{shopName}}")//TODO
        private TextView storeNameTextView;
        @Binding(id = R.id.order_id_text_view, format = "订单号:{{orderId}}")
        private TextView orderIdTextView;
        @Binding(id = R.id.amount_text_view, format = "¥{{mny | money}}")
        private TextView amountTextView;

        private Binder binder = new Binder();

        SubOrderViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_sub_order, null);
            binder.bindView(this, view);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
