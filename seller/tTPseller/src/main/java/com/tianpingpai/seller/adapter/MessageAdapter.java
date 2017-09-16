package com.tianpingpai.seller.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

public class MessageAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new MessageAdapterViewHolder(inflater);
    }

    private class MessageAdapterViewHolder implements ViewHolder<Model> {
        private View view;
        private TextView titleTextView;
        private TextView timeTextView;
        private TextView contentTextView;

        MessageAdapterViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_message,null,false);
            titleTextView = (TextView) view.findViewById(R.id.title_text_view);
            timeTextView = (TextView) view.findViewById(R.id.time_text_view);
            contentTextView = (TextView) view.findViewById(R.id.content_text_view);
        }
        @Override
        public void setModel(Model model) {
            Log.e("xx", "model:" + model);
            titleTextView.setText(model.getString("type_name"));
            timeTextView.setText(model.getString("send_dt"));
            contentTextView.setText(model.getString("content"));
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
