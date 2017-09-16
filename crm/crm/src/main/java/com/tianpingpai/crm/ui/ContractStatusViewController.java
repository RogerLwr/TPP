package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.ContractItemAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;

@ActionBar(title = "合同管理")
@Layout(id = R.layout.view_controller_contract_status)
public class ContractStatusViewController extends CrmBaseViewController {

    public static final String KEY_CUSTOMER = "key.Customer";
    private CustomerModel customerModel;

    @Binding(id =R.id.contract_list_view)
    private ListView contractListView;
    @Binding(id = R.id.contract_button)
    private Button addContractButton;

    private ContractItemAdapter contractAdapter = new ContractItemAdapter();


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(ContractDetailViewController.KEY_CUSTOMER);
        contractAdapter.setCustomerModel(customerModel);
        contractAdapter.setActivity(getActivity());
        contractListView.setAdapter(contractAdapter);
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

        String url = URLApi.getBaseUrl()+"/crm/customer/list_contract";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,listener);

        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.addParam("user_id", "" + customerModel.getUserId());
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {

            hideLoading();
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if (data.isSuccess()) {
                contractAdapter.setData(data);
                if(data.getModels().size()>0){
                    addContractButton.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressWarnings("unused")
    @OnClick(R.id.contract_button)
    private View.OnClickListener contractButton = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,UploadContractViewController.class);
            intent.putExtra(KEY_CUSTOMER, customerModel);
            getActivity().startActivity(intent);
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
