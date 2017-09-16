package com.tianpingpai.seller.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.DateFormatter;

public class StatisticsOrderAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new OrderViewHolder(inflater);
    }

    @SuppressWarnings("unused")
    class OrderViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.buyer_name_text_view,format = "{{b_shop_name}}")
        private TextView buyerNameTextView;
        @Binding(id = R.id.time_text_view,format = "下单时间:{{order_dt}}")
        private TextView timeTextView;
        @Binding(id = R.id.amount_text_view,format = "金额:¥{{mny|money}}")
        private TextView amountTextView;

        private Binder binder = new Binder();

        public OrderViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_statistics_order,null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user != null && user.getGrade() == UserModel.GRADE_1){
                long orderDt = model.getLong("order_dt");
                String dateOrderDtStr = DateFormatter.formatMS(orderDt);
                if(!TextUtils.isEmpty(dateOrderDtStr)){
                    timeTextView.setText(String.format("下单时间:%s",dateOrderDtStr));
                }
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
