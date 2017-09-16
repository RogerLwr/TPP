package com.tianpingpai.seller.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

public class ProductQualityAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new ProductQualityViewHolder();
    }

    private int selectedPosition = -1;
    public void setSelectedPosition(int position){
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    private boolean editable = false;

    public void setEditable(boolean editable){
        this.editable = editable;
        notifyDataSetChanged();
    }

    public void setAddViewOnClickListener(View.OnClickListener addViewOnClickListener) {
        this.addViewOnClickListener = addViewOnClickListener;
    }

    private View.OnClickListener addViewOnClickListener;

    @Override
    public int getCount() {
        if(editable){
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < getModels().size()){
            return 0;
        }
        return 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position) == 0){
            return super.getView(position, convertView, parent);
        }
        @SuppressLint("ViewHolder")
        View addView = View.inflate(ContextProvider.getContext(),R.layout.item_product_property_add,null);
        addView.setOnClickListener(addViewOnClickListener);
        return addView;
    }

    @SuppressWarnings("unused")
    private class ProductQualityViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "【{{level}}】")
        private TextView nameTextView;
        @Binding(id = R.id.is_checked_image_view)
        private ImageView isCheckedImageView;
        @Binding(id = R.id.value_text_view,format = "{{value}}")
        private TextView valueTextView;
        @Binding(id = R.id.chevron_right_view)
        private View chevronRightView;

        private int index;

        private Binder binder = new Binder();

        private View.OnClickListener checkboxViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = index;
                notifyDataSetChanged();
            }
        };

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_product_quality,null);
            binder.bindView(this,view);
            isCheckedImageView.setOnClickListener(checkboxViewOnClickListener);
        }

        @Override
        public void setModel(Model model) {
            binder.bindData(model);
//            nameTextView.setText(model.getString("level"));
            index = getModels().indexOf(model);
            if(selectedPosition != -1){
                int id = getItem(selectedPosition).getInt("id");
                if( getItem(selectedPosition).equals(model) ){
                    isCheckedImageView.setImageResource(R.drawable.ic_rb_checked);
                }else {
                    isCheckedImageView.setImageResource(R.drawable.ic_rb_unchecked);
                }
            }else{
                isCheckedImageView.setImageResource(R.drawable.ic_rb_unchecked);
            }
            boolean editable = model.getBoolean("editable");
            if(editable){
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

    public boolean shouldLoad(){
        return getData() == null;
    }
}
