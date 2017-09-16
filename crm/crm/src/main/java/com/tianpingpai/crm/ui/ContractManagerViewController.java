package com.tianpingpai.crm.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.ContractAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
@SuppressWarnings("unused")
@ActionBar(title = "合同管理")
@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_contract)
@Layout(id = R.layout.view_controller_contract_manager)
public class ContractManagerViewController extends CrmBaseViewController {

    private SwipeRefreshLayout refreshLayout;
    private ContractAdapter contractAdapter = new ContractAdapter();

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadContracts(page);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();

        View view = getView();
        ListView contractListView = (ListView) view.findViewById(R.id.contract_list_view);
        contractAdapter.setPageControl(mPageControl);
        contractAdapter.setActivity(getActivity());
        contractListView.setAdapter(contractAdapter);

//        getView().findViewById(R.id.ab_right_button).setVisibility(View.GONE);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);
        loadContracts(1);
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadContracts(1);
        }
    };

    public void loadContracts(int page) {
        if (UserManager.getInstance().getCurrentUser() == null) {
            contractAdapter.setData(null);
            refreshLayout.setRefreshing(false);

            return;
        }
        String url = URLApi.Customer.getMyCustomers();
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, contractListener);

        JSONListParser parser = new JSONListParser();
        req.setParser(parser);

        req.addParam("date_type", -1 + "");//-1表示全部
        int dateType = -1;
        req.addParam("date_type", dateType + "");
        int userType = 0;
        req.addParam("user_type", userType + "");
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize", String.valueOf(10));
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

    private HttpRequest.ResultListener<ListResult<Model>> contractListener = new HttpRequest.ResultListener<ListResult<Model>>() {

        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            hideLoading();
            if (data.getCode() == 1) {
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                contractAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            } else if (data.getCode() == 1) {
                UserManager.getInstance().logout();
                getActivity().finish();
            }
            if (data.isSuccess()) {
                contractAdapter.setData(data);
                Log.e("xx", "121--------data=" + data.getModels());
                Log.e("xx", "183----------是否为空=" + contractAdapter.isEmpty());
                if (contractAdapter.isEmpty()) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
}
