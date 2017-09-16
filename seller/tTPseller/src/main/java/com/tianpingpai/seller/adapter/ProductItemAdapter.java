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

public class ProductItemAdapter extends ModelAdapter<Model>{
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new ProductItemViewHolder();
    }

    @SuppressWarnings("unused")
    class ProductItemViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{prod_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.remark_text_view,format = "备注: {{remark}}")
        private TextView remarkTextView;
        @Binding(id = R.id.number_text_view,format = "x{{prod_num}}")
        private TextView numberTextView;
        @Binding(id = R.id.specs_unit_text_view,format = "单价:￥{{coupon_price | money}}/{{unit}}")
        private TextView priceTextView;

        private Binder binder = new Binder();

        ProductItemViewHolder() {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_product_simple,null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
            if( !TextUtils.isEmpty(model.getString("remark"))){
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
