package com.tianpingpai.seller.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class PrintProductItemAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new PrintProductItemViewHolder();
    }

    @SuppressWarnings("unused")
    class PrintProductItemViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{prod_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.remark_text_view,format = "备注: {{remark}}")
        private TextView remarkTextView;
        @Binding(id = R.id.number_text_view,format = "x{{prod_num}}")
        private TextView numberTextView;
        @Binding(id = R.id.specs_unit_text_view,format = "￥{{coupon_price | money}}")
        private TextView priceTextView;
        @Binding(id = R.id.total_price_text_view,format = "￥{{__total_price__ | money}}")
        private TextView totalPriceTextView;

        private Binder binder = new Binder();

        PrintProductItemViewHolder() {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_print_product_item,null);
            binder.bindView(this,view);
//            priceTextView.setGravity(Gravity.LEFT_BOTTOM | Gravity.CENTER_VERTICAL);
        }

        @Override
        public void setModel(Model model) {
            boolean isHeader = model.getBoolean("isHeader");
            if(isHeader){
                nameTextView.setText("商品名");
                priceTextView.setText("单价");
                numberTextView.setText("数量");
                totalPriceTextView.setText("总价");
                return;
            }
            double total = model.getDouble("coupon_price") * model.getInt("prod_num");
            model.set("__total_price__", total);
            binder.bindData(model);
            String remark = model.getString("remark");
            if(remark != null){
                remark = remark.trim();
            }
            if( !TextUtils.isEmpty(remark)){
                remarkTextView.setVisibility(View.VISIBLE);
            }else{
                remarkTextView.setVisibility(View.GONE);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
