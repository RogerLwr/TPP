package com.tianpingpai.crm.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.CustomerAdapter;
import com.tianpingpai.crm.adapter.CustomerFilterAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.CustomerEvent;
import com.tianpingpai.manager.CustomerManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.DimensionUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_customers)
@Layout(id = R.layout.fragment_customers)
public class SellerCustomersViewController extends CrmBaseViewController {

    public static final String KEY_DATE_TYPE = "Key.dateType";
    public static final String KEY_USER_TYPE = "Key.userType";
    public static final String KEY_CUSTOMER_TYPE = "Key.customerType";
    public static final String KEY_IS_ITEM_CLICK = "Key.isClick"; //是否可点击 (只有冲个人中心进来  item 才可以点击)
    public static final String KEY_TITLE = "Key.title";
    public static final String KEY_IS_VISIBLE_BTN = "Key.isVisibleBtn";
    public static final String KEY_APPROVAL_GONE = "Key.approvalGone";

    private String dateType;
    private String userType = "1";// 默认买家
    private String title;
    private int customerType = 0;
    private String searchText;

    private TextView rightBtn;
    boolean isSearchClose;
    private CustomerAdapter customersAdapter = new CustomerAdapter();
    private SellerCustomerFilterViewController filterViewController;

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

    private ModelAdapter.PageControl<Model> searchPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            search(searchText, page);
        }
    };

    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();

        dateType = getActivity().getIntent().getStringExtra(KEY_DATE_TYPE);
        title = getActivity().getIntent().getStringExtra(KEY_TITLE);
        customerType = getActivity().getIntent().getIntExtra(KEY_CUSTOMER_TYPE, 0);
        boolean isItemClick = getActivity().getIntent().getBooleanExtra(KEY_IS_ITEM_CLICK, false);
        userType = getActivity().getIntent().getStringExtra(KEY_USER_TYPE);
        boolean isVisibleBtn = getActivity().getIntent().getBooleanExtra(KEY_IS_VISIBLE_BTN, true);
        boolean isApprovalGone = getActivity().getIntent().getBooleanExtra(KEY_APPROVAL_GONE,false);

        customersAdapter.setActivity(getActivity());
        customersAdapter.setPageControl(mPageControl);
        customersAdapter.setIsItemClick(isItemClick);
        customersAdapter.setIsVisibleBtn(isVisibleBtn);
        customersAdapter.setIsApprovalGone(isApprovalGone);
        customersAdapter.setDateType(dateType);
        ListView customersListView = (ListView) rootView.findViewById(R.id.customers_list_view);
        customersListView.setAdapter(customersAdapter);

        refreshLayout = (SwipeRefreshLayout) rootView
                .findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);

        loadCustomers(1);
        CustomerManager.getInstance().registerListener(customerStatusListener);//TODO

        View topView = setActionBarLayout(R.layout.ab_back_title_right);
        rightBtn = (TextView) topView.findViewById(R.id.ab_right_button);
        rightBtn.setText("筛选");
        setTitle(title);

        if (customerType == 0){
            SearchView searchView = (SearchView) topView.findViewById(R.id.search_view);

            isSearchClose = true;

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
//                Toast.makeText(ContextProvider.getContext(), "关闭", Toast.LENGTH_LONG).show();
                    isSearchClose = true;
                    setTitle(title);
                    customersAdapter.setPageControl(mPageControl);
                    rightBtn.setClickable(true);
                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("xx", "152--------点击事件");
                    setTitle("");
                    isSearchClose = false;
                    customersAdapter.setPageControl(searchPageControl);
                    rightBtn.setClickable(false);
                }
            });
            searchView.setOnQueryTextListener(searchQueryListener);
            searchView.setVisibility(View.VISIBLE);
        }

        rightBtn.setOnClickListener(filterButtonListener);
    }

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String q) {
            searchText = q;
            search(q, 1);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String arg0) {
            return false;
        }
    };

    private void search(String queryString, int page) {
        if (UserManager.getInstance().getCurrentUser() == null) {
            customersAdapter.setData(null);
            refreshLayout.setRefreshing(false);
            return;
        }
        if(null==queryString||"".equals(queryString)){
            Toast.makeText(getActivity(),"搜索内容不能为空!",Toast.LENGTH_SHORT).show();
            return;
        }

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.Customer.getMyCustomers(), customersListener);
        CommonErrorHandler handler = new CommonErrorHandler(this);
        handler.setSwipeRefreshLayout(refreshLayout);
        req.setErrorListener(handler);
        try {
            req.addParam("display_name", URLEncoder.encode(queryString, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);

        if(filterViewController!=null){

            CustomerFilterAdapter filterAdapter = filterViewController.getAdapter();
            if(filterAdapter.getDateType() != -1) {
                req.addParam("date_type", filterAdapter.getDateType() + "");
            }
            req.addParam("date_type", filterAdapter.getDateType() + "");
            if (filterAdapter.getMarketerIds() != null) {
                req.addParam("marketer_ids", filterAdapter.getMarketerIds());
            }
            MarketModel marketModel = filterAdapter.getSelectedMarket();
            if (marketModel != null && marketModel.getId() != -1) {
                req.addParam("market_id", marketModel.getId() + "");
            }
        }else {
            req.addParam("date_type", dateType + "");
//            req.addParam("user_type", userType);//默认是买家1 个人中心进入的我的客户是全部  -1
        }
        req.addParam("user_type", userType);//默认买家和卖家
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
        }
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("xx", "loading customers");
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userInfoListener);
    }

    HttpRequest.ResultListener<ListResult<Model>> customersListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {
            if(data.getCode() == 1){
                UserManager.getInstance().loginExpired(getActivity());
                getActivity().finish();
            }
            if(request.getAttachment(Boolean.class) == Boolean.TRUE){
                customersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if (data.isSuccess()) {
                customersAdapter.setData(data);
                customersAdapter.setDateType(dateType);
                Log.e("xx", "121--------data=" + data.getModels());
                Log.e("xx", "183----------是否为空=" + customersAdapter.isEmpty());
                if (customersAdapter.isEmpty()) {
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
        String url = "";
        switch (customerType) {
            case 0:
                url = URLApi.Customer.getMyCustomers();
                break;
            case 1:
                url = URLApi.getBaseUrl() + "/crm/customer/getNewCustomer";
                break;
            case 2:
                url = URLApi.getBaseUrl() + "/crm/customer/getOrderedCustomer";
                break;
        }

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, customersListener);
        if (filterViewController != null) {
            CustomerFilterAdapter filterAdapter = filterViewController.getAdapter();
            if(filterAdapter.getUserType() != -1){
                //TODO
                req.addParam("user_type", filterAdapter.getUserType() + "");
            }
            if(filterAdapter.getDateType() != -1) {
                req.addParam("date_type", filterAdapter.getDateType() + "");
            }
            req.addParam("user_type", filterAdapter.getUserType() + "");
            req.addParam("date_type", filterAdapter.getDateType() + "");
            if (filterAdapter.getMarketerIds() != null) {
                req.addParam("marketer_ids", filterAdapter.getMarketerIds());
            }
            MarketModel marketModel = filterAdapter.getSelectedMarket();
            if (marketModel != null && marketModel.getId() != -1) {
                req.addParam("market_id", marketModel.getId() + "");
            }
        } else {
            req.addParam("date_type", dateType + "");
            req.addParam("user_type", userType);//默认是买家1 个人中心进入的我的客户是全部  -1
        }

        req.addParam("user_type", userType);//默认是买家1 个人中心进入的我的客户是全部  -1

        JSONListParser parser = new JSONListParser();
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
            showLoading();
            req.setAttachment(Boolean.TRUE);
        }
        VolleyDispatcher.getInstance().dispatch(req);
        Log.e("xx", "loading customers");
    }

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            loadCustomers(1);
        }
    };

    private View.OnClickListener filterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if("0".equals(userType)){
//
//            }else if("1".equals(userType)){
//
//            }
            showFilter();
        }
    };

    private void showFilter() {
        if(filterViewController == null){
            filterViewController = new SellerCustomerFilterViewController();
            filterViewController.setActionSheet(getActionSheet(true));
            filterViewController.setOnConditionSelectedListener(new SellerCustomerFilterViewController.OnConditionSelectedListener() {
                @Override
                public void onConditionSelected() {
                    loadCustomers(1);
                }
            });
            filterViewController.setActivity(getActivity());
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            getActionSheet(true).setViewController(filterViewController);
            getActionSheet(true).setWidth(DimensionUtil.dip2px(300));
        }
        getActionSheet(true).show();
    }
}
