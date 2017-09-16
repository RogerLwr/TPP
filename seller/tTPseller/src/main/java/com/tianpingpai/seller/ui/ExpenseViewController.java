package com.tianpingpai.seller.ui;


import android.support.design.widget.TabLayout;
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
import com.tianpingpai.seller.adapter.ExpenseAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@ActionBar(title = "支出金额")
@Statistics(page = "支出明细")
@Layout(id = R.layout.ui_journal)
public class ExpenseViewController extends BaseViewController{

    public static final String KEY_YEAR = "Key.year";
    public static final String KEY_MONTH = "Key.month";

    private int year;
    private int month;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ExpenseAdapter outcomeAdapter = new ExpenseAdapter();
    private ListView expenseListView;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        year = getActivity().getIntent().getIntExtra(KEY_YEAR, 2016);
        month = getActivity().getIntent().getIntExtra(KEY_MONTH, 1);
        outcomeAdapter.setPageControl(pageControl);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);
        expenseListView = (ListView) rootView.findViewById(R.id.journal_list_view);
        expenseListView.setAdapter(outcomeAdapter);
        expenseListView.setOnItemClickListener(onItemClickListener);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        showContent();
    }

    private void loadPage(int page){
        String url = ContextProvider.getBaseURL() + "/api/saler/expense/pay";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,httpListener);
        req.setAttachment(page);
        req.setAttachment(expenseListView.getAdapter());
        req.setParser(new JSONListParser());
        req.addParam("year", String.valueOf(year));
        req.addParam("month", String.valueOf(month));
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize",String.valueOf(10));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> httpListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            Log.e("xx","data" + data.getModels());
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                ExpenseAdapter adapter = request.getAttachment(ExpenseAdapter.class);
                if(request.getAttachment(Integer.class) == 1){
                    adapter.clear();
                }
                request.getAttachment(ExpenseAdapter.class).setData(data);
            }else{
                ResultHandler.handleError(data, ExpenseViewController.this);
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
//            intent.putExtra(ContainerActivity.KEY_FRAGMENT_CLASS,OrderStatusFragment.class);
//            startActivity(intent);
        }
    };
}
