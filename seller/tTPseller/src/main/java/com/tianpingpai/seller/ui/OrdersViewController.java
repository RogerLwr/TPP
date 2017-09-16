package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tianpingpai.seller.R;
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
import com.tianpingpai.seller.adapter.OrdersAdapter;
import com.tianpingpai.seller.adapter.TimeSortAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.TimeSortModel;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

@ActionBar(hidden = true)
@Statistics(page = "订单列表")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_orders)
@Layout(id = R.layout.ui_orders)
public class OrdersViewController extends BaseViewController {

    private String orderState = CommonUtil.NOTACCEPT_SELLER + "";

    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.empty_container)
    private ViewGroup emptyView;
    @Binding(id = R.id.sp_time_sort)
    private Spinner spTimeSort;

    private TimeSortAdapter timeSortAdapter = new TimeSortAdapter();

    private OrdersAdapter ordersAdapter = new OrdersAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<OrderModel> mPageControl = new ModelAdapter.PageControl<OrderModel>() {
        @Override
        public void onLoadPage(int page) {
            getOrders(page);
        }
    };

    @Override
    protected ViewGroup getEmptyContainer() {
        return emptyView;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        ListView ordersListView = (ListView) rootView
                .findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(ordersAdapter);
        ordersAdapter.setPageControl(mPageControl);
        ordersListView.setOnItemClickListener(ordersItemClickListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();

        ArrayList<TimeSortModel> timeSortModels = new ArrayList<>();

        TimeSortModel timeSortModelByCreated = new TimeSortModel();
        timeSortModelByCreated.setTime_sort("BY_CREATED");
        timeSortModelByCreated.setTimeSortName("按下单时间排序");
        timeSortModels.add(timeSortModelByCreated);

        TimeSortModel timeSortModel = new TimeSortModel();
        timeSortModel.setTime_sort("");
        timeSortModel.setTimeSortName("按更新时间排序");
        timeSortModels.add(timeSortModel);

        spTimeSort.setAdapter(timeSortAdapter);
        timeSortAdapter.setModels(timeSortModels);
        spTimeSort.setOnItemSelectedListener(onItemSelectListener);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("待处理"));
        tabLayout.addTab(tabLayout.newTab().setText("待收货"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        tabLayout.addTab(tabLayout.newTab().setText("全部订单"));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        UserManager.getInstance().registerListener(userListener);
        OrderManager.getInstance().registerListener(orderListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userListener);
        OrderManager.getInstance().unregisterListener(orderListener);
    }

    boolean isFirst = true;

    private AdapterView.OnItemSelectedListener onItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.e("xx", "126----------选择了"+timeSortAdapter.getItem(i).getTimeSortName());

            timeSort = timeSortAdapter.getItem(i).getTime_sort();
            if(isFirst){
                isFirst = false;
                if(!timeSort.equals("")){
                    refreshLayoutControl.triggerRefresh();
                }
            }else{
                refreshLayoutControl.triggerRefresh();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    String timeSort = "BY_CREATED";
    private void getOrders(int page) {
        String url = URLApi.GET_ORDER_LIST_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            refreshLayout.setRefreshing(false);
            return;
        }
        if(user.getGrade() == UserModel.GRADE_1){
            url = URLApi.SaleOrder.ORDER_LIST_INFO;
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

        if(orderState != null){
            req.addParam("status", String.valueOf(orderState));
        }

        req.addParam("time_sort", timeSort);
        /*if(user != null && user.getGrade() != UserModel.GRADE_1){
            spTimeSort.setVisibility(View.VISIBLE);
        }else{
            spTimeSort.setVisibility(View.GONE);
        }*/

        req.addParam("page", String.valueOf(page));
        req.addParam("page_size", String.valueOf(CommonUtil.PAGESIZE));

        if (page == 1) {//1 means reload
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
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                ordersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if (data.isSuccess()) {
                ordersAdapter.setData(data);
                Log.e("xx", "121--------data=" + data.getModels());
                Log.e("xx", "183----------是否为空=" + ordersAdapter.isEmpty());
                if (refreshLayout != null) {
                    if (ordersAdapter.isEmpty()) {
                        showEmptyView();
                        refreshLayout.setVisibility(View.GONE);
                    } else {
                        hideEmptyView();
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                ordersAdapter.setRefreshing(false);
                ResultHandler.handleError(data, OrdersViewController.this);
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
                orderState = "1,2";
            } else if (pos == 1) {
                orderState = "3";//TODO
            } else if (pos == 2) {
                orderState = CommonUtil.COMPLETE + ",9,10";
            } else {
                orderState = null;
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
            if(getActivity() == null){
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            if (UserManager.getInstance().isLoggedIn()) {
                intent.putExtra(ContainerActivity.KEY_CONTENT, OrderDetailViewController.class);
                intent.putExtra(OrderDetailViewController.KEY_ORDER_ID, ordersAdapter.getItem(position).getId());
                intent.putExtra(OrderDetailViewController.KEY_ORDER_PAY_DESC, ordersAdapter.getItem(position).getPaymentDesc());
            } else {
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
            }
            getActivity().startActivity(intent);
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event){
                case Login:
                    refreshLayoutControl.triggerRefresh();
                    break;
                case Logout:
                    ordersAdapter.clear();
                    break;
            }

        }
    };

    private ModelStatusListener<ModelEvent, OrderModel> orderListener = new ModelStatusListener<ModelEvent, OrderModel>() {
        @Override
        public void onModelEvent(ModelEvent event, OrderModel model) {
            switch (event) {
                case OnModelUpdate:
                    if (ordersAdapter != null && ordersAdapter.getModels() != null && ordersAdapter.getModels().contains(model)) {
                        refreshLayoutControl.triggerRefresh();
                    }
                    break;
            }
        }
    };
}
