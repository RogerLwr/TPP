package com.tianpingpai.buyer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class CollectProductInAdapter extends ModelAdapter<Model> {

    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater) {
        return null;
    }


    /*
    private ArrayList<Model> mList = new ArrayList<>();
    public void setData(ArrayList<Model> list){
        if(list.size() == 0){
            mList =list;
        }else{
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
    */
}
