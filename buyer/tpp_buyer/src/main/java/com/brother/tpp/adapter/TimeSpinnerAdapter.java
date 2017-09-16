package com.brother.tpp.adapter;

import com.tianpingpai.buyer.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/** 年月份选择的Spinner
 * @author lwr
 *
 */
@SuppressLint({ "ViewHolder", "InflateParams" })
public class TimeSpinnerAdapter extends BaseAdapter {
	String[] data = null;
	private Context mCtx;

	public TimeSpinnerAdapter(Context mCtx) {
		super();
		this.mCtx = mCtx;
	}

	public void setData(String[] data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data == null ? 0 : data.length;
	}


	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_time_text, null);
		TextView tv = (TextView) convertView.findViewById(R.id.content_text_view);
		tv.setText(data[position]);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(tv.getResources().getColor(R.color.white));
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_time_dropdown, parent, false);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_title);
		parent.setBackgroundColor(parent.getResources().getColor(R.color.bg_ccc));
		tv.setText(data[position]);
		tv.setGravity(Gravity.CENTER);
		return convertView;
	}
}
