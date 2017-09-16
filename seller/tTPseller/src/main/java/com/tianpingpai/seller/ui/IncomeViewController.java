package com.tianpingpai.seller.ui;


import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.adapter.IncomeMnyAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

/**
 * 收入金额
 */
@ActionBar(title = "")
@Statistics(page = "收入金额")
@Layout(id = R.layout.ui_income)
public class IncomeViewController extends BaseViewController {

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    //category:类型 3补贴4红包5订单
    public static final int ALLOWANCE = 3;
    public static final int RED_PACK = 4;
    public static final int ORDER = 5;

    public static final String KEY_YEAR = "Key.year";
    public static final String KEY_MONTH = "Key.month";
    public static final String KEY_IS_ARRIVED = "Key.isArrived";

    private int year;
    private int month;
    private boolean isArrived;

    private IncomeMnyAdapter incomeAdapter = new IncomeMnyAdapter();

    private ListView incomeListView;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        incomeListView = (ListView) rootView.findViewById(R.id.income_list_view);
        incomeListView.setOnItemClickListener(onItemClickListener);

        isArrived = getActivity().getIntent().getBooleanExtra(KEY_IS_ARRIVED, false);
        year = getActivity().getIntent().getIntExtra(KEY_YEAR, 2015);
        month = getActivity().getIntent().getIntExtra(KEY_MONTH, 9);

        if(isArrived){
            setTitle("收入金额(已到账)");
        }else{
            setTitle("收入金额(未到账)");
        }
        incomeAdapter.setArrived(isArrived);
        incomeAdapter.setPageControl(pageControl);
        incomeListView.setAdapter(incomeAdapter);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        showContent();
    }

    private void loadPage(int page){
        String url;
        if(isArrived){
            url = ContextProvider.getBaseURL() + "/api/saler/expense/arrived";
        }else{
            url = ContextProvider.getBaseURL() + "/api/saler/expense/notArrived";
        }
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,httpListener);
        req.setAttachment(page);
        req.setAttachment(incomeListView.getAdapter());
        req.setParser(new JSONListParser());
        req.addParam("year", String.valueOf(year));
        req.addParam("month", String.valueOf(month));
        if(isArrived){
            req.addParam("pageNo", String.valueOf(page));
            req.addParam("pageSize",String.valueOf(10));
        }
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> httpListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            Log.e("xx","data" + data.getModels());
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                IncomeMnyAdapter adapter = request.getAttachment(IncomeMnyAdapter.class);
                if(request.getAttachment(Integer.class) == 1){
                    adapter.clear();
                }
                request.getAttachment(IncomeMnyAdapter.class).setData(data);
            }else{
                ResultHandler.handleError(data, IncomeViewController.this);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPage(1);
        }
    };

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadPage(page);
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
