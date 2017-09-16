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
import com.tianpingpai.seller.ui.IncomeViewController;
import com.tianpingpai.ui.ModelAdapter;

public class IncomeMnyAdapter extends ModelAdapter<Model> {

    public void setArrived(boolean isArrived) {
        this.isArrived = isArrived;
    }

    /**
     * 是否到账
     */
    private boolean isArrived = false;

    public IncomeMnyAdapter() {

    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new IncomeMnyViewHolder();
    }

    @SuppressWarnings("unused")
    private class IncomeMnyViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.type_text_view, format = "{{categoryName}}")
        private TextView typeTextView;

        @Binding(id = R.id.src_name_text_view, format = "{{srcName}}")
        private TextView srcNameTextView;
        @Binding(id = R.id.src_name_tag_text_view)
        private TextView srcNameTagTextView;

        @Binding(id = R.id.src_name_contain)
        private LinearLayout srcNameContain;

        @Binding(id = R.id.amount_text_view, format = "¥{{fee|money}}")
        private TextView amountTextView;

        @Binding(id = R.id.order_id_text_view, format = "{{orderId}}")
        private TextView orderIdTextView;

        @Binding(id = R.id.order_id_contain)
        private LinearLayout orderIdContain;
        @Binding(id = R.id.consumed_date_text_view, format = "{{consumptionDate}}")
        private TextView consumedDateTextView;

        @Binding(id = R.id.consumed_date_contain)
        private LinearLayout consumedDateContain;
        @Binding(id = R.id.pay_type_text_view, format = "支付状态: {{orderStatus}}")
        private TextView payTypeTextView;

        @Binding(id = R.id.arrived_date_text_view, format = "{{arrivedDate}}")
        private TextView arrivedDateTextView;

        @Binding(id = R.id.arrived_date_contain)
        private LinearLayout arrivedDateContain;

        @Binding(id = R.id.order_status_text_view, format = "{{orderStatus}}")
        private TextView orderStatusTV;

        Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_income_mny,null);
            binder.bindView(this, view);

        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);

            switch (model.getInt("category")){
                case IncomeViewController.ALLOWANCE:
                    srcNameTagTextView.setText("来自系统: ");
                    orderIdContain.setVisibility(View.VISIBLE);
                    consumedDateContain.setVisibility(View.GONE);
                    break;
                case IncomeViewController.RED_PACK:
                    srcNameTagTextView.setText("来自红包: ");
                    orderIdContain.setVisibility(View.GONE);
                    consumedDateContain.setVisibility(View.GONE);
                    break;
                case IncomeViewController.ORDER:
                    srcNameTagTextView.setText("来自订单: ");
                    orderIdContain.setVisibility(View.VISIBLE);
                    consumedDateContain.setVisibility(View.VISIBLE);
                    break;
            }

            if(isArrived){
//                consumedDateTextView.setVisibility(View.VISIBLE);
                arrivedDateContain.setVisibility(View.VISIBLE);
            }else {
//                consumedDateTextView.setVisibility(View.GONE);
                arrivedDateContain.setVisibility(View.GONE);
            }

        }

        @Override
        public View getView() {
            return view;
        }
    }
}
