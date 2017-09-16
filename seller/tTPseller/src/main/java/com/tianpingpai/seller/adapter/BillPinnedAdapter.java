package com.tianpingpai.seller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;

import java.util.ArrayList;
import java.util.List;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

/**
 * Created by LiuWenRong on 16/4/21.
 */
public class BillPinnedAdapter extends SectionedBaseAdapter{

    private TextView headerTextView;

    private class BillViewHolder {

            private View view;

            @Binding(id = R.id.header_title_text_view)
            private TextView headerTitleTextView;
            @Binding(id = R.id.title_text_view, format = "{{title}}")
            private TextView titleTextView;
            @Binding(id = R.id.status_text_view, format = "{{status}}")
            private TextView statusTextView;

            @Binding(id = R.id.desc_text_view, format = "{{description}}")
            private TextView descTextView;
            @Binding(id = R.id.time_text_view, format = "{{time}}")
            private TextView timeTextView;

            @Binding(id = R.id.money_text_view, format = "{{__sign__}}{{mny}}")
            private TextView moneyTextView;

            private Binder binder = new Binder();
            private BillViewHolder(View v){
                this.view = v;
                binder.bindView(this, view);
            }

        }

    List<Model> bills;

    public void setBills(List<Model> bills) {
        this.bills = bills;
    }
    List<List<Model>> pinnedBills = new ArrayList<>();

    public void setPinnedBills(List<List<Model>> pinnedBills) {
        this.pinnedBills = pinnedBills;
        notifyDataSetChanged();
    }

    public boolean isEmpty(){
        return pinnedBills.isEmpty();
    }

    @Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        return pinnedBills.get(section).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getSectionCount() {
        return pinnedBills.size(); //几个 section
    }

    @Override
    public int getCountForSection(int section) {
        return pinnedBills.get(section).size();  //一个 section 有 几个?
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        View layout = null;
        BillViewHolder billViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.item_bill, null);
            billViewHolder = new BillViewHolder(layout);
            layout.setTag(billViewHolder);
        } else {
            layout = convertView;
            billViewHolder = (BillViewHolder) layout.getTag();
        }
        Log.e("xx", "103------pos="+position);
        if(pinnedBills != null){
            Model model = pinnedBills.get(section).get(position);
            if(model.getInt("type") == 1){ //收入
                billViewHolder.moneyTextView.setTextColor(billViewHolder.moneyTextView.getResources().getColor(R.color.green_0c));
                model.set("__sign__","+");
            }else {
                billViewHolder.moneyTextView.setTextColor(billViewHolder.moneyTextView.getResources().getColor(R.color.red_ff6));
                model.set("__sign__","-");
            }

            billViewHolder.binder.bindData(model);
        }

        return layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflater.inflate(R.layout.item_bill_header, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        ((TextView) layout.findViewById(R.id.header)).setText(pinnedBills.get(section).get(0).getString("yearMonth"));
        return layout;
    }

}
