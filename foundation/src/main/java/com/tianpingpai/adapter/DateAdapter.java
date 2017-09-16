package com.tianpingpai.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.foundation.R;

import java.util.Calendar;

public class DateAdapter extends BaseAdapter {

    private Calendar calendar = Calendar.getInstance();

    {
        init();
    }

    private void init(){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DATE, startDate);
        if(getStartHour(0) >= 20){
            calendar.setTimeInMillis(System.currentTimeMillis() + 5 * 60 * 60 * 1000);
        }
    }

    int startHour = DeliverTimeAdapter.START_HOUR;

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration = 30;

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    private int startDate = 0;

    public void setStartHour(int startHour){
        this.startHour = startHour;
    }

    @Override
    public int getCount() {
        return duration;
    }

    private int selection;

    public int getSelection(){
        return selection;
    }

    public void setSelection(int selection){
        this.selection = selection;
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(ContextProvider.getContext(), R.layout.item_city,null);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(String.valueOf(position));
        init();
        calendar.add(Calendar.DATE, position);
        String text = String.format("%d年%02d月%02d日",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DAY_OF_MONTH));
        nameTextView.setText(text);
        View selectionView = convertView.findViewById(R.id.selection_view);
        if(position == selection){
            selectionView.setVisibility(View.VISIBLE);
        }else{
            selectionView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public int getStartHour(int position){
        if(position == 0){
            return calendar.get(Calendar.HOUR_OF_DAY);
        }
        return startHour;
    }

    public String getTime(int hour){
        init();
        calendar.add(Calendar.DATE, selection);
        return String.format("%d-%02d-%02d %02d:00-%02d:00",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DAY_OF_MONTH),hour,hour + 1);
    }

    public String getTime(String hour){
        init();
        calendar.add(Calendar.DATE, selection);
        return String.format("%d-%02d-%02d %s",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DAY_OF_MONTH),hour);
    }
    public String getDate(){
        init();
        calendar.add(Calendar.DATE, selection);
        return String.format("%d-%02d-%02d",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,calendar.get(Calendar.DAY_OF_MONTH));

    }
}
