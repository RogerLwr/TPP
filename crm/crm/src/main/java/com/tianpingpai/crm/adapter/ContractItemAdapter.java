package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.ContractStatusViewController;
import com.tianpingpai.crm.ui.UploadContractViewController;
import com.tianpingpai.model.ContractModel;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
public class ContractItemAdapter extends ModelAdapter<Model>{

    private Activity activity;

    private CustomerModel customerModel;

    public void setActivity(Activity a){
        this.activity = a;
    }

    public void setCustomerModel(CustomerModel cm){
        this.customerModel = cm;
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {

        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_contract_detail, null);
        return new ContractItemViewHolder(convertView);
    }

    @SuppressWarnings("unused")
    private class ContractItemViewHolder implements ViewHolder<Model>{

        private View view;

        @Binding(id = R.id.contract_number_text_view,format = "{{contract_id}}")
        private TextView contractNumberTextView;
        @Binding(id = R.id.contract_status_text_view)
        private TextView contractStatusTextView;
        @Binding(id = R.id.legal_name_text_view,format = "法人姓名：{{legal_name}}")
        private TextView legalNameTextView;
        @Binding(id = R.id.update_time_text_view,format = "上传时间：{{upload_time}}")
        private TextView updateTimeTextView;
        @Binding(id = R.id.contract_fail_des_text_view,format = "失败原因：{{remarks}}")
        private TextView contractFailDesTextView;

        Button lookContract;
        Model model;
        Binder binder = new Binder();

        ContractItemViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,view);
            lookContract = (Button)v.findViewById(R.id.look_contract_button);

        }

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(m);
//            updateTimeTextView.setText("上传时间：" + new SimpleDateFormat("yyyy-MM-dd").format(m.getLong("upload_time")));
            switch (m.getInt("status")){
                case 0:
                    contractStatusTextView.setText("待审核");
                    contractFailDesTextView.setVisibility(View.GONE);
                    break;
                case 1:
                    contractStatusTextView.setText("审核成功");
                    contractFailDesTextView.setVisibility(View.GONE);
                    break;
                case 2:
                    contractStatusTextView.setText("审核失败");
                    contractFailDesTextView.setVisibility(View.VISIBLE);
                    lookContract.setText("重新上传");
                    break;
            }
            if("".equals(m.getString("remarks"))||null==m.getString("remarks")){
//                contractFailDesTextView.setVisibility(View.VISIBLE);
            }else{
                contractFailDesTextView.setVisibility(View.VISIBLE);
            }

            lookContract.setTag(m);
        }

        @Override
        public View getView() {
            return view;
        }

        //TODO
        private void getContractModel(ContractModel cm,Model model){
            cm.setId(model.getInt("id"));
            cm.setUserId(model.getInt("user_id"));
            cm.setContractId(model.getString("contract_id"));
            cm.setLegalName(model.getString("legal_name"));
            cm.setLegalIdcard(model.getString("legal_idcard"));
            cm.setRemarks(model.getString("remarks"));
            cm.setStatus(model.getInt("status"));
            cm.setUploadTime(model.getLong("upload_time"));
            cm.setLegalIdcardPictures(model.getList("legal_idcard_pictures", String.class));
            cm.setContractPictures(model.getList("contract_pictures",String.class));
        }

        @OnClick(R.id.look_contract_button)
        private View.OnClickListener lookButton = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, UploadContractViewController.class);
                intent.putExtra(UploadContractViewController.KEY_IS_CHECK_OUT, true);
                ContractModel cm = new ContractModel();
                getContractModel(cm,model);
                intent.putExtra(UploadContractViewController.KEY_CONTRACT_ITEM_MODEL, cm);
                intent.putExtra(ContractStatusViewController.KEY_CUSTOMER,customerModel);
                activity.startActivity(intent);
            }
        };
    }
}
