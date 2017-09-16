package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

public class GridCategoryAdapter extends ModelAdapter<Model> {

    private Model selectCategory;

    public void setSelectCategory(Model selectCategory) {
        this.selectCategory = selectCategory;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CategoryViewHolder(inflater);
    }

    private class CategoryViewHolder implements ViewHolder<Model> {

        private View view;
        private TextView categoryNameTextView;

        CategoryViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_category_name, null, false);
            categoryNameTextView = (TextView) view.findViewById(R.id.category_name_text_view);
        }

        @Override
        public void setModel(Model model) {

            categoryNameTextView.setText(model.getString("name"));
            if(selectCategory != null){
                if(selectCategory.getInt("category_id") == model.getInt("category_id")){
                    categoryNameTextView.setBackgroundResource(R.drawable.bg_grid_item_corners_d9);
//                    categoryNameTextView.setTextColor(categoryNameTextView.getResources().getColor(R.color.blue));
                }else{
                    categoryNameTextView.setBackgroundResource(R.drawable.bg_grid_item_corners_f2);
                }

            }else {
                categoryNameTextView.setBackgroundResource(R.drawable.bg_grid_item_corners_f2);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
