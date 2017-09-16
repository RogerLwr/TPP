package com.tianpingpai.crm.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.WithdrawalAdapter;
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
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;

@ActionBar(title = "提现记录")
@Layout(id = R.layout.view_controller_withdrawal)
public class WithdrawalViewController extends CrmBaseViewController {

    private CustomerModel cm;

    private SwipeRefreshLayout refreshLayout;
    private View emptyView;

    private TextView filterButton;

    private WithdrawalAdapter wa;

    private int date = -1;

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadDate(date,page);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadDate(date,1);
        }
    };

    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();
        filterButton = (TextView)rootView.findViewById(R.id.ab_right_button);
        filterButton.setOnClickListener(rightOnClickListener);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);
        emptyView = rootView.findViewById(R.id.empty_view);
        cm = (CustomerModel)getActivity().getIntent().getSerializableExtra(CustomerDetailViewController.KEY_CUSTOMER);
        ListView withdrawalListView = (ListView) rootView.findViewById(R.id.withdrawal_list_view);
        wa = new WithdrawalAdapter();
        //TODO
//        wa.setName(cm.getDisplayName());
        wa.setPageControl(mPageControl);
        withdrawalListView.setAdapter(wa);
        loadDate(date,1);
    }

    private View.OnClickListener rightOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            ActionSheet actionSheet = getActionSheet(true);
            actionSheet.setHeight(getView().getHeight());
            actionSheet.setWidth(getView().getWidth() * 3 / 4);
            SelectDateViewController selectDateViewController = new SelectDateViewController();
            selectDateViewController.setActivity(getActivity());
            selectDateViewController.setActionSheet(actionSheet);
            selectDateViewController.setOnSelectDateListener(new SelectDateViewController.OnSelectDateListener() {
                @Override
                public void onSelectDate(String s, int id) {
                    filterButton.setText(s);
                    loadDate(id - 1, 1);
                    date = id - 1;
                }
            });

            actionSheet.setViewController(selectDateViewController);
            actionSheet.setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            actionSheet.show();
        }
    };

    public void loadDate(int dateType,int page){
        String url = URLApi.getBaseUrl()+"/crm/customer/list_trade_log";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,listener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(true);
        req.setParser(parser);
        req.addParam("user_id", "" + cm.getUserId());
        req.addParam("user_type",""+0);
        req.addParam("date_type",""+dateType);
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



    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>(){
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                wa.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            else if(data.getCode() == 1){
                UserManager.getInstance().logout();
                getActivity().finish();
            }
            if (data.isSuccess()) {
//                contractAdapter.setData(data);
                wa.setData(data);
                Log.e("xx", "121--------data=" + data.getModels());
//                Log.e("xx", "183----------是否为空=" + contractAdapter.isEmpty());
                if (wa.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };
}
