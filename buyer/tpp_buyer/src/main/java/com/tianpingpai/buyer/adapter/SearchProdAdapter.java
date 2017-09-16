package com.tianpingpai.buyer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class SearchProdAdapter extends ModelAdapter<ProductModel> {
    private Activity a;
    @Override
    protected ViewHolder<ProductModel> onCreateViewHolder(
            LayoutInflater inflater) {
        return new ProductViewHolder(inflater);
    }

    public Activity getActivity() {
        return a;
    }

    public void setActivity(Activity a) {
        this.a = a;
    }

    class ProductViewHolder implements ViewHolder<ProductModel> {
        private Binder binder = new Binder();

        private View view;

        @Binding(id = R.id.name_text_view)
        private TextView nameTextView;


        public ProductViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_search_prod, null);
            binder.bindView(this, view);
        }

        @Override
        public void setModel(ProductModel model) {
            nameTextView.setText(model.getName());
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
