package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserModel;
@SuppressWarnings("unused")
@SuppressLint("InflateParams")
public class SubordinatesAdapter extends ModelAdapter<UserModel> {

	LayoutInflater mInflater;

	public void setLayoutInflater(LayoutInflater l) {
		this.mInflater = l;
	}

	@Override
	protected com.tianpingpai.ui.ModelAdapter.ViewHolder<UserModel> onCreateViewHolder(
			LayoutInflater inflater) {
		return new SubordinateViewHolder(inflater);
	}

	class SubordinateViewHolder implements ViewHolder<UserModel> {
		SubordinateViewHolder(LayoutInflater inflater) {
			view = mInflater.inflate(R.layout.item_subordinate, null);
			nameTextView = (TextView) view.findViewById(R.id.name_text_view);
			phoneTextView = (TextView) view.findViewById(R.id.phone_text_view);
		}

		private View view;
		private TextView nameTextView;
		private TextView phoneTextView;

		@Override
		public void setModel(UserModel model) {
			nameTextView.setText("姓名:" + model.getDisplayName());
			phoneTextView.setText("电话:" + model.getPhone());
		}

		@Override
		public View getView() {
			return view;
		}
	}
}
