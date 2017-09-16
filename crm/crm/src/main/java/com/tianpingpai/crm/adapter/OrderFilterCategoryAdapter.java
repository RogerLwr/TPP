package com.tianpingpai.crm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.user.UserModel;

import java.util.Collection;
@SuppressWarnings("unused")
public class OrderFilterCategoryAdapter extends BaseAdapter {

    public int selectedOrderState = 0;
    public int selectedUserType = 2;
    public int selectedOrderTime = 0;


    String[] cats = {"订单状态", "商家类型", "订单时间", "人员"};
    public static final String[] orderTypes = {"全部", "未付款", "待确认", "未发货", "已发货",
            "已收货", "已结帐", "取消订单", "取消订单中", "已取消", "已评价"};

    public static final String[] userTypes = {"全部", "卖家", "买家"};
    public static final String[] timeTypes = {"全部", "本日", "本周", "本月"};

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
            view = LayoutInflater.from(ContextProvider.getContext()).inflate(R.layout.item_order_filter, null, false);
        }
        TextView categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
        categoryTextView.setText(cats[i]);


        String valueText = null;
        switch (i) {
            case 0:
                valueText = orderTypes[selectedOrderState];
                break;
            case 1:
                valueText = userTypes[selectedUserType];
                break;
            case 2:
                valueText = timeTypes[selectedOrderTime];
                break;
            case 3:
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
        }

        TextView valueTextView = (TextView) view.findViewById(R.id.value_text_view);
        valueTextView.setText(valueText);
        return view;
    }

    public void setMarketers(Collection<UserModel> marketers) {
        this.marketers = marketers;
    }
}
