package com.tianpingpai.seller.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

public class DeliverTypeAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new DeliverTypeViewHolder(inflater);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    class DeliverTypeViewHolder implements ViewHolder<Model> {

        private View view;
        private TextView nameTextView;

        public DeliverTypeViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(android.R.layout.simple_list_item_1,null);
            nameTextView = (TextView) view;
            nameTextView.setTextColor(Color.BLACK);
            nameTextView.setTextSize(15);
        }

        @Override
        public void setModel(Model model) {
            nameTextView.setText(model.getString("name"));
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
