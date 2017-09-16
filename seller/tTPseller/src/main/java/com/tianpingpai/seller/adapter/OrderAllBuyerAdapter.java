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
 * 所有下单客户(下单买家) 的适配器
 */
public class OrderAllBuyerAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new OrderBuyerViewHolder();
    }

    @SuppressWarnings("unused")
    private class OrderBuyerViewHolder implements ViewHolder<Model>{
        private View view;
        @Binding(id = R.id.buyer_name_text_view, format = "{{display_name}}")
        private TextView buyerNameTextView;
        @Binding(id = R.id.newly_order_time_text_view, format = "最近下单时间:{{latestTime}}")
        private TextView newlyOrderTimeTextView;
        @Binding(id = R.id.order_num_text_view, format = "本月下单数量:{{numOfOrder}}单")
        private TextView orderNumTextView;
        @Binding(id = R.id.order_mny_text_view, format = "本月消费金额:¥{{mnyOfOrder | money}}")
        private TextView orderMnyTextView;

        private LinearLayout containOrderBuyer;

        Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_order_all_buyer,null);
            binder.bindView(this, view);
            containOrderBuyer = (LinearLayout) view.findViewById(R.id.contain_order_buyer);
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
