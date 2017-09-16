package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.seller.tools.CommonUtil;
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
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "24小时订单")
@Layout(id = R.layout.ui_orders_by_status)
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_orders)
public class OrdersByStatusViewController extends BaseViewController{

    public static final String KEY_ORDERS_STATE = "ordersState";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PAY_TYPE = "payType";
    public static final String KEY_IS_48HOURS = "48hours";

    private String orderState = CommonUtil.NOTACCEPT_SELLER + "";
    private String payType = null;
    private boolean is48Hours = true;

    private SwipeRefreshLayout refreshLayout;

    private OrdersAdapter ordersAdapter = new OrdersAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<OrderModel> mPageControl = new ModelAdapter.PageControl<OrderModel>() {
        @Override
        public void onLoadPage(int page) {
            getOrders(page);
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderState = a.getIntent().getStringExtra(KEY_ORDERS_STATE);
        payType = a.getIntent().getStringExtra(KEY_PAY_TYPE);
        is48Hours = a.getIntent().getBooleanExtra(KEY_IS_48HOURS,true);
    }


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        TextView titleTextView = (TextView) setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.ab_title_text_view);
        SpannableStringBuilder ssb = new SpannableStringBuilder(getActivity().getIntent().getStringExtra(KEY_TITLE));
        ssb.append("  ");
        int titleLength = ssb.length();
        if(is48Hours) {
            ssb.append("(48小时内)");
        }
        ssb.setSpan(new RelativeSizeSpan(0.8f), titleLength, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        titleTextView.setText(ssb);
        showContent();
        ListView ordersListView = (ListView) rootView
                .findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(ordersAdapter);
        ordersAdapter.setPageControl(mPageControl);
        ordersListView.setOnItemClickListener(ordersItemClickListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        OrderManager.getInstance().registerListener(orderListener);
        UserManager.getInstance().registerListener(userListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OrderManager.getInstance().unregisterListener(orderListener);
        UserManager.getInstance().unregisterListener(userListener);
    }

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
                    if (ordersAdapter != null && ordersAdapter.getModels() != null  && ordersAdapter.getModels().contains(model)) {
                        refreshLayoutControl.triggerRefresh();
                    }
                    break;
            }
        }
    };

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

        if(orderState != null && !orderState.equals("-1") ){
            req.addParam("status", String.valueOf(orderState));
        }
        if(payType != null){
            req.addParam("pay_type",payType);
        }

        if(is48Hours) {
            req.addParam("date_type", "48");
        }
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
                ResultHandler.handleError(data, OrdersByStatusViewController.this);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getOrders(1);
        }
    };

    private AdapterView.OnItemClickListener ordersItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
}
