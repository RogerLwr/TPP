package com.tianpingpai.widget;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class NumberAdapter extends ModelAdapter<Integer> implements AdapterView.OnItemClickListener{

    private int min;

    public void setFormat(String format) {
        this.format = format;
    }

    private String format;

    public void setRange(int min,int max){
        this.min = min;
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = min;i <= max;i++){
            list.add(i);
        }
        setModels(list);
    }

    public void setSelection(int selection) {
        this.selection = selection;
        notifyDataSetChanged();
    }

    public int getSelectedNumber(){
        return selection + min;
    }

    private int selection;

    @Override
    protected ViewHolder<Integer> onCreateViewHolder(LayoutInflater inflater) {
        return new NumberViewHolder(inflater);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelection(position);
    }

    public int getSelection() {
        return selection;
    }

    private class NumberViewHolder implements ViewHolder<Integer> {
        private View view;
        private TextView nameTextView;
        private View selectionView;

        @SuppressLint("InflateParams")
        public NumberViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_city, null);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            selectionView = view.findViewById(R.id.selection_view);
        }

        @Override
        public void setModel(Integer model) {
            if(format == null) {
                nameTextView.setText(String.valueOf(model));
            }else{
                nameTextView.setText(String.format(format,model));
            }
            if (model - min == selection) {
                selectionView.setVisibility(View.VISIBLE);
            } else {
                selectionView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }

}
