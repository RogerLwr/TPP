package com.tianpingpai.seller.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.model.UserModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public class BillFilterCategoryAdapter extends BaseAdapter {

    public int selectedBillType = 0;
    public int selectedBillStatus = 0;
    public int selectedOrderTime = 0;


    public String[] cats = {"类型", "状态"};
    public static final String[] billTypes = {"收入", "全部", "订单收入", "补贴收入", "红包收入", "退款", "支出", "全部", "支付宝支付", "微信支付", "余额支付", "申请提现", "扣款"};

    public List<Model> billTypeModels;

    public void setBillTypeModels(List<Model> billTypeModels){
        this.billTypeModels = billTypeModels;
        notifyDataSetChanged();
    }

    private HashSet<Model> selectedBillTypes;

    public void setSelectedBillTypes(HashSet<Model> selectedBillTypes) {
        this.selectedBillTypes = selectedBillTypes;
        notifyDataSetChanged();
    }

    public static final String[] billStatus = {"全部", "进行中", "交易完成", "交易失败"};

    private HashSet<Integer> selections;
    public void setSelections(HashSet<Integer> selections){
        this.selections = selections;
        notifyDataSetChanged();
    }

    private Collection<UserModel> marketers;

    int selectionPosition = -1;

    public void setSelection(int position){
        selectionPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cats.length;
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
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_bill_filter, null, false);
        }
        TextView categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
        categoryTextView.setText(cats[i]);


        String valueText = "";
        switch (i) {
            case 0:
                if(selectedBillTypes != null && selectedBillTypes.size() > 0){

                    for (Model billType : selectedBillTypes){
                        valueText = valueText + billType.getString("name") + ",";
                    }
                    if( !TextUtils.isEmpty(valueText) ){
                        valueText = valueText.substring(0, valueText.length()-1);
                    }

                }else if(billTypeModels != null){
//                    valueText = billTypeModels.get(selectedBillType).getString("name");
                    valueText = "请选择";
                }else {
//                    valueText = billTypes[selectedBillType];
                }
                break;
            case 1:
                if(selections.size() != 0){

                    for(Integer position : selections){
                        valueText = valueText + billStatus[position] + " ";
                    }
                }else {
                    valueText = billStatus[selectedBillStatus];
                }
                break;

        }

        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);
        valueTextView.setText(valueText);
        return view;
    }

    public void setMarketers(Collection<UserModel> marketers) {
        this.marketers = marketers;
    }
}
