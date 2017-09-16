package com.tianpingpai.crm.adapter;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class UserTypeAdapter extends BaseAdapter {

	String[] data = null;
	public int normalTextColor = Color.WHITE;
	
	@Override
	public int getCount() {
		return data == null ? 0 : data.length;
	}

	public void setData(String[] data){
		this.data = data;
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView) LayoutInflater.from(ContextProvider.getContext()).inflate(android.R.layout.simple_list_item_1, null);
		tv.setText(data[position]);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(normalTextColor);
		return tv;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView) LayoutInflater.from(ContextProvider.getContext()).inflate(android.R.layout.simple_list_item_1, null);
		tv.setText(data[position]);
		tv.setTextColor(tv.getResources().getColor(R.color.gray_66));
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

}
