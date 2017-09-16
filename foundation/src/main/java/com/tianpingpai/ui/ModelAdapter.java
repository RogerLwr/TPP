package com.tianpingpai.ui;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;
import com.tianpingpai.parser.ListResult;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public abstract class ModelAdapter<T> extends BaseAdapter {

	public interface PageControl<T> {
		void onLoadPage(int page);
	}

	public interface ViewHolder<T> {
		void setModel(T model);

		View getView();
	}

	public void setPageControl(PageControl<T> pc) {
		this.pageControl = pc;
	}

	private PageControl<T> pageControl;
	private ListResult<T> listResult;
	private ArrayList<T> models;

	public boolean isEmpty(){
		return getCount() == 0;
	}

	@Override
	public boolean isEnabled(int position) {
		if(models != null){
			return position < models.size();
		}
		return super.isEnabled(position);
	}

	public void setData(ListResult<T> data) {
		this.listResult = data;
		if (models == null) {
			if (data != null) {
				models = data.getModels();// TODO
			}
		} else {
			if (data != null && data.getModels() != null) {
				for(T m:data.getModels()){
					if(!models.contains(m)){
						models.add(m);
					}
				}
			}
		}
		loading = false;
		notifyDataSetChanged();
	}
	
	public ListResult<T> getData(){
		return this.listResult;
	}

	public void setModels(ArrayList<T> models) {
		this.models = models;
		notifyDataSetChanged();
	}
	
	public ArrayList<T> getModels(){
		return this.models;
	}

	public void clear() {
		if (models != null) {
//			models.clear();
			models = null;
		}
		
		listResult = null;
		notifyDataSetChanged();
	}

	private boolean loading = false;

	public void setRefreshing(boolean refreshing){
		this.loading = refreshing;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (listResult == null) {
			if (models != null) {
				return models.size();
			}
			return 0;
		}
		
		if(models == null){
			return 0;
		}

		if (listResult.hasMorePage()) {
			return models.size() + 1;
		}
		return models.size();
	}

	@Override
	public T getItem(int position) {
		if(models != null) {
			return models.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (models == null  || position < models.size()) {
			return 0;// TODO
		}
		return 1;// TODO 1
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		currentPos = position;
		ViewHolder<T> holder;
		if (getItemViewType(position) == 0) {
			if (convertView == null) {
				holder = onCreateViewHolder(LayoutInflater.from(ContextProvider
						.getContext()));
				convertView = holder.getView();
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder<T>) convertView.getTag();
			}
			holder.setModel(getItem(position));
		} else {
			if (pageControl != null && !loading) {
				pageControl.onLoadPage(listResult.getNextPage());
			}
			Log.e("xx", "load more");
			convertView = LayoutInflater.from(ContextProvider.getContext())
					.inflate(R.layout.item_loading_footer, null);
		}

		return convertView;
	}

	protected abstract ViewHolder<T> onCreateViewHolder(LayoutInflater inflater);
	private int currentPos;
	public int getCurrentPos(){
		return currentPos;
	}
}
