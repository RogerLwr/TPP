package com.tianpingpai.seller.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

public class SelectCategoryAdapter extends ModelAdapter<Model> {

    int f4 = ContextProvider.getContext().getResources().getColor(R.color.gray_f4);
    int white = Color.WHITE;

    @Override
    public void setModels(ArrayList<Model> models) {
        ArrayList<Model> flatArray = new ArrayList<>();
        for(Model m:models){
            flatArray.add(m);
            ArrayList<Model> subCategories = (ArrayList<Model>) m.getList("subCategories",Model.class);
            flatArray.addAll(subCategories);
        }
        super.setModels(flatArray);
    }

    @Override
    public boolean isEnabled(int position) {
        Model model = getItem(position);
        int level = model.getInt("level");
        return level != 1;//TODO
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CategoryViewHolder();
    }

    private class CategoryViewHolder implements ViewHolder<Model>{

        private View view;
        private TextView nameTextView;
        private View chevronRightView;

        private CategoryViewHolder(){
            view = View.inflate(ContextProvider.getContext(), R.layout.item_category_second,null);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            chevronRightView = view.findViewById(R.id.chevron_right_view);
        }

        @Override
        public void setModel(Model model) {
            nameTextView.setText(model.getString("name"));
            int level = model.getInt("level");
            if(level == 1){
                view.setBackgroundColor(f4);
            }else{
                view.setBackgroundColor(white);
            }
            if(level == 2){
                chevronRightView.setVisibility(View.VISIBLE);
            }else{
                chevronRightView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
