package com.tianpingpai.crm.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectCategoryAdapter extends BaseAdapter {

    private boolean isOnly;

    public void setOnly(boolean isOnly){
        this.isOnly = isOnly;
    }

    private ArrayList<Integer> selectedId = new ArrayList<>();
    public void setSelected(ArrayList<Integer> selectedId){
        this.selectedId = selectedId;
    }
    private ArrayList<Model> disData;

    private HashMap<Integer ,String> selection = new HashMap<>();

    private HashMap<Integer ,String> selectionItem = new HashMap<>();

    public void clear(){
        disData = null;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<Model> data){
        Model m = new Model();
        m.set("name","全部");
        m.set("category_id",-2);
        if(isOnly){
            this.disData =data;
            if(disData.size()!=0){
                for(int i=0;i< disData.size();i++){
                    if(selectedId.contains(disData.get(i).getInt("category_id"))){
                        toggleSelection(i, disData.get(i).getString("name")+"abc"+ disData.get(i).getInt("category_id"));
                    }
                    Log.e("SelectCategoryAdapter==",disData.get(i).getString("name")+disData.get(i).getInt("category_id"));
                }
            }
        }else{
            ArrayList<Model> list = new ArrayList<>();
            list.add(m);
            list.addAll(data);
            this.disData = list;
            if(disData.size()!=0){
                for(int i=0;i< disData.size();i++){
                    if(selectedId.contains(disData.get(i).getInt("category_id"))){
                        toggleSelection(i, disData.get(i).getString("name")+"abc"+ disData.get(i).getInt("category_id"));
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return disData == null ? 0 : disData.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_filter_selection,null,false);
        }
        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);

        ImageView selectionImageView = (ImageView) view.findViewById(R.id.selection_image_view);

        valueTextView.setBackgroundResource(R.color.white);

        if(isSelected(i)){
            valueTextView.setTextColor(ContextProvider.getContext().getResources().getColor(R.color.green));
            selectionImageView.setImageResource(R.drawable.cb_right_green);
        }else{
            valueTextView.setTextColor(Color.BLACK);
            selectionImageView.setImageResource(R.drawable.cb_off_green);
        }
        valueTextView.setText(disData.get(i).getString("name"));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(i, disData.get(i).getString("name") + "abc" + disData.get(i).getInt("category_id"));
            }
        });
        return view;
    }

    public void toggleSelection(Integer i,String s){
        if(isOnly){
            selectionItem.clear();
            selection.clear();
            selectionItem.put(i, s);
            selection.put(i,s);
        }else{
            if(0 == i){
                if(selectionItem.containsKey(i)){
                    selectionItem.clear();
                    selection.clear();
                }else{
                    for(int a = 0;a<disData.size();a++){
                        selectionItem.put(a,disData.get(a).getString("name") + "abc" + disData.get(a).getInt("category_id"));
                        if(a>0){
                            selection.put(a,disData.get(a).getString("name") + "abc" + disData.get(a).getInt("category_id"));
                        }
                    }
                }
            }else{
                if(selectionItem.containsKey(i)){
                    if(selectionItem.containsKey(0)){
                        selectionItem.remove(0);
                    }
                    selectionItem.remove(i);
                    selection.remove(i);
                }else{
                    selectionItem.put(i,s);
                    selection.put(i, s);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int position){
        return selectionItem.containsKey(position);
    }

    public HashMap<Integer ,String> getSelection() {
        return selection;
    }


}
