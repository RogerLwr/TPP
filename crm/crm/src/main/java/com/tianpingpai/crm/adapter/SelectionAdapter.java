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
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.user.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
@SuppressWarnings("unused")
public class SelectionAdapter extends BaseAdapter{

    public void setParent(UserModel parent) {
        this.parent = parent;
    }

    private UserModel parent;
    private String[] data;
    private HashSet<Integer> selection = new HashSet<>();

    private int selectionPos;

    public void setSelection(int selection) {
        this.selectionPos = selection;
    }

    public void setSelectedUsers(HashSet<UserModel> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    private HashSet<UserModel> selectedUsers;

    public HashMap<UserModel, HashSet<UserModel>> getSelectedSub() {
        return selectedSub;
    }

    public void setSelectedSub(HashMap<UserModel, HashSet<UserModel>> selectedSub) {
        this.selectedSub = selectedSub;
    }

    private HashMap<UserModel,HashSet<UserModel>> selectedSub;
    private ArrayList<UserModel> marketers;

    public void setData(String[] data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(marketers != null){
            return marketers.size();
        }

        return data == null ? 0 : data.length;
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

        if(marketers == null){
            selectionImageView.setVisibility(View.GONE);
            if(i == selectionPos){
                valueTextView.setBackgroundResource(R.color.gray_f5);
            }else{
                valueTextView.setBackgroundResource(R.color.white);
            }
        }


        if(isSelected(i)){
            valueTextView.setTextColor(Color.RED);
            selectionImageView.setImageResource(R.drawable.cb_right);
        }else{
            valueTextView.setTextColor(Color.BLACK);
            selectionImageView.setImageResource(R.drawable.cb_off);
        }

        if(marketers == null){
            valueTextView.setText(data[i]);
        }else{
            String name;
            if(marketers.get(i) == UserManager.getInstance().getCurrentUser()){
                name = "自己";
            }else{
                name = marketers.get(i).getDisplayName();
                HashSet<UserModel> subs = selectedSub.get(marketers.get(i));
                if(subs != null && !subs.isEmpty()){
                    name += "(";
                    for(UserModel u:subs){
                        name += u.getDisplayName() + ",";
                    }
                    name += ")";
                }
            }
            valueTextView.setText(name);
        }
        view.findViewById(R.id.selection_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelection(i);
                Log.e("xx", "onClick");
            }
        });

        return view;
    }

    public void toggleSelection(int i){
        if(marketers == null){
            if(selection.contains(i)){
                selection.remove(i);
            }else{
                selection.add(i);
            }
        }else{
            UserModel user = marketers.get(i);
            if(selectedUsers.contains(user)){
                Log.e("xx", "contains:true");
                selectedUsers.remove(user);
                HashSet<UserModel> set = selectedSub.get(parent);
                if(set != null){
                    set.remove(user);
                }
            }else{
                Log.e("xx","contains:false");
                selectedUsers.add(user);

                HashSet<UserModel> set = selectedSub.get(parent);
                if(set == null){
                    set = new HashSet<>();
                }
                set.add(user);
                selectedSub.put(parent,set);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelected(int position){
        if(marketers == null){
            return selection.contains(position);
        }else{
            UserModel user = marketers.get(position);
            return selectedUsers.contains(user);
        }
    }


    public Collection<Integer> getSelection() {
        return selection;
    }

    public void setMarketers(ArrayList<UserModel> marketers) {
        this.marketers = marketers;
        notifyDataSetChanged();
    }

    public ArrayList<UserModel> getMarketers() {
        return marketers;
    }

    public HashSet<UserModel> getSelectedUserSet() {
        return selectedUsers;
    }

    public HashMap<UserModel,HashSet<UserModel>> getSubMarketers() {
        return selectedSub;
    }
}
