package com.tianpingpai.seller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.tianpingpai.seller.R;

import java.util.List;

/** 百度地图搜索出来的地址列表适配器
 * @author lwr
 *
 */
public class AddressListAdapter extends BaseAdapter {

	private List<PoiInfo> data;
	private Context mCtx;
	
	public AddressListAdapter(List<PoiInfo> data, Context mCtx) {
		super();
		this.data = data;
		this.mCtx = mCtx;
	}

	@Override
	public int getCount() {
		return data==null?0:data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {  
		ViewHolder vh;
		PoiInfo poiInfo = data.get(position);
		String name = poiInfo.name;
		String address = poiInfo.address;
//		LatLng location = poiInfo.location;
		if(convertView == null){
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_address_baidu, null);
			vh= new ViewHolder(convertView);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}

		vh.mAddressName.setText(name);
		vh.mAddress.setText(address);
		
		return convertView;
	}

	class ViewHolder{
		TextView mAddressName, mAddress;
		public ViewHolder(View v){
			mAddressName = (TextView) v.findViewById(R.id.address_name_tv);
			mAddress = (TextView) v.findViewById(R.id.address_tv);
		}
	}
	
}
