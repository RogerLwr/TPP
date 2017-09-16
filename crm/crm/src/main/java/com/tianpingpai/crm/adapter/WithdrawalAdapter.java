package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;
public class WithdrawalAdapter extends ModelAdapter<Model> {

    private String name ="";

    public void setName(String name){
        this.name = name;
    }

    @Override
    protected ViewHolder onCreateViewHolder(LayoutInflater inflater) {

        @SuppressLint("InflateParams") View convertView = inflater.inflate(R.layout.item_withdrawal,null);
        return new WithdrawalViewHolder(convertView);
    }

    @SuppressWarnings("unused")
    private class WithdrawalViewHolder implements ModelAdapter.ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.sale_name_text_view)
        private TextView saleNameTextView;
        @Binding(id = R.id.withdrawal_status_text_view,format = "{{status}}")
        private TextView withdrawalStatusTextView;
        @Binding(id = R.id.draw_money_text_view,format = "提现金额：￥{{amount}}")
        private TextView drawMoneyTextView;
        @Binding(id = R.id.apply_time_text_view,format = "申请时间：{{created}}")
        private TextView applyTimeTextView;

        private Model model;
        private Binder binder = new Binder();

        WithdrawalViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,view);
        }

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(m);
            saleNameTextView.setText(name);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}
