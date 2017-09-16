package com.tianpingpai.buyer.adapter;
import com.tianpingpai.buyer.R;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SearchProductAdapter extends ModelAdapter<ProductModel> {

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<ProductModel> onCreateViewHolder(
			LayoutInflater inflater) {
		return new SearchViewHolder(inflater);
	}

	@SuppressWarnings("unused")
	private class SearchViewHolder implements ViewHolder<ProductModel>{
		private View view;
		@Binding(id = R.id.name_text_view,format = "{{prod_name}}")
		private TextView nameTextView;
		@Binding(id = R.id.price_text_view,format = "￥{{coupon_price}}/{{unit}}")
		private TextView priceTextView;
		@Binding(id = R.id.seller_text_view,format = "商家:{{sale_name}}")
		private TextView sellerTextView;
		@Binding(id = R.id.desc_text_view,format = "描述:{{description}}")
		private TextView descTextView;

		private Binder binder = new Binder();
		
		public SearchViewHolder(LayoutInflater inflater) {
			view = inflater.inflate(R.layout.item_search_product, null);
			binder.bindView(this,view);
		}

		@Override
		public void setModel(ProductModel model) {
			binder.bindData(model);
		}

		@Override
		public View getView() {
			return view;
		}
		
	}
}
