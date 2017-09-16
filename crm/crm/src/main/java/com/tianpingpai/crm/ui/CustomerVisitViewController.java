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
import com.tianpingpai.crm.adapter.CustomerVisitAdapter;
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
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@ActionBar(layout = R.layout.ab_back_title_right_add_visit,title = "客户拜访")
@Layout(id = R.layout.view_controller_customer_visit)
public class CustomerVisitViewController extends CrmBaseViewController {

    public static final String KEY_CUSTOMER = "key.Customer";
    private CustomerModel customer;

    public static final String KEY_IS_VISITED = "key.IsVisited";

    private SwipeRefreshLayout refreshLayout;
    private CustomerVisitAdapter adapter = new CustomerVisitAdapter();

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    private ModelStatusListener<ModelEvent, Model> modelStatusListener = new ModelStatusListener<ModelEvent, Model>() {
        @Override
        public void onModelEvent(ModelEvent event, Model model) {
            switch (event){
                case OnModelUpdate:
                    loadData(1);
                    break;
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        rootView.findViewById(
                R.id.ab_right_button).setOnClickListener(rightButtonListener);

        customer = (CustomerModel)getActivity().getIntent().getSerializableExtra(KEY_CUSTOMER);
        boolean isVisited = getActivity().getIntent().getBooleanExtra(KEY_IS_VISITED, false);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);
        ListView customerVisitListView = (ListView)rootView.findViewById(R.id.customer_visit_list_view);
        adapter.setPageControl(mPageControl);
        customerVisitListView.setAdapter(adapter);
        if(!isVisited){
            rootView.findViewById(R.id.ab_right_button).setVisibility(View.GONE);
        }

        loadData(1);
        ModelManager.getModelInstance().registerListener(modelStatusListener);

    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        ModelManager.getModelInstance().unregisterListener(modelStatusListener);
    }

    public void loadData(int page){
        CustomerModel customer = (CustomerModel) getActivity().getIntent()
                .getSerializableExtra(KEY_CUSTOMER);
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ContextProvider.getContext(), R.string.login_first,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.Customer.getComments(), listener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(true);
        req.setParser(parser);
        req.setAttachment(page);

        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize", String.valueOf(10));
        req.addParam("customer_id", customer.getId() + "");
        req.addParam("accessToken", user.getAccessToken());

        if (page == 1) {// 1 means refresh
            req.setAttachment(Boolean.TRUE);
            showLoading();
        }
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                refreshLayout.setRefreshing(false);
                hideLoading();

                int page = request.getAttachment(Integer.class);

                if(page == 1){
                    adapter.clear();
                }
                adapter.setData(data);
            }
        }
    };

    private View.OnClickListener rightButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(),
                    ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,
                    AddCommentViewController.class);
            i.putExtra(KEY_CUSTOMER, customer);
            getActivity().startActivity(i);
        }
    };
}
