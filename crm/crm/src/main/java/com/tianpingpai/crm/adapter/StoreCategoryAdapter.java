package com.tianpingpai.crm.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.StoreCategoryModel;
import com.tianpingpai.ui.ModelAdapter;

@SuppressWarnings("unused")
public class StoreCategoryAdapter extends ModelAdapter<StoreCategoryModel> {
    @Override
    protected ViewHolder<StoreCategoryModel> onCreateViewHolder(LayoutInflater inflater) {
        return new CategoryViewHolder(inflater);
    }

    private class CategoryViewHolder implements ViewHolder<StoreCategoryModel>{

        private TextView tv;
        CategoryViewHolder(LayoutInflater inflater){
            tv = (TextView) LayoutInflater.from(ContextProvider.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            tv.setGravity(Gravity.CENTER);
        }
        @Override
        public void setModel(StoreCategoryModel model) {
            tv.setText(model.getCategory());
            tv.setTextColor(Color.WHITE);
        }

        @Override
        public View getView() {
            return tv;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if(convertView == null){
            tv = (TextView) LayoutInflater.from(ContextProvider.getContext()).inflate(android.R.layout.simple_list_item_1, null);
            tv.setGravity(Gravity.CENTER);
        }else{
            tv = (TextView) convertView;
        }
        tv.setText(getItem(position).getCategory());
        tv.setTextColor(Color.BLACK);
        tv.setTextScaleX(16);
        return tv;
    }
}
