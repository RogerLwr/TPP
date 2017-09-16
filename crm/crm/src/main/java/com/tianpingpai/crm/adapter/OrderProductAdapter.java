package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
@SuppressWarnings("unused")
@SuppressLint("InflateParams")
public class OrderProductAdapter extends ModelAdapter<Model> {

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<Model> onCreateViewHolder(
			LayoutInflater inflater) {
		return new ProductViewHolder(inflater);
	}

	@SuppressWarnings("unused")
	class ProductViewHolder implements ViewHolder<Model>{
		private View view;
		@Binding(id = R.id.name_text_view,format = "{{prod_name}}")
		private TextView nameTextView;
		@Binding(id = R.id.price_text_view,format = "￥{{prod_price}}{{unit}}")
		private TextView priceTextView;
		@Binding(id = R.id.number_text_view,format = "x{{prod_num}}")
		private TextView numberTextView;
		@Binding(id = R.id.remark_text_view,format = "{{remark}}")
		private TextView remarkTextView;

		Binder binder = new Binder();
		
		public ProductViewHolder(LayoutInflater inflater) {
			view = inflater.inflate(R.layout.item_order_product, null);
			binder.bindView(this,view);
		}
		
		@Override
		public void setModel(Model model) {

			binder.bindData(model);
			String remark = model.getString("remark");
			if(TextUtils.isEmpty(remark)){
				remarkTextView.setVisibility(View.GONE);
			}else{
				remarkTextView.setVisibility(View.VISIBLE);
				remarkTextView.setText(remark);
			}
		}

		@Override
		public View getView() {
			return view;
		}
	}
}
