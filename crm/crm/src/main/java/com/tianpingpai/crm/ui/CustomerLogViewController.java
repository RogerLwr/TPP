package com.tianpingpai.crm.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerLogAdapter;
import com.tianpingpai.http.HttpError;
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
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;

//客户日志查看
@SuppressWarnings("unused")
@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_logs)
@ActionBar(title = "客户日志")
@Layout(id = R.layout.view_controller_contract_manager)
public class CustomerLogViewController extends BaseViewController {

    private SwipeRefreshLayout refreshLayout;

    private CustomerLogAdapter adapter = new CustomerLogAdapter();

    private CustomerModel customerModel;

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadDate(page);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        customerModel = (CustomerModel)getActivity().getIntent().getSerializableExtra(CustomerDetailViewController.KEY_CUSTOMER);
        View rootView = getView();
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);
        ListView logListView = (ListView)rootView.findViewById(R.id.contract_list_view);
        adapter.setActivity(getActivity());
        adapter.setPageControl(mPageControl);
        logListView.setAdapter(adapter);
        loadDate(1);
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadDate(1);
        }
    };

    public void loadDate(int page){

        String url = URLApi.getBaseUrl() +"/crm/customer/list_customer_log";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,contractlistener);
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.addParam("customer_id", "" + customerModel.getUserId());
        req.addParam("customer_type", "" + customerModel.getUserType());
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize",String.valueOf(10));
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.e("xx", "url:" + req.getUrl());
        if (page == 1) {// 1 means refresh
            req.setAttachment(Boolean.TRUE);
            showLoading();
        }
        VolleyDispatcher.getInstance().dispatch(req);

    }

    HttpRequest.ResultListener<ListResult<Model>> contractlistener = new HttpRequest.ResultListener<ListResult<Model>>(){

        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                adapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            else if(data.getCode() == 1){
                UserManager.getInstance().logout();
                getActivity().finish();
            }
            if (data.isSuccess()) {
                adapter.setData(data);
                Log.e("xx", "121--------data=" + data.getModels());
//                Log.e("xx", "183----------是否为空=" + contractAdapter.isEmpty());
                if (adapter.isEmpty()) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };
}
