package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;

import java.util.List;

/**
 * CRM 业态 日营业 日采购额 的选择 适配器
 */
@SuppressLint("InflateParams")
public class SelectListAdapter extends BaseAdapter {

	List<Model> models;

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public SelectListAdapter() {
		// setData(MarketManager.getInstance().getMarkets());
	}

	@Override
	public int getCount() {
		return models == null ? 0 : models.size();
	}

	@Override
	public Model getItem(int position) {
		return models.get(position);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getDropDownView(position, convertView, parent);
		TextView tv = (TextView) view.findViewById(R.id.name_text_view);
		tv.setText(getItem(position).getString("name"));
		return view;
	}

	class SelectViewHolder {
		View view;
		TextView tv;

		public SelectViewHolder(LayoutInflater inflater) {
			view = inflater.inflate(R.layout.item_select, null);
			tv = (TextView)view.findViewById(R.id.name_text_view);
			view.setTag(this);
		}

		public void setModel(Model model) {
			tv.setText(model.getString("name"));
		}

		public View getView() {
//			view.setBackgroundColor(Color.WHITE);
			view.setBackgroundColor(ContextProvider.getContext().getResources().getColor(R.color.white));
			return view;
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SelectViewHolder holder;
		if (convertView == null) {
			holder = new SelectViewHolder(LayoutInflater.from(ContextProvider
					.getContext()));
		} else {
			holder = (SelectViewHolder) convertView.getTag();
		}
		holder.setModel(getItem(position));
		return holder.getView();
	}
}
