package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.RegisterSellerAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;


@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_customers)
@Layout(id = R.layout.view_controller_register_seller)
public class RegisterSellerViewController extends CrmBaseViewController {

//    @Binding(id = R.id.refresh_layout)
//    private SwipeRefreshLayout refreshLayout;

    @Binding(id = R.id.register_seller_list_view)
    private ListView registerSellerListView;

    RegisterSellerAdapter registerSellerAdapter = new RegisterSellerAdapter();
    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadCustomers(page);
        }
    };

    private SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayoutControl swipeRefreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadCustomers(1);
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        View actionBar = setActionBarLayout(R.layout.ab_back_title_right_add_visit);
        setTitle("注册卖家列表");
        actionBar.findViewById(R.id.ab_right_button).setOnClickListener(addOnClickListener);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);

        swipeRefreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        swipeRefreshLayoutControl.setOnRefreshListener(mOnRefreshListener);

        registerSellerAdapter.setActivity(getActivity());
        registerSellerAdapter.setPageControl(mPageControl);
        registerSellerListView.setAdapter(registerSellerAdapter);
//        swipeRefreshLayoutControl.triggerRefresh();
        swipeRefreshLayoutControl.triggerRefreshDelayed();
        ModelManager.getModelInstance().registerListener(modelStatusListener);
    }


    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        ModelManager.getModelInstance().unregisterListener(modelStatusListener);
    }

    private ModelStatusListener<ModelEvent, Model> modelStatusListener = new ModelStatusListener<ModelEvent, Model>() {
        @Override
        public void onModelEvent(ModelEvent event, Model model) {
            switch (event){
                case OnModelUpdate:
                    loadCustomers(1);
                    break;
            }
        }
    };

    public void loadCustomers(int page){

        String url = URLApi.getBaseUrl()+"/crm/customer/get_register_salers";

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,listener);
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        JSONListParser parser = new JSONListParser();
        parser.setPaged(false);
        req.setParser(parser);
        VolleyDispatcher.getInstance().dispatch(req);
        if(1==page){
            showLoading();
        }
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            registerSellerAdapter.clear();
            if(1 == data.getCode()){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if(data.isSuccess()){
                registerSellerAdapter.setData(data);
                if(registerSellerAdapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
            }else{
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };

    private View.OnClickListener addOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT, RegisterViewController.class);
            getActivity().startActivity(i);
        }
    };

}
