package com.tianpingpai.seller.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.seller.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.StatisticsOrderAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.OnSelectListener;
import com.tianpingpai.widget.SelectDateViewController;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@SuppressWarnings("unused")
@ActionBar(title = "统计")
@Layout(id = R.layout.ui_statistics)
public class StatisticsViewController extends BaseViewController{

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    @Binding(id = R.id.date_text_view)
    private TextView dateTextView;
    @Binding(id = R.id.total_amount_text_view,format = "{{fee|money}}元")
    private TextView totalAmountTextView;
    @Binding(id = R.id.order_number_text_view,format = "{{order}}单")
    private TextView orderNumberTextView;
    @Binding(id = R.id.buyer_number_text_view,format = "{{buyer}}家")
    private TextView buyerNumberTextView;

    private StatisticsOrderAdapter adapter = new StatisticsOrderAdapter();
    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadOrderList(page);
        }
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadOrderList(1);
            Log.e("xx","onRefresh");
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        dateTextView.setText(String.format("%d-%d", selectDateViewController.getSelectedYear(), selectDateViewController.getSelectedMonth()));

        ListView ordersListView = (ListView) rootView.findViewById(R.id.orders_list_view);
        ordersListView.setAdapter(adapter);
        adapter.setPageControl(pageControl);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.setSwipeRefreshLayout(swipeRefreshLayout);
        loadData();
    }

    private SelectDateViewController selectDateViewController = new SelectDateViewController();
    @OnClick(R.id.date_text_view)
    private View.OnClickListener dateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActionSheet actionSheet = getActionSheet(true);
            selectDateViewController.setActivity(getActivity());
            selectDateViewController.setActionSheet(actionSheet);
            selectDateViewController.setOnSelectListener(onSelectListener);
            actionSheet.setViewController(selectDateViewController);
            actionSheet.show();
        }
    };

    private OnSelectListener onSelectListener = new OnSelectListener() {
        @Override
        public void onSelect() {
            dateTextView.setText(String.format("%d-%d", selectDateViewController.getSelectedYear(), selectDateViewController.getSelectedMonth()));
            loadData();
        }
    };


    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Log.e("xx", "data:" + data.getModel());
            getBinder().bindData(data.getModel());
            loadOrderList(1);
        }
    };

    private void loadData(){
        UserModel user = UserManager.getInstance().getCurrentUser();
        String url = URLApi.STATISTIC;

        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,listener);

        req.addParam("saler_id",user.getUserID());
        req.addParam("year",selectDateViewController.getSelectedYear() + "");
        req.addParam("month",selectDateViewController.getSelectedMonth() + "");
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void loadOrderList(int page){
        String url = URLApi.getBaseUrl() + "/api/saler/statistic/order";
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user != null && user.getGrade() == UserModel.GRADE_1){
            url = URLApi.SaleOrder.ORDER_LIST_INFO;
        }
        if(user == null){
            return;
        }

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,ordersListener);
//        req.addParam("s_user_id",user.getUserID());
//        req.addParam("status",CommonUtil.ORDER_STATUS);//TODO
        req.addParam("page",String.valueOf(page));
        req.addParam("page_size",String.valueOf(CommonUtil.PAGESIZE));
        req.addParam("year",selectDateViewController.getSelectedYear() + "");
        req.addParam("month",selectDateViewController.getSelectedMonth() + "");

        req.setParser(new JSONListParser());
        req.setAttachment(page);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> ordersListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                if(request.getAttachment(Integer.class) == 1){
                    adapter.clear();
                }
                adapter.setData(data);
            }
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
        }
    };
}
