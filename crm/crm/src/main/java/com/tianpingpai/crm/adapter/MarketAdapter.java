package com.tianpingpai.crm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.ui.ModelAdapter;
@SuppressWarnings("unused")
public class MarketAdapter extends ModelAdapter<MarketModel> {
    @Override
    protected ViewHolder<MarketModel> onCreateViewHolder(LayoutInflater inflater) {
        return new MarketViewHolder(inflater);
    }

    class MarketViewHolder implements ViewHolder<MarketModel>{

        private View view;
        private TextView nameTextView;

        public MarketViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_filter_selection, null, false);
            nameTextView = (TextView) view.findViewById(R.id.value_text_view);
            (view.findViewById(R.id.selection_image_view)).setVisibility(View.GONE);
//            view = inflater.inflate(android.R.layout.simple_list_item_1, null, false);
//            nameTextView = (TextView) view;
//            nameTextView.setGravity(Gravity.LEFT | Gravity.CENTER);
//            nameTextView.setPadding(40, 0, 0, 0);
//            nameTextView.setTextColor(Color.BLACK);
//            nameTextView.setTextSize(16);
        }

        @Override
        public void setModel(MarketModel model) {
            nameTextView.setText(model.getName());
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
