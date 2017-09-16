package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;

import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.BankAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@ActionBar(title = "银行卡管理")
@Layout(id = R.layout.view_controller_bank_card_status)
public class BankCardStatusViewController extends BaseViewController {

    private CustomerModel customerModel;
    private BankAdapter bankAdapter = new BankAdapter();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(ContractDetailViewController.KEY_CUSTOMER);
        bankAdapter.setActivity(getActivity());
        bankAdapter.setCustomerModel(customerModel);
        ListView bankCardListView = (ListView) rootView.findViewById(R.id.bank_card_list_view);
        bankCardListView.setAdapter(bankAdapter);
        loadData();
        ModelManager.getModelInstance().registerListener(modelStatusListener);
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        ModelManager.getModelInstance().unregisterListener(modelStatusListener);
    }

    public void loadData(){
        if (UserManager.getInstance().getCurrentUser() == null) {
//            refreshLayout.setRefreshing(false);
            return;
        }
        String url = URLApi.getBaseUrl() + "/crm/customer/list_bankcard";

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,listener);

        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);

//        req.addParam("user_id", 1 + "");
        req.addParam("user_id", customerModel.getUserId()+"");
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    @OnClick(R.id.update_bank_card_button)
    private View.OnClickListener buttonOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,UploadBankCardViewController.class);
            intent.putExtra(UploadBankCardViewController.KEY_IS_CHECK_OUT,false);
            intent.putExtra(ContractDetailViewController.KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);
        }
    };

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            hideLoading();
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if(data.isSuccess()){
                bankAdapter.setModels(data.getModels());
            }
        }
    };

    private ModelStatusListener<ModelEvent, Model> modelStatusListener = new ModelStatusListener<ModelEvent, Model>() {
        @Override
        public void onModelEvent(ModelEvent event, Model model) {
            switch (event){
                case OnModelUpdate:
                    loadData();
                    break;
            }
        }
    };

}
