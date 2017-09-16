package com.tianpingpai.seller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class FirstCategorySelectionAdapter extends ModelAdapter<Model> {

    private HashSet<Integer> selectedIds = new HashSet<>();

    public void select(int id){
        selectedIds.add(id);
    }

    public ArrayList<Model> getSelection(){
        ArrayList<Model> selection = new ArrayList<>();
        for(Model m:getModels()){
            int id = m.getInt("category_id");
            if(selectedIds.contains(id)) {
                selection.add(m);
            }
        }
        return selection;
    }

    public void toggleSelection(int position){
        int id = getItem(position).getInt("category_id");
        if(selectedIds.contains(id)){
            selectedIds.remove(id);
        }else{
            selectedIds.add(id);
        }
        notifyDataSetChanged();
    }

    public boolean shouldLoad(){
        return getCount() == 0;
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CategoriesViewHolder();
    }

    private class CategoriesViewHolder implements ViewHolder<Model>{

        private View view = View.inflate(ContextProvider.getContext(), R.layout.item_first_category_selection,null);

        private TextView nameTextView;
        private ImageView selectionImageView;

        {
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            selectionImageView = (ImageView) view.findViewById(R.id.selection_image_view);
        }
        @Override
        public void setModel(Model model) {
            nameTextView.setText(model.getString("name"));
            int id = model.getInt("category_id");
            if(selectedIds.contains(id)){
                selectionImageView.setImageResource(R.drawable.ic_checked);
            }else{
                selectionImageView.setImageResource(R.drawable.ic_check_not);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
