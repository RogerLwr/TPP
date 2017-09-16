package com.tianpingpai.buyer.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.tianpingpai.buyer.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.util.ImageLoader;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {
	
	ArrayList<String> urls = null;

	ImageView[] mImageViews = {
			new ImageView(ContextProvider.getContext()),
			new ImageView(ContextProvider.getContext()),
			new ImageView(ContextProvider.getContext()),
			new ImageView(ContextProvider.getContext()),
			new ImageView(ContextProvider.getContext()), };
	
	public void setData(ArrayList<String> data){
		this.urls = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return urls == null ? 0 : urls.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mImageViews[position
				% mImageViews.length]);
	}

	/**
	 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = mImageViews[position % mImageViews.length];
		iv.setImageResource(R.drawable.ic_home);
		iv.setScaleType(ScaleType.CENTER_CROP);
		container.addView(iv, 0);
		String url = urls.get(position);
		ImageLoader.load(url, iv);
		return iv;
	}
}
