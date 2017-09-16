package com.tianpingpai.crm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.user.UserModel;

import java.util.Collection;
import java.util.HashSet;
@SuppressWarnings("unused")
public class CustomerFilterAdapter extends BaseAdapter {
    private int userType;
    public CustomerFilterAdapter(int type){
        this.userType = type;
    }
    public int selectedUserType = 1;
    public int selectedOrderTime = 0;
    public int selectedCustomerSource = 0;
    public int selectedCustomerStatus = 0;
    public int selectedNotOrderStatus = 0;

//    String[] cats = {"用户类型", "商圈", "时间", "人员"};
    String[] cats = {"商圈", "时间", "人员"};
    String[] buyerCats = {"商圈", "时间", "人员","客户来源","客户分类","按未下单时间"};

//    public static final String[] userTypes = {"全部", "买家", "卖家"};
    public static final String[] timeTypes = {"全部", "本日", "本周", "本月"};
    public static final String[] customerSource = {"全部","注册添加客户","公海认领客户","转客户"};
    public static final String[] customerStatus = {"全部","未下单客户","已下单客户"};
    public static final String[] notOrder = {"全部","1-4天","5-9天","10-14天","14天以上"};

    int selectionPosition = -1;

    public void setSelection(int position){
        selectionPosition = position;
        notifyDataSetChanged();
    }

    private Collection<UserModel> marketers;
    private String marketerIds;

    public Collection<UserModel> getMarketers() {
        return marketers;
    }

    public MarketModel getSelectedMarket() {
        return selectedMarket;
    }

    private MarketModel selectedMarket;

    @Override
    public int getCount() {
        if(0==userType){
            return cats.length;
        }else {
            return buyerCats.length;
        }
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_customer_filter, null, false);
        }
        TextView categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
        if(0==userType){
            categoryTextView.setText(cats[i]);
        }else{
            categoryTextView.setText(buyerCats[i]);
        }

        String valueText = null;
        switch (i) {
            case 0:
                valueText = selectedMarket == null ? "全部" : selectedMarket.getName();
                break;
            case 1:
                valueText = timeTypes[selectedOrderTime];
                break;
            case 2:
                if (marketers == null) {
                    valueText = "全部";
                } else {
                    if (marketers.isEmpty()) {
                        valueText = "全部";
                    } else {
                        if (marketers.size() == 1) {
                            for (UserModel um : marketers) {
                                if (um == UserManager.getInstance().getCurrentUser()) {
                                    valueText = "自己";
                                } else {
                                    valueText = um.getDisplayName();
                                }
                            }
                        } else {
                            valueText = "多人";
                        }
                    }
                }
                break;
            case 3:
                valueText = customerSource[selectedCustomerSource];
                break;
            case 4:
                valueText = customerStatus[selectedCustomerStatus];
                break;
            case 5:
                valueText = notOrder[selectedNotOrderStatus];
                break;
        }

        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);
        valueTextView.setText(valueText);
        return view;
    }

    public void setMarketers(HashSet<UserModel> selectedUserSet) {
        this.marketers = selectedUserSet;
    }

    public void setMarket(MarketModel market) {
        this.selectedMarket = market;
        notifyDataSetChanged();
    }

    public int getUserType() {

        if (selectedUserType == 0) {
            return -1;
        }
        if (selectedUserType == 1) {
            return CustomerModel.USER_TYPE_BUYER;
        }
        return CustomerModel.USER_TYPE_SELLER;
    }

    public int getDateType() {
        return (selectedOrderTime - 1) ;
    }

    public int getSelectedCustomerSource(){
        return (selectedCustomerSource);
    }

    public int getSelectedCustomerStatus(){
        return (selectedCustomerStatus);
    }

    public int getSelectedNotOrderStatus(){
        return selectedNotOrderStatus;
    }

    public String getMarketerIds() {
        if (marketers == null) {
            return null;
        }
        String s = "";
        for (UserModel user : marketers) {
            s += user.getId() + ",";
        }
        return s;
    }

}
