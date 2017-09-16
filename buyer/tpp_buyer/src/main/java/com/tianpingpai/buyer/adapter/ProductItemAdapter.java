package com.tianpingpai.buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class ProductItemAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new ProductItemViewHolder();
    }

    class ProductItemViewHolder implements ViewHolder<Model> {

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{prod_name}}")
        private TextView nameTextView;
        @Binding(id = R.id.number_text_view,format = "x{{prod_num}}")
        private TextView numberTextView;
        @Binding(id = R.id.price_text_view,format = "单价￥{{coupon_price}}")
        private TextView priceTextView;

        private Binder binder = new Binder();

        private ProductItemViewHolder() {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_product_simple, null);
            binder.bindView(this,view);
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
