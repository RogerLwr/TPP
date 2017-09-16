package com.tianpingpai.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;


public class DeliverTimeAdapter extends BaseAdapter {

    public static final int START_HOUR = 3;
    private static final int MIN_GAP = 4;

    private int start = START_HOUR;

    public void setStart(int s){
        this.start = s;
        if(this.start < START_HOUR){
            this.start = START_HOUR;
        }
        start += MIN_GAP;
        selection = 0;
        notifyDataSetChanged();
    }

    public int getStart(){
        return start;
    }

    @Override
    public int getCount() {
        return 24 - start;
    }

    private int selection;

    public void setSelection(int selection){
        this.selection = selection;
        notifyDataSetChanged();
    }

    public int getHour(){
        return selection + start;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(ContextProvider.getContext(), R.layout.item_city,null);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(String.valueOf(position));
        int hour = position + start;
        String text = hour + ":00 - " + (hour + 1) + ":00";
        nameTextView.setText(text);

        View selectionView = convertView.findViewById(R.id.selection_view);
        if(position == selection){
            selectionView.setVisibility(View.VISIBLE);
        }else{
            selectionView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
