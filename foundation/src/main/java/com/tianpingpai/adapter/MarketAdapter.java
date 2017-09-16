package com.tianpingpai.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class MarketAdapter extends BaseAdapter {

	private ArrayList<MarketModel> models = MarketManager.getInstance()
			.getMarkets();



	public MarketAdapter() {
//		 setData(MarketManager.getInstance().getMarkets());
	}

	@Override
	public int getCount() {
		return models == null ? 0 : models.size();
	}

	@Override
	public MarketModel getItem(int position) {
		return models.get(position);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);
		TextView tv = (TextView) view;
		tv.setTextColor(Color.BLACK);
		tv.setText(getItem(position).getName());
		return view;
	}

	class MarketViewHolder {
		TextView view;

		public MarketViewHolder(LayoutInflater inflater) {
			view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
			view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			TextView tv = view;
			tv.setTextColor(Color.BLACK);
			view.setTag(this);
		}

		public void setModel(MarketModel model) {
			view.setText(model.getName());
		}

		public View getView() {
//			view.setBackgroundColor(Color.WHITE);
			view.setBackgroundColor(ContextProvider.getContext().getResources().getColor(R.color.gray_ed));
			return view;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MarketViewHolder holder;
		if (convertView == null) {
			holder = new MarketViewHolder(LayoutInflater.from(ContextProvider
					.getContext()));
		} else {
			holder = (MarketViewHolder) convertView.getTag();
		}
		holder.setModel(getItem(position));
		return holder.getView();
	}
}
