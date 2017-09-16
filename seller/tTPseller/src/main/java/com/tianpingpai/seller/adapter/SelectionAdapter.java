package com.tianpingpai.seller.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings("unused")
public class SelectionAdapter extends BaseAdapter {

    private HashSet<Integer> selections;
    public void setSelections(HashSet<Integer> selections){
        this.selections = selections;
    }

    private int selectionPos;
    public void setSelectionPos(int selection) {
        this.selectionPos = selection;
    }
    private boolean multiSelection;
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    private ArrayList<Model> billTypes;

    private String[] data;
    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (billTypes != null) {
            return billTypes.size();
        }

        return data == null ? 0 : data.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_filter_selection, null, false);
        }
        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);

        ImageView selectionImageView = (ImageView) view.findViewById(R.id.selection_image_view);

        // 单选
        if ( !multiSelection ) {
            selectionImageView.setVisibility(View.GONE);
            if (i == selectionPos) {
//                valueTextView.setBackgroundResource(R.color.gray_f5);
                selectionImageView.setVisibility(View.VISIBLE);
                selectionImageView.setImageResource(R.drawable.ic_checked_bill_type);
            } else {
//                valueTextView.setBackgroundResource(R.color.white);
                selectionImageView.setVisibility(View.GONE);
            }
        }else {

            // 多选
            if (isSelected(i)) {
//                valueTextView.setTextColor(Color.RED);
                selectionImageView.setVisibility(View.VISIBLE);
                selectionImageView.setImageResource(R.drawable.ic_checked_bill_type);
            } else {
//                valueTextView.setTextColor(Color.BLACK);
                selectionImageView.setVisibility(View.GONE);
            }

        }



        valueTextView.setText(data[i]);

        return view;
    }

    public void toggleSelection(int i) {
        if (billTypes == null) {
            if (selections.contains(i)) {
                selections.remove(i);
            } else {
                selections.add(i);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int position) {
        return selections.contains(position);
    }


    public Collection<Integer> getSelections() {
        return selections;
    }

}
