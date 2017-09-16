package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.ContractDetailViewController;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ContractAdapter extends ModelAdapter<Model> {
    private Activity activity;

    public void setActivity(Activity a){
        this.activity = a;
    }

    @Override
    public void setModels(ArrayList<Model> models) {
        super.setModels(models);
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_contract1124, null);
        return new ContractViewHolder(convertView);
    }

    private class ContractViewHolder implements View.OnClickListener,ViewHolder<Model> {

        private View view;

        @Binding(id = R.id.store_name_text_view, format = "{{sale_name}}")
        private TextView storeNameTextView;
        @Binding(id = R.id.user_name_text_view, format = "客户姓名: {{display_name}}")
        private TextView usernameTextView;
        @Binding(id = R.id.contanct_text_view, format = "客户电话: {{phone}}")
        private TextView contactTextView;
        @Binding(id = R.id.address_text_view, format = "客户地址: {{sale_address}}")
        private TextView addressTextView;

        Model model;

        Binder binder = new Binder();

        ContractViewHolder(View v){
            v.setTag(this);
            view = v;
            binder.bindView(this,view);
//            nextUpDateBtn = (Button) view.findViewById(R.id.next_update_button);
//            lookContractButton = (Button) view.findViewById(R.id.look_contract_button);
        }

        @Override
        public void onClick(View v) {

        }

        //再次上传照片按钮
        @OnClick(R.id.next_update_button)
        private View.OnClickListener nextUpDateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        //上传及查看状态按钮
        @OnClick(R.id.look_contract_button)
        private View.OnClickListener lookContractClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CustomerModel cm = new CustomerModel();
                getCustomerModel(cm, model);
                Intent intent = new Intent(activity,ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,ContractDetailViewController.class);
                intent.putExtra(ContractDetailViewController.KEY_CUSTOMER,cm);
                activity.startActivity(intent);

            }
        };

        @Override
        public void setModel(Model c) {
            this.model = c;
            binder.bindData(model);
            //判断各种状态极其显示
//            nextUpDateBtn.setTag(c);
//            lookContractButton.setTag(c);
        }

        @Override
        public View getView() {
            return view;
        }

        //TODO
        private void getCustomerModel(CustomerModel cm, Model model){
            cm.setUserId(model.getInt("user_id"));
//            cm.setId(model.getInt("user_id"));
            cm.setPhone(model.getString("phone"));
            cm.setDisplayName(model.getString("display_name"));
            cm.setSaleName(model.getString("sale_name"));
            cm.setSaleAddress(model.getString("sale_address"));
            cm.setUserType(Integer.parseInt(model.getString("user_type")));
            cm.setMarketId(model.getInt("market_id"));
        }

    }
}
