package com.tianpingpai.seller.ui;


import android.content.Intent;
import android.support.design.widget.TabLayout;
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
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.OrderAllBuyerAdapter;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.OnSelectListener;
import com.tianpingpai.widget.SelectDateViewController;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

/**
 * 我的明细
 */
@SuppressWarnings("unused")
@Statistics(page = "我的明细")
@ActionBar(title = "我的明细")
@Layout(id = R.layout.ui_my_journal)

public class MyJournalViewController extends BaseViewController{

    @Binding(id = R.id.tab_layout)
    private TabLayout tabLayout;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    @Binding(id = R.id.date_text_view)
    private TextView dateTextView;
    @Binding(id = R.id.arrived_text_view, format = "¥{{arrived | money}}")
    private TextView arrivedTextView;
    @Binding(id = R.id.not_arrived_text_view, format = "¥{{notArrived | money}}")
    private TextView notArrivedTextView;
    @Binding(id = R.id.expend_text_view, format = "-¥{{expenditure | money}}")
    private TextView expendTextView;

    private OrderAllBuyerAdapter allDataAdapter = new OrderAllBuyerAdapter();

    @OnClick(R.id.income_received_button)
    private View.OnClickListener arrivedOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, IncomeViewController.class);
            intent.putExtra(IncomeViewController.KEY_IS_ARRIVED, true);
            intent.putExtra(IncomeViewController.KEY_YEAR, selectDateViewController.getSelectedYear());
            intent.putExtra(IncomeViewController.KEY_MONTH, selectDateViewController.getSelectedMonth());
            getActivity().startActivity(intent);

        }
    };

    @OnClick(R.id.income_not_received_button)
    private View.OnClickListener notArrivedOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, IncomeViewController.class);
            intent.putExtra(IncomeViewController.KEY_IS_ARRIVED, false);
            intent.putExtra(IncomeViewController.KEY_YEAR, selectDateViewController.getSelectedYear());
            intent.putExtra(IncomeViewController.KEY_MONTH, selectDateViewController.getSelectedMonth());
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.expense_button)
    private View.OnClickListener expendOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, ExpenseViewController.class);
            intent.putExtra(IncomeViewController.KEY_YEAR, selectDateViewController.getSelectedYear());
            intent.putExtra(IncomeViewController.KEY_MONTH, selectDateViewController.getSelectedMonth());
            getActivity().startActivity(intent);
        }
    };

    private OnSelectListener onSelectListener = new OnSelectListener() {
        @Override
        public void onSelect() {
            dateTextView.setText(String.format("%d-%d",selectDateViewController.getSelectedYear(),selectDateViewController.getSelectedMonth()));
            loadData();
            refreshLayoutControl.triggerRefreshDelayed();
        }
    };

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


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        allDataAdapter.setPageControl(pageControl);
        dateTextView.setText(String.format("%d-%d",selectDateViewController.getSelectedYear(),selectDateViewController.getSelectedMonth()));

        ListView myJournalListView = (ListView) rootView.findViewById(R.id.my_journal_list_view);
        myJournalListView.setAdapter(allDataAdapter);
        myJournalListView.setOnItemClickListener(onItemClickListener);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        loadData();
        showContent();
    }

    private void loadData(){
        String url = ContextProvider.getBaseURL() + "/api/saler/expense";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,httpListener);
        req.setParser(new GenericModelParser());
        req.addParam("year", String.valueOf(selectDateViewController.getSelectedYear()));
        req.addParam("month", String.valueOf(selectDateViewController.getSelectedMonth()));
        req.setErrorListener(new CommonErrorHandler(MyJournalViewController.this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> httpListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                Model model = data.getModel();
                if(model != null){
                    getBinder().bindData(data.getModel());
                }
            }
//            dateTextView.setText(year+"-"+month);
        }
    };

    private void loadOrderData(int page){
        String url = ContextProvider.getBaseURL() + "/api/saler/orderedBuyer";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, orderListener);
//        req.addParam("");
        req.setAttachment(page);
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("year", String.valueOf(selectDateViewController.getSelectedYear()));
        req.addParam("month", String.valueOf(selectDateViewController.getSelectedMonth()));
//        req.setErrorListener(new CommonErrorHandler(MyJournalViewController.this));
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
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderOfBuyerViewController.class);
            intent.putExtra(OrderOfBuyerViewController.KEY_USER_ID, allDataAdapter.getItem(position).getInt("user_id"));
            intent.putExtra(OrderOfBuyerViewController.KEY_USER_NAME, allDataAdapter.getItem(position).getString("display_name"));
            intent.putExtra(ExpenseViewController.KEY_YEAR, selectDateViewController.getSelectedYear());
            intent.putExtra(ExpenseViewController.KEY_MONTH, selectDateViewController.getSelectedMonth());
            getActivity().startActivity(intent);
        }
    };
}
