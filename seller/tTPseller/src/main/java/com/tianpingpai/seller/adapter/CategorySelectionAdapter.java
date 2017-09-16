package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

public class CategorySelectionAdapter extends ModelAdapter<Model>{
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CategorySelectionViewHolder(inflater);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    private class CategorySelectionViewHolder implements ViewHolder<Model>{

        private View view;
        private TextView nameTextView;

        CategorySelectionViewHolder(LayoutInflater inflater){
            view = inflater.inflate(R.layout.item_category_selection,null,false);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
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
