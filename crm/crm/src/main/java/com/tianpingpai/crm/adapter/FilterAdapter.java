package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;

import java.util.HashSet;
@SuppressWarnings("unused")
public class FilterAdapter extends BaseAdapter {

	private static final int ITEM_TYPE_AREA = 0;
	private static final int ITEM_TYPE_RADIO = 1;
	private static final int ITEM_TYPE_TITLE = 2;

	public void fillHttpParams(HttpRequest<?> req) {
		if(buyerButton == null){
			req.addParam("view_sub", "0");
			return;
		}
		
		if(buyerButton.isChecked()){
			Log.e("xx","buyer checked");
			if(!sellerButton.isChecked()){
				Log.e("xx","1 seller not checked");
				req.addParam("user_type", "1");
			}
		}else{
			Log.e("xx","buyer not checked");
			if(sellerButton.isChecked()){
				Log.e("xx","2 seller checked");
				req.addParam("user_type", "0");
			}else{
				Log.e("xx","2 seller not checked");
			}
		}
		//0代表我的客户，1代表我的下属的客户
		if(areaListener.topButton.isChecked()){
			req.addParam("view_sub", "0");
		}else{
			if(areaListener.bottomButton.isChecked()){
				req.addParam("view_sub", "1");
			}
		}
		
		if(timeListener.topButton.isChecked()){
			req.addParam("date_type", "0");
		}else if(timeListener.bottomButton.isChecked()){
			req.addParam("date_type", "1");
		}else if(timeListener.extraButton.isChecked()){
			req.addParam("date_type", "2");
		}else if(timeListener.extra2Button.isChecked()){
			req.addParam("date_type", "3");
		}
		
		if(currentMarket != null && currentMarket.getId() != -1){
			req.addParam("market_id", currentMarket.getId() + "");
		}
	}

	@Override
	public int getCount() {
		if (MarketManager.getInstance().getMarkets() != null) {
			return 5 + MarketManager.getInstance().getMarkets().size();
		}
		return 3;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < 3) {
			return ITEM_TYPE_RADIO;
		}
		if (position == 3) {
			return ITEM_TYPE_TITLE;
		}
		return ITEM_TYPE_AREA;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			if (getItemViewType(position) == ITEM_TYPE_RADIO) {
				convertView = LayoutInflater.from(ContextProvider.getContext())
						.inflate(R.layout.item_radio_group, null);
				configureRadio(convertView, position, true);
			} else if (getItemViewType(position) == ITEM_TYPE_AREA) {
				convertView = LayoutInflater.from(ContextProvider.getContext())
						.inflate(R.layout.item_selection, null);
				configureArea(convertView, position);
			} else {
				convertView = LayoutInflater.from(ContextProvider.getContext())
						.inflate(android.R.layout.simple_list_item_1, null);
				TextView tv = (TextView) convertView;
				tv.setText("商圈");
				tv.setTextColor(tv.getResources().getColor(R.color.gray_66));
				tv.setBackgroundResource(R.color.gray_ec);
			}
		} else {
			if (getItemViewType(position) == ITEM_TYPE_RADIO) {
				configureRadio(convertView, position, false);
			} else if (getItemViewType(position) == ITEM_TYPE_AREA) {
				configureArea(convertView, position);
			}
		}
		return convertView;
	}

	private CompoundButton buyerButton;
	private CompoundButton sellerButton;
	
	private void configureRadio(View view, int position, boolean init) {
		TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
		CheckBox topBox = (CheckBox) view.findViewById(R.id.top_check_box);
		CheckBox bottomBox = (CheckBox) view
				.findViewById(R.id.bottom_check_box);
		if (position == 0) {
			titleTextView.setText("用户类型");
			topBox.setText("买家");
			bottomBox.setText("卖家");
			buyerButton = topBox;
			sellerButton = bottomBox;
		} else if (position == 1) {
			titleTextView.setText("查看范围");
			topBox.setText("只看我的");
			bottomBox.setText("我的下属");
			if (init) {
				bottomBox.setChecked(false);
				areaListener.addCheckBox(topBox,0);
				areaListener.addCheckBox(bottomBox,1);
			}
		} else {
			//客户的时间0是今日1是本周2是本月3是更多
			titleTextView.setText("时间");
			topBox.setText("今天");
			topBox.setChecked(false);
			bottomBox.setText("本周");
			
			CheckBox extra = (CheckBox) view.findViewById(R.id.extra_check_box);
			extra.setVisibility(View.VISIBLE);
			extra.setText("本月");
			
			CheckBox extra2 = (CheckBox) view.findViewById(R.id.extra2_check_box);
			extra2.setVisibility(View.VISIBLE);
			extra2.setText("更早");
			extra2.setChecked(true);
			
			if (init) {
				bottomBox.setChecked(false);
				timeListener.addCheckBox(topBox,0);
				timeListener.addCheckBox(bottomBox,1);
				timeListener.addCheckBox(extra,2);
				timeListener.addCheckBox(extra2,3);
			}
		}
	}

	private MarketModel currentMarket;
	private HashSet<CheckBox> marketButtons = new HashSet<>();

	private void configureArea(View view, int position) {
		CheckBox tv = (CheckBox) view;
		MarketModel mm;
		if(position == 4){
			mm = new MarketModel();
			mm.setId(-1);
			mm.setName("全部");
			tv.setChecked(true);
		}else{
			mm = MarketManager.getInstance().getMarkets()
					.get(position - 5);
		}
		
		tv.setTextColor(Color.GRAY);
		tv.setText(mm.getName());
		tv.setOnCheckedChangeListener(marketCheckedChangeListener);
		tv.setTag(mm);
		tv.setEnabled(true);
		tv.setClickable(true);
		marketButtons.add(tv);
	}

	private OnCheckedChangeListener marketCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				currentMarket = (MarketModel) buttonView.getTag();
			}
			for (CheckBox cb : marketButtons) {
				if (cb.getTag() != currentMarket) {
					cb.setChecked(false);
				}
			}
		}
	};

	private GroupListener areaListener = new GroupListener();
	private GroupListener timeListener = new GroupListener();

	private class GroupListener implements OnCheckedChangeListener {
		private HashSet<CompoundButton> buttons = new HashSet<>();
		private CompoundButton currentSelection;
		private CompoundButton topButton;
		private CompoundButton bottomButton;
		private CompoundButton extraButton;
		private CompoundButton extra2Button;

		public void addCheckBox(CompoundButton cb,int position) {
			buttons.add(cb);
			if (cb.isChecked()) {
				currentSelection = cb;
			}
			cb.setOnCheckedChangeListener(this);
			if(position == 0){
				topButton = cb;
			}else if(position == 1){
				bottomButton = cb;
			} else if(position == 2){
				extraButton = cb;
			} else {
				extra2Button = cb;
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				for (CompoundButton cb : buttons) {
					if (cb != buttonView) {
						cb.setChecked(false);
					}
				}
				currentSelection = buttonView;
			} else {
				if (currentSelection == buttonView) {
					buttonView.setSelected(true);
				}
			}
		}
	}
}
