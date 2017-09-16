package com.tianpingpai.seller.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends ModelAdapter<Model> {
    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        return new PropertyViewHolder();
    }

    private Model selectedWithValueModel;
    private List<Model> selectedModels;
    public void setSelectedWithValueModel(Model selectedWithValueModel) {
        this.selectedWithValueModel = selectedWithValueModel;
        selectedModels = selectedWithValueModel.getList("selectedModels", Model.class);
    }

    private Model remarkModel = new Model();

    public void setRemark(String string){
        remarkModel.set("value", string);
        notifyDataSetChanged();
    }

    public boolean isRemark(Model m){
        return remarkModel == m;
    }

    @Override
    public void setModels(ArrayList<Model> models) {
        models.add(remarkModel);
        remarkModel.set("name", "商品描述");
        remarkModel.set("value", "");
        remarkModel.set("type", "desc");
        super.setModels(models);
    }

    public String getRemark() {
        return remarkModel.getString("value");
    }

    @SuppressWarnings("unused")
    private class PropertyViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.name_text_view,format = "{{name}}")
        private TextView nameTextView;
        @Binding(id = R.id.required_text_view)
        private TextView requiredTextView;
        @Binding(id = R.id.attr_value_text_view,format = "{{value}}")
        private TextView attrValueTextView;

        private Binder binder = new Binder();

        {
            view = View.inflate(ContextProvider.getContext(), R.layout.item_product_property,null);
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model model) {
            boolean required = model.getBoolean("required");
            if(required){
                requiredTextView.setVisibility(View.VISIBLE);
            }else{
                requiredTextView.setVisibility(View.INVISIBLE);
            }
            Log.e("xx","81---value:" + model.getString("value"));
            if(selectedModels != null){
                String strValue = "";
                for(int i = 0; i<selectedModels.size(); i++){
                    if(selectedModels.get(i).getInt("attr_id") == model.getInt("id")){
                        if("quality".equals(model.getString("type"))){
                            strValue += "【" + selectedModels.get(i).getString("level") + "】";
                        }
                        strValue += selectedModels.get(i).getString("value");
                    }
                }
                if( !model.getString("type").equals("desc") ){
                    model.set("value", strValue);
                }
            }
            Log.e("xx","94---value:" + model.getString("value"));
            attrValueTextView.setText(model.getString("value"));
            binder.bindData(model);
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
