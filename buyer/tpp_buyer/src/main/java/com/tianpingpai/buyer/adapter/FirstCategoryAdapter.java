package com.tianpingpai.buyer.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.model.LayeredProduct.SecondCategory;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.widget.BadgeView;

public class FirstCategoryAdapter extends ModelAdapter<SecondCategory> {

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<SecondCategory> onCreateViewHolder(
			LayoutInflater inflater) {
		return new CategoryViewHolder(inflater);
	}
	
	private SecondCategory currentSelection;
	private int storeId;
	private boolean enableBadge = true;
	
	public void setEnableBadge(boolean enable){
		this.enableBadge = enable;
	}
//	private boolean isMultiShop;
	
	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public SecondCategory getCurrentSelection() {
		return currentSelection;
	}

	public void setCurrentSelection(SecondCategory currentSelection) {
		this.currentSelection = currentSelection;
		this.notifyDataSetChanged();
	}
	
	int green = Color.parseColor("#9dcf62");

	class CategoryViewHolder implements ViewHolder<SecondCategory>{
		private View view;
		private TextView nameTextView;
		private BadgeView badgeView;
		
		CategoryViewHolder(LayoutInflater inflater){
			view = inflater.inflate(R.layout.item_first_category, null);
			nameTextView = (TextView) view.findViewById(R.id.name_text_view);
			badgeView = (BadgeView) view.findViewById(R.id.badge_view);
			badgeView.setText("");
			if(!enableBadge){
				badgeView.setVisibility(View.INVISIBLE);
			}
		}
		
		
		@Override
		public void setModel(SecondCategory model) {
			TextView tv = nameTextView;
			tv.setText(model.getName());
			tv.setGravity(Gravity.CENTER);
			if(model == currentSelection){
				view.setBackgroundColor(Color.WHITE);
				tv.setTextColor(green);
			}else{
				view.setBackgroundColor(Color.TRANSPARENT);
				tv.setTextColor(Color.GRAY);
			}
			
			int count = 0;
			for(ProductModel pm:model.getProducts()){
				if(pm.getProductNum() > 0){
					count++;
				}
			}
			int categoryNum = count;
			Log.e("xx","sss");
			if(categoryNum > 0){
				badgeView.setText(categoryNum + "");
			}else{
				badgeView.setText(null);
			}
		}

		@Override
		public View getView() {
			return view;
		}
	}
}
