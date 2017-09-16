package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.OrdersAdapter;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Layout(id = R.layout.ui_orders)
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_order)
@Statistics(page = "订单列表")
public class OrdersViewController extends BaseViewController {

    public static final String KEY_ORDER_FORM = "key.orderForm";
    /**
     * 订单状态  未付款(待付款) "0"   待发货  "1,2"  代收货 "3"  待评价 "4"
     */
    private String orderState = "";
    /**
     * 时间类型  一周订单 "1"  一月订单 "2"  更早订单 "3"  全部订单  ""
     */
    private String date_type = "1";
    private SwipeRefreshLayout refreshLayout;

    private OrdersAdapter ordersAdapter = new OrdersAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<OrderModel> mPageControl = new ModelAdapter.PageControl<OrderModel>() {
        @Override
        public void onLoadPage(int page) {
            getOrders(page);
        }
    };

    private ViewGroup emptyContainer;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        setActionBarLayout(R.layout.ab_title_white);
        ListView ordersListView = (ListView) rootView
                .findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(ordersAdapter);
        ordersAdapter.setPageControl(mPageControl);
        ordersListView.setOnItemClickListener(ordersItemClickListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("一周订单"));
        tabLayout.addTab(tabLayout.newTab().setText("一月订单"));
        tabLayout.addTab(tabLayout.newTab().setText("更早订单"));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        UserManager.getInstance().registerListener(userListener);
        OrderManager.getInstance().registerListener(orderListener);

        View dividerLineView = rootView.findViewById(R.id.divider_line);
        emptyContainer = (ViewGroup) rootView.findViewById(R.id.empty_container);

        orderState = getActivity().getIntent().getStringExtra(KEY_ORDER_FORM);
        Log.e("xx", "70--------------------" + orderState);
        if(TextUtils.isEmpty(orderState)){
            setTitle("全部订单");
            orderState = "";
        }else {
            tabLayout.setVisibility(View.GONE);
            dividerLineView.setVisibility(View.GONE);
            if(orderState.equals("0")){
                setTitle("待付款");
            }else if(orderState.equals("3")){
                setTitle("待收货");
            }else if(orderState.equals("4")){
                setTitle("待评价");
            }else {
                setTitle("待发货");
            }
        }
        ordersAdapter.setActivity(getActivity());
        ordersAdapter.setBaseViewController(OrdersViewController.this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userListener);
        OrderManager.getInstance().unregisterListener(orderListener);
    }

    @Override
    public ViewGroup getEmptyContainer() {
        return emptyContainer;
    }

    private void getOrders(int page) {
        String url = URLUtil.GET_ORDER_FORM_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            refreshLayout.setRefreshing(false);
            return;
        }

        final HttpRequest<ListResult<OrderModel>> req = new HttpRequest<>(
                url, listener);
        ListParser<OrderModel> parser = new ListParser<>(
                OrderModel.class);
        req.setParser(parser);

        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        req.addParam("s_user_id", user.getUserID());
        req.addParam("accessToken", user.getAccessToken());

        req.addParam("status", String.valueOf(orderState));
        if(TextUtils.isEmpty(orderState)){
            req.addParam("date_type", String.valueOf(date_type));
        }else{
            req.addParam("date_type", "");
        }
        req.addParam("page", String.valueOf(page));
        req.addParam("page_size", String.valueOf(CommonUtil.PAGESIZE));

        if (page == 1) {//1 means onReloadData
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<OrderModel>> listener = new HttpRequest.ResultListener<ListResult<OrderModel>>() {
        @Override
        public void onResult(HttpRequest<ListResult<OrderModel>> request,
                             ListResult<OrderModel> data) {
            showContent();
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                ordersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if (data.isSuccess()) {
                ordersAdapter.setData(data);
            } else {
                ordersAdapter.setRefreshing(false);
                ResultHandler.handleError(data, OrdersViewController.this);
            }
            if(ordersAdapter.isEmpty()){
                showEmptyView();
            }else{
                hideEmptyView();
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getOrders(1);
        }
    };

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();
            if (pos == 0) {
                date_type = "1";
            } else if (pos == 1) {
                date_type = "2";
            } else if(pos == 2){
                date_type = "3";//TODO
            }
            refreshLayoutControl.triggerRefresh();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private AdapterView.OnItemClickListener ordersItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if(UserManager.getInstance().isLoggedIn()){
                intent.putExtra(ContainerActivity.KEY_CONTENT,OrderDetailViewController.class);
                intent.putExtra(OrderDetailViewController.KEY_ORDER_ID,ordersAdapter.getItem(position).getId());
                intent.putExtra(OrderDetailViewController.KEY_ORDER_PAY_DESC, ordersAdapter.getItem(position).getPaymentDesc());
            }else{
                intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userListener = new ModelStatusListener<UserEvent,UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            refreshLayoutControl.triggerRefresh();
        }
    };

    private ModelStatusListener<ModelEvent, OrderModel> orderListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            switch (event) {
                case OnModelUpdate:
//                    if (ordersAdapter.getModels().contains(model)) {
                        refreshLayoutControl.triggerRefresh();
//                    }
                    break;
            }
        }
    };
}
