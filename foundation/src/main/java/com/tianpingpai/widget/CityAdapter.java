package com.tianpingpai.widget;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.foundation.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.ModelAdapter;

public class CityAdapter extends ModelAdapter<Model> {

    private Model selection;
    private boolean selectionMode;

    public void setSelectionMode(boolean selectionMode){
        this.selectionMode = selectionMode;
    }

    public void setSelection(Model m){
        selection = m;
        notifyDataSetChanged();
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new CityViewHolder(inflater);
    }

    public Model getSelection() {
        return selection;
    }

    private class CityViewHolder implements ViewHolder<Model>{
        private View view;
        private TextView nameTextView;
        private View selectionView;
        @SuppressLint("InflateParams")
        public CityViewHolder(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.item_city,null);
            nameTextView = (TextView) view.findViewById(R.id.name_text_view);
            selectionView = view.findViewById(R.id.selection_view);
        }

        @Override
        public void setModel(Model model) {
            nameTextView.setText(model.getString("name"));
            if(selectionMode){
                if(model == selection){
                    selectionView.setVisibility(View.VISIBLE);
                }else{
                    selectionView.setVisibility(View.INVISIBLE);
                }
            }else{
                selectionView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
