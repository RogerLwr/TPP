package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.material.widget.CheckBox;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class ProductSelectionAdapter extends ModelAdapter<ProductModel> {
	
	public interface ProductSelectionListener{
		void onSelectionChange(ProductModel pm,boolean isSelected);
		boolean contains(ProductModel pm);
	}
	
	ProductSelectionListener selectionListener;
	
	public void setSelectionListener(ProductSelectionListener l){
		this.selectionListener = l;
	}
	
	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<ProductModel> onCreateViewHolder(
			LayoutInflater inflater) {
		return new ProductViewHolder(inflater);
	}

    @SuppressWarnings("unused")
	private class ProductViewHolder implements ViewHolder<ProductModel>{
		private View view;
		@Binding(id = R.id.name_text_view,format = "{{prod_name}}")
		private TextView nameTextView;
		@Binding(id = R.id.price_text_view,format = "{{coupon_price}}/{{unit}}")
		private TextView priceTextView;
		@Binding(id = R.id.desc_text_view,format = "{{description}}")
		private TextView descTextView;
		@Binding(id = R.id.selection_check_box)
		private CheckBox checkBox;
		private ProductModel model;

		private Binder binder = new Binder();
		
		private ProductViewHolder(LayoutInflater inflater){
			view = inflater.inflate(R.layout.item_product_selection, null);
			binder.bindView(this, view);
			nameTextView.setGravity(Gravity.CENTER);
			nameTextView.setTextColor(Color.parseColor("#666666"));
		}
		
		@Override
		public void setModel(ProductModel model) {
			this.model = model;
			binder.bindData(model);
			if (TextUtils.isEmpty(model.getDescription())) {
				descTextView.setVisibility(View.GONE);
			} else {
				descTextView.setVisibility(View.VISIBLE);
			}
			checkBox.setOnCheckedChangeListener(null);
			if(selectionListener != null){
				checkBox.setChecked(selectionListener.contains(model));
			}
			checkBox.setOnCheckedChangeListener(checkBoxListener);
		}

		@Override
		public View getView() {
			return view;
		}
		
		private OnCheckedChangeListener checkBoxListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(selectionListener != null){
					selectionListener.onSelectionChange(model, isChecked);
				}
			}
		};
	}
}
