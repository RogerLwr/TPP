package com.tianpingpai.seller.ui;


import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.adapter.OrderSingleBuyerAdapter;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

/**
 * 我的 单个 下单客户(客户订单)
 */
@ActionBar(title = "客户订单")
@Layout(id = R.layout.ui_order_buyer)
@Statistics(page = "下单客户")
public class OrderOfBuyerViewController extends BaseViewController {

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    public static final String KEY_YEAR = "Key.year";
    public static final String KEY_MONTH = "Key.month";
    public static final String KEY_USER_ID = "Key.userId";
    public static final String KEY_USER_NAME = "Key.userName";

    int userId;
    private int year;
    private int month;
    private String userName;

    @Binding(id = R.id.buyer_name_text_view)
    private TextView buyerNameTextView;

    private OrderSingleBuyerAdapter allDataAdapter = new OrderSingleBuyerAdapter();

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        year = a.getIntent().getIntExtra(KEY_YEAR, 2015);
        month = a.getIntent().getIntExtra(KEY_MONTH, 9);
        userId = a.getIntent().getIntExtra(KEY_USER_ID, 0);
        userName = a.getIntent().getStringExtra(KEY_USER_NAME);
        allDataAdapter.setPageControl(pageControl);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        buyerNameTextView.setText(userName);
        ListView orderBuyerListView = (ListView) rootView.findViewById(R.id.order_buyer_list_view);
        orderBuyerListView.setAdapter(allDataAdapter);
        orderBuyerListView.setOnItemClickListener(onItemClickListener);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        showContent();
    }

    private void loadOrderData(int page){
        String url = ContextProvider.getBaseURL() + "/api/saler/expense/orderDetail";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, orderListener);
        req.setAttachment(page);
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("user_id", String.valueOf(userId));
        req.addParam("year", String.valueOf(year));
        req.addParam("month", String.valueOf(month));
//        req.setErrorListener(new CommonErrorHandler(OrderOfBuyerViewController.this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> orderListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                if(request.getAttachment(Integer.class) == 1){
                    allDataAdapter.clear();
                }
                allDataAdapter.setData(data);

            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadOrderData(1);
        }
    };

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadOrderData(page);
        }
    };


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent = new Intent(getActivity(), ContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT,OrderStatusFragment.class);
//            startActivity(intent);
        }
    };

}
