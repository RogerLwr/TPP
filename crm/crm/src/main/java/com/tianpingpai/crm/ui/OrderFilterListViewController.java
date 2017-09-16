package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.OrdersAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_orders)
@Layout(id = R.layout.view_controll_order_list)
public class OrderFilterListViewController extends CrmBaseViewController {

    private SwipeRefreshLayout refreshLayout;

    private ArrayList<String> myArrayList = new ArrayList<>();

    public static final String KEY_CUSTOMER = "Key.customer";
    CustomerModel mCustomer;
    public static final String KEY_USER_TYPE = "Kry.userType";
    private String userType;
    public static final String KEY_ORDER_STATUS = "Key.orderStatus";
    private String orderStatus;
    public static final String KEY_DATE_TYPE = "Key.dateType";
    private String dateType;
    public static final String KEY_TITLE = "Key.title";
    public static final String KEY_CUSTOMER_ID = "Key.customer_id";//客户id
    private String customer_id;
    public static final String KEY_ORDER_TYPE = "Key.orderType";// 订单类型 0 订单列表  1 新客户订单列表 2 某个客户的订单
    private int customerType = 0;

    private OrdersAdapter ordersAdapter = new OrdersAdapter();
    private OrdersFilterViewController filterViewController1509;

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            getOrders(page);
        }
    };

    private TextView rightBtn;

    @Binding(id = R.id.select_container)
    private View selectContainer;
    @Binding(id = R.id.select_listView)
    private ListView selectListView;
    private ImageButton upOrDown;

    {
        myArrayList.add("买家采购订单");
        myArrayList.add("上游采购订单");
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();

        orderStatus = getActivity().getIntent().getStringExtra(KEY_ORDER_STATUS);
        dateType = getActivity().getIntent().getStringExtra(KEY_DATE_TYPE);
        String title = getActivity().getIntent().getStringExtra(KEY_TITLE);
        customer_id = getActivity().getIntent().getStringExtra(KEY_CUSTOMER_ID);
        customerType = getActivity().getIntent().getIntExtra(KEY_ORDER_TYPE, 0);
        userType = getActivity().getIntent().getStringExtra(KEY_USER_TYPE);
//        mCustomer = (CustomerModel)getActivity().getIntent().getSerializableExtra(KEY_CUSTOMER);
        mCustomer = new CustomerModel();
        mCustomer.setUserType(1);
        Log.e("xx", "102-----------orderStatus=" + orderStatus + ", dateType=" + dateType);

        View topView = setActionBarLayout(R.layout.ab_back_title_right);
        rightBtn = (TextView) topView.findViewById(R.id.ab_right_button);
        rightBtn.setText("筛选");
        setTitle(title);
        rightBtn.setOnClickListener(filterButtonListener);

        upOrDown = (ImageButton)topView.findViewById(R.id.drop_view_button);
        upOrDown.setOnClickListener(upOrDownOnClickListener);
        if(null!=mCustomer){
            if(1==mCustomer.getGrade()){
                upOrDown.setVisibility(View.VISIBLE);
                setTitle("客户订单");
            }else if(3==mCustomer.getGrade()){
                upOrDown.setVisibility(View.VISIBLE);
                setTitle("上游采购订单");
            }
        }

        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, myArrayList);
        selectListView.setAdapter(myArrayAdapter);
        selectListView.setOnItemClickListener(selectOnItemClickListener);

        ListView ordersListView = (ListView) rootView
                .findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(ordersAdapter);
        ordersAdapter.setCustomerModel(mCustomer);
        ordersAdapter.setPageControl(mPageControl);
        ordersAdapter.setActivity(getActivity());
        ordersListView.setAdapter(ordersAdapter);
        ordersListView.setOnItemClickListener(ordersItemClickListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);

        UserManager.getInstance().registerListener(userInfoListener);
        refreshLayoutControl.triggerRefresh();

    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userInfoListener);
    }

    private void getOrders(int page) {
        if (UserManager.getInstance().getCurrentUser() == null) {
            ordersAdapter.setData(null);
            refreshLayout.setRefreshing(false);
            return;
        }
        String url = URLApi.Order.list();
        switch (customerType) {
            case 0:
                url = URLApi.Order.list();
                break;
            case 1:
                url = URLApi.getBaseUrl() + "/crm/order/getNewCustomerOrder";
                break;
            case 2:
                url = URLApi.getBaseUrl() + "/crm/customer/order";
                rightBtn.setVisibility(View.GONE);
                break;
            case 3:
                url = URLApi.getBaseUrl() + "/crm/order/purchase_list";
                rightBtn.setVisibility(View.GONE);
                break;
        }

        final HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
//        ListParser<Model> parser = new ListParser<>(
//                OrderModel.class);
        req.setParser(new JSONListParser());

        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        if (filterViewController1509 != null) {
            req.addParam("date_type", filterViewController1509.getTimeType() + "");
            req.addParam("status", filterViewController1509.getOrderType() + "");
            req.addParam("user_type", filterViewController1509.getUserType() + "");
            if (customerType == 2) {
                req.addParam("customer_id", customer_id);
            } else {
                req.addParam("marketer_ids", filterViewController1509.getMarketerIds());
            }
        } else {
            req.addParam("status", orderStatus);
            req.addParam("date_type", dateType);
            req.addParam("user_type", userType);//默认是买家
            req.addParam("customer_id", customer_id);
        }

        req.addParam("pageNo", String.valueOf(page));
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());

        if (page == 1) {//1 means reload
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
            showLoading();
        }
        Log.e("xx", "url:" + req.getUrl());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private AdapterView.OnItemClickListener ordersItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Intent intent = new Intent(getActivity(),
                    ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,
                    OrderDetailViewController.class);
            intent.putExtra(OrderDetailViewController.KEY_ORDER,
                    ordersAdapter.getItem(position).toJsonString());
            getActivity().startActivity(intent);
        }
    };

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                ordersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            } else if (data.getCode() == 1) {
                UserManager.getInstance().logout();
            }
            if (data.isSuccess()) {
                ordersAdapter.setData(data);
                if (filterViewController1509 != null) {
//                    titleTV.setText(filterPopup.getOrderStatus() + "订单");
                    setTitle(filterViewController1509.getOrderStatus() + "订单");
                    mCustomer.setUserType(filterViewController1509.getUserType());
                }
                if (ordersAdapter.isEmpty()) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            } else {
                ordersAdapter.setData(data);
                Toast.makeText(ContextProvider.getContext(), "" + data.getDesc(), Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            refreshLayoutControl.triggerRefresh();
//            if (filterViewController1509 != null) {
                //TODO
//                filterViewController1509.notifyUserChange();
//            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getOrders(1);
        }
    };

    private View.OnClickListener filterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (filterViewController1509 == null) {
                filterViewController1509 = new OrdersFilterViewController();
                filterViewController1509.setActivity(getActivity());
                filterViewController1509.setActionSheet(getActionSheet(true));
                filterViewController1509.setOnConditionSelectedListener(new OrdersFilterViewController.OnConditionSelectedListener() {
                    @Override
                    public void onConditionSelected() {
                        getOrders(1);
                    }
                });
                getActionSheet(true).setViewController(filterViewController1509);
                getActionSheet(true).setWidth(DimensionUtil.dip2px(300));
                getActionSheet(true).setHeight(getView().getHeight());
                getActionSheet(true).setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            }
            getActionSheet(true).show();
        }
    };
    private View.OnClickListener upOrDownOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showTopButton();
        }
    };

    String grade = "1";
    private AdapterView.OnItemClickListener selectOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            showTopButton();
            if(i==0){
                grade = ""+1;
            }else if(i==1){
                grade = ""+3;
            }

        }
    };
    boolean isDown = true;
    private void showTopButton(){
        if(isDown){
            isDown = false;
            selectContainer.setVisibility(View.VISIBLE);
            upOrDown.setImageResource(R.drawable.ar);
        }else{
            isDown = true;
            selectContainer.setVisibility(View.GONE);
            upOrDown.setImageResource(R.drawable.ad);
        }
    }
}
