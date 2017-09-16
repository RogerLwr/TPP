package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.view.View;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
@SuppressWarnings("unused")
@ActionBar(title = "合同管理")
@Layout(id = R.layout.view_controller_contract_detail)
public class ContractDetailViewController extends CrmBaseViewController {

    public static final String KEY_CUSTOMER = "key.Customer";
    private CustomerModel customerModel;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(KEY_CUSTOMER);
        setTitle(customerModel.getSaleName());
    }

    //合同管理
    @OnClick(R.id.contract_management_container)
    private View.OnClickListener contractManagementOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,ContractStatusViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);

        }
    };

    //银行卡管理
    @OnClick(R.id.bank_management_button_container)
    private View.OnClickListener bankManagementOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,BankCardStatusViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);
        }
    };
}
