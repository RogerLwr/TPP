package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.model.CategoryModel;
import com.tianpingpai.ui.ModelAdapter;

public class ProductCategoryAdapter extends ModelAdapter<CategoryModel> {

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<CategoryModel> onCreateViewHolder(
			LayoutInflater inflater) {
		return new CatViewHolder(inflater);
	}
	
	private class CatViewHolder implements ViewHolder<CategoryModel>{
		private View view;
		private TextView nameTextView;
		
		public CatViewHolder(LayoutInflater inflater) {
			view = inflater.inflate(android.R.layout.simple_list_item_1, null);
			nameTextView = (TextView) view;
			nameTextView.setGravity(Gravity.CENTER);
			nameTextView.setTextColor(Color.GRAY);
		}
		
		@Override
		public void setModel(CategoryModel model) {
			nameTextView.setText(model.getName());
		}

		@Override
		public View getView() {
			return view;
		}
	}
}
