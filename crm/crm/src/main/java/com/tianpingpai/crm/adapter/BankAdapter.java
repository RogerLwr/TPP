package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.crm.ui.ContractDetailViewController;
import com.tianpingpai.crm.ui.UploadBankCardViewController;
import com.tianpingpai.model.BankModel;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
public class BankAdapter extends ModelAdapter<Model> {

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
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_bank, null);
        return new BankViewHolder(convertView);
    }
    private class BankViewHolder implements ViewHolder<Model>{

        private View view;

        @Binding(id = R.id.bank_card_id_text_view,format = "{{card_no}}")
        private TextView bankCardIdTextView;
        @Binding(id = R.id.bank_card_status_text_view,format = "{{status}}")
        private TextView bankCardStatusTextView;
        @Binding(id = R.id.payee_name_text_view,format = "收款人名称：{{name}}")
        private TextView payeeNameTextView;
        @Binding(id = R.id.bank_is_public_text_view,format = "银行卡性质：{{is_public}}")
        private TextView bankIsPublicTextView;
        @Binding(id = R.id.bank_name_text_view,format = "银行名称：{{bank}}")
        private TextView bankNameTextView;
        @Binding(id = R.id.update_time_text_view,format = "上传时间：{{upload_time}}")
        private TextView updateTimeTextView;
        @Binding(id = R.id.failed_reason_text_view,format = "失败原因：{{remarks}}")
        private TextView failedReasonTextView;

        Button lookBank;
        Model model;
        Binder binder = new Binder();

        BankViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,view);
            lookBank = (Button)view.findViewById(R.id.look_bank_button);
        }

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(m);
//            updateTimeTextView.setText("上传时间：" + new SimpleDateFormat("yyyy-MM-dd").format(m.getLong("upload_time")));

            switch (m.getInt("status")){
                case 0:
                    bankCardStatusTextView.setText("待审核");
                    failedReasonTextView.setVisibility(View.GONE);
                    break;
                case 1:
                    bankCardStatusTextView.setText("审核成功");
                    failedReasonTextView.setVisibility(View.GONE);
                    break;
                case 2:
                    bankCardStatusTextView.setText("审核失败");
                    failedReasonTextView.setVisibility(View.VISIBLE);
                    lookBank.setText("重新上传");
                    break;
            }
            switch (m.getInt("is_public")){
                case 0:
                    bankIsPublicTextView.setText("银行卡性质：对私");
                    break;
                case 1:
                    bankIsPublicTextView.setText("银行卡性质：对公");
                    break;
            }
            lookBank.setTag(m);
        }

        @Override
        public View getView() {
            return view;
        }

        @OnClick(R.id.look_bank_button)
        private View.OnClickListener lookBankOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, UploadBankCardViewController.class);
                intent.putExtra(UploadBankCardViewController.KEY_IS_CHECK_OUT, true);
                BankModel bm = new BankModel();
                getBankModel(bm,model);
                intent.putExtra(UploadBankCardViewController.KEY_BANK_ITEM_MODEL, bm);
                intent.putExtra(ContractDetailViewController.KEY_CUSTOMER,customerModel);
                activity.startActivity(intent);
            }
        };

        private void getBankModel(BankModel bm,Model model){
            bm.setId(model.getInt("id"));
            bm.setUserId(model.getInt("user_id"));
            bm.setUploadTime(model.getLong("upload_time"));
            bm.setCardNo(model.getString("card_no"));
            bm.setName(model.getString("name"));
            bm.setIsPublic(model.getInt("is_public"));
            bm.setBank(model.getString("bank"));
            bm.setStatus(model.getInt("status"));
            bm.setContractId(model.getString("contract_id"));
            bm.setOpeningBankAddress(model.getString("opening_bank_address"));
            bm.setBankProvince(model.getString("bank_province"));
            bm.setBankCity(model.getString("bank_city"));
            bm.setBankcardPictures(model.getList("bankcard_pictures",String.class));
            bm.setIdcardPictures(model.getList("idcard_pictures",String.class));
            bm.setContractPictures(model.getList("contract_pictures",String.class));
        }
    }
}
