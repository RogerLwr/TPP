package com.tianpingpai.crm.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerAdapter;
import com.tianpingpai.crm.adapter.FilterAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.CustomerEvent;
import com.tianpingpai.manager.CustomerManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;


@SuppressWarnings("unused")
@ActionBar(title = "拜访客户")
@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_customers)
@Layout(id = R.layout.fragment_customers_visited)
public class CustomersVisitedViewController extends CrmBaseViewController {
    private View backButton;
    private String searchText;
    private CustomerAdapter customersAdapter = new CustomerAdapter();
    private ListView customersListView;
    private PopupWindow filterPopupWindow;

    public static final String KEY_DATE_TYPE = "Key.dateType";
    public static final String KEY_CUSTOMER_TYPE = "Key.customerType";
    private String dateType;
    private String customerType;

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadCustomers(page);
        }
    };

    private SwipeRefreshLayout refreshLayout;
    private ModelStatusListener<CustomerEvent, CustomerModel> customerStatusListener = new ModelStatusListener<CustomerEvent, CustomerModel>() {
        @Override
        public void onModelEvent(CustomerEvent event, CustomerModel model) {
            loadCustomers(1);
        }
    };

    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();

        dateType = getActivity().getIntent().getStringExtra(KEY_DATE_TYPE);
        customerType = getActivity().getIntent().getStringExtra(KEY_CUSTOMER_TYPE);

        customersAdapter.setActivity(getActivity());
        customersAdapter.setPageControl(mPageControl);
        customersAdapter.setDateType(dateType);
        customersAdapter.setIsVisited(true);
        customersListView = (ListView) rootView
                .findViewById(R.id.customers_list_view);
        customersListView.setAdapter(customersAdapter);
        customersAdapter.setPageControl(mPageControl);

        refreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);

        loadCustomers(1);
        CustomerManager.getInstance().registerListener(customerStatusListener);//TODO
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userInfoListener);
        if (filterPopupWindow != null && filterPopupWindow.isShowing()) {
            filterPopupWindow.dismiss();
        }
    }

    HttpRequest.ResultListener<ListResult<Model>> customersListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {

            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                customersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            else if(data.getCode() == 1){
                UserManager.getInstance().logout();
            }
            if (data.isSuccess()) {
                customersAdapter.setDateType(dateType);
                customersAdapter.setData(data);
                if(customersAdapter.isEmpty()){
                    showEmptyView();
                }else {
                    hideEmptyView();
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(),
                        Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadCustomers(1);
        }
    };

    private void loadCustomers(int page) {
        if (UserManager.getInstance().getCurrentUser() == null) {
            customersAdapter.setData(null);
            refreshLayout.setRefreshing(false);

            return;
        }

        String url = URLApi.getBaseUrl() + "/crm/customer/getVisitedCustomer";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(
                url, customersListener);
        CommonErrorHandler handler = new CommonErrorHandler(this);
        handler.setSwipeRefreshLayout(refreshLayout);
        req.setErrorListener(handler);
        JSONListParser parser = new JSONListParser();
        req.addParam("date_type", dateType);
//        req.addParam("user_type", "1");//默认是买家
        req.addParam("user_type",customerType);
        req.setParser(parser);

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


    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            loadCustomers(1);
        }
    };

    private FilterAdapter filterAdapter = new FilterAdapter();

    /*
    private void showFilterPopup() {
        int tabBarHeight = (int) getActivity().getResources()
                .getDimension(R.dimen.tab_tab_height);
        if (filterPopupWindow == null) {
            @SuppressLint("InflateParams") View vv = getActivity().getLayoutInflater().inflate(
                    R.layout.menu_customers_filter, null);
            int w = customersListView.getWidth();

            int h = (customersListView.getHeight() + tabBarHeight + backButton
                    .getHeight());
            filterPopupWindow = new PopupWindow(vv, w, h);
            filterPopupWindow.setBackgroundDrawable(new ColorDrawable());
            vv.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && keyCode == KeyEvent.KEYCODE_BACK) {
                        filterPopupWindow.dismiss();
                    }
                    return true;
                }
            });
            ListView lv = (ListView) vv.findViewById(R.id.filter_list_view);
            lv.setAdapter(filterAdapter);
            vv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    filterPopupWindow.dismiss();
                }
            });
            vv.findViewById(R.id.done_button).setOnClickListener(
                    doneButtonListener);
        }
        filterPopupWindow.showAsDropDown(backButton, 0,
                -backButton.getHeight());
    }
    */

//    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            filterPopupWindow.dismiss();
//            loadCustomers(1);
//        }
//    };

//    @Override
//    public boolean onBackKeyDown(Activity a) {
//        if (filterPopupWindow != null && filterPopupWindow.isShowing()) {
//            filterPopupWindow.dismiss();
//            return true;
//        }
//        return false;
//    }
}
