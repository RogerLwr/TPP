package com.tianpingpai.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.utils.DimensionUtil;

@SuppressLint("InflateParams")
public class StringAdapter extends ModelAdapter<String> {

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<String> onCreateViewHolder(
			LayoutInflater inflater) {
		return new StringViewHolder(inflater);
	}

	private class StringViewHolder implements ViewHolder<String> {
		TextView view;

		public StringViewHolder(LayoutInflater inflater) {
			view = (TextView) inflater.inflate(
					android.R.layout.simple_expandable_list_item_1, null);
			view.setGravity(Gravity.END);
			view.setTextColor(0xff4c4c4c);
			view.setTextSize(13);
			int padding = DimensionUtil.dip2px(16);
			view.setPadding(padding, padding / 2, padding, padding / 2);
		}

		@Override
		public void setModel(String model) {
			view.setText(model);
		}

		@Override
		public View getView() {
			return view;
		}
	}
}
