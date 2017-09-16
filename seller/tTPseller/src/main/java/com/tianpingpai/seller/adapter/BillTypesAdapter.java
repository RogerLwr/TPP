package com.tianpingpai.seller.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class BillTypesAdapter extends BaseAdapter{

    public void setParent(Model parent) {
        this.parent = parent;
    }

    private Model parent;
    private List<Model> data;
    private HashSet<Integer> selection = new HashSet<>();

    private int selectionPos;

    public void setSelection(int selection) {
        this.selectionPos = selection;
    }

    private HashSet<Model> selectedBillTypes;

    public void setSelectedBillTypes(HashSet<Model> selectedBillTypes) {
        this.selectedBillTypes = selectedBillTypes;
    }

    public HashMap<Model, HashSet<Model>> getSelectedSub() {
        return selectedSub;
    }

    public void setSelectedSub(HashMap<Model, HashSet<Model>> selectedSub) {
        this.selectedSub = selectedSub;
    }

    private HashMap<Model,HashSet<Model>> selectedSub;
    int inCount;
    public void setInCount(int inCount){
        this.inCount = inCount;
    }
    private List<Model> billTypes;
    public void setBillTypes(List<Model> billTypes) {
        this.billTypes = billTypes;
        notifyDataSetChanged();
    }


//    public void setData(List<Model> data){
//        this.data = data;
//        notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        if(billTypes != null){
            return billTypes.size();
        }

        return data == null ? 0 : data.size();
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
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_bill_type,null,false);
        }
        TextView billTypeNameTextView = (TextView) view.findViewById(R.id.bill_type_name_text_view);
        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);

        ImageView selectionImageView = (ImageView) view.findViewById(R.id.selection_image_view);

        billTypeNameTextView.setText(billTypes.get(i).getString("parent_name"));
        valueTextView.setText(billTypes.get(i).getString("name"));
        if(i == 0 || i == inCount){
            billTypeNameTextView.setVisibility(View.VISIBLE);

        }else {
            billTypeNameTextView.setVisibility(View.GONE);
        }


        if(isSelected(i)){
//            valueTextView.setTextColor(Color.RED);
            selectionImageView.setVisibility(View.VISIBLE);
            selectionImageView.setImageResource(R.drawable.ic_checked_bill_type);
        }else{
//            valueTextView.setTextColor(Color.BLACK);
            selectionImageView.setVisibility(View.GONE);
        }


        return view;
    }

    public void toggleSelection(int i){
        if(billTypes == null){
            if(selection.contains(i)){
                selection.remove(i);
            }else{
                selection.add(i);
            }
        }else{
            Model billType = billTypes.get(i);
            if(selectedBillTypes.contains(billType)){
                Log.e("xx", "contains:true");
                selectedBillTypes.remove(billType);
//                HashSet<Model> set = selectedSub.get(parent);
//                if(set != null){
//                    set.remove(billType);
//                }
            }else{
                Log.e("xx","contains:false");
                selectedBillTypes.add(billType);

//                HashSet<Model> set = selectedSub.get(parent);
//                if(set == null){
//                    set = new HashSet<>();
//                }
//                set.add(billType);
//                selectedSub.put(parent,set);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int position){
        if(billTypes == null){
            return selection.contains(position);
        }else{
            Model billType = billTypes.get(position);
            return selectedBillTypes.contains(billType);
        }
    }


    public Collection<Integer> getSelection() {
        return selection;
    }

    public List<Model> getBillTypes() {
        return billTypes;
    }

    public HashSet<Model> getSelectedUserSet() {
        return selectedBillTypes;
    }

//    public HashMap<Model,HashSet<Model>> getSubMarketers() {
//        return selectedSub;
//    }
}
