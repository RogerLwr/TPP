package com.tianpingpai.seller.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.BillAdapter;
import com.tianpingpai.seller.adapter.BillPinnedAdapter;
import com.tianpingpai.seller.adapter.JournalAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.TLog;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

/**
 * Created by LiuWenRong on 16/4/21.
 */
@Statistics(page = "我的明细")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_bills)
@Layout(id = R.layout.ui_bills)
public class BillsViewController extends BaseViewController {

    private TextView rightBtn;
//    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.bill_list_view)
    private PinnedHeaderListView billListView;

    @Binding(id = R.id.total_in_text_view, format = "¥{{totalIn}}")
    private TextView totalInTextView;

    @Binding(id = R.id.total_out_text_view, format = "¥{{totalOut}}")
    private TextView totalOutTextView;

    private String beginDate = null;
    private String endDate = null;
    private String categorys = "1";
    private String status = "1";
    //    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//        @Override
//        public void onRefresh() {
//            loadPage(1, beginDate, endDate, categorys, status);
//        }
//    };
    private BillAdapter billAdapter;
    private BillPinnedAdapter billPinnedAdapter = new BillPinnedAdapter();

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();

        View topView = setActionBarLayout(R.layout.ab_back_title_right);
        rightBtn = (TextView) topView.findViewById(R.id.ab_right_button);
        rightBtn.setText("筛选");
        setTitle("我的明细");
        rightBtn.setOnClickListener(filterButtonListener);

        billListView.setAdapter(billPinnedAdapter);

//        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
//        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
//        refreshLayoutControl.triggerRefreshDelayed();

        loadPage(1, beginDate, endDate, null, null);

    }
    BillsFilterViewController filterViewController;
    private View.OnClickListener filterButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (filterViewController == null) {
                filterViewController = new BillsFilterViewController();
                filterViewController.setActivity(getActivity());
                filterViewController.setActionSheet(getActionSheet(true));
                filterViewController.setOnConditionSelectedListener(new BillsFilterViewController.OnConditionSelectedListener() {
                    @Override
                    public void onConditionSelected(String beginDate, String endDate, String categorys, String status) {
                        BillsViewController.this.beginDate = beginDate;
                        BillsViewController.this.endDate = endDate;
                        BillsViewController.this.categorys = categorys;
                        BillsViewController.this.status = status;
                        loadPage(1, beginDate, endDate, categorys, status);
                    }
                });
                getActionSheet(true).setViewController(filterViewController);
                getActionSheet(true).setWidth(getView().getWidth());
//                getActionSheet(true).setWidth(DimensionUtil.dip2px(300));
                getActionSheet(true).setHeight(getView().getHeight());
                getActionSheet(true).setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            }
            getActionSheet(true).show();

        }
    };

    private void loadPage(int page, String beginDate, String endDate, String categorys, String status){

        // 接口详情  https://tower.im/projects/2c261acafa2f4fe4bc57e7a59d7d1075/docs/8014478a619e4a239f11165831450711/

        String url = ContextProvider.getBaseURL() + "/api/user/account/list";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, httpListener);
        req.setAttachment(page);
        req.setAttachment(billListView.getAdapter());
        req.setParser(new GenericModelParser());
        if( TextUtils.isEmpty(beginDate) ){
//            req.addParam("days",String.valueOf(7));
        }else {
            req.addParam("beginDate", beginDate);
            req.addParam("endDate", endDate);
        }

        req.addParam("category",String.valueOf(-1));//交易分类，默认为-1表示所有分类，1表示支付宝支付，6表示微信支付，5表示订单货款，11表示退款
        if ( !TextUtils.isEmpty(categorys) ){
            req.addParam("categorys", categorys); //可以多选
            req.addParam("status", status); //交易状态，默认为所有，0表示交易中，1表示交易完成，2表示交易失败
        }
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize",String.valueOf(5000));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();

    }

    private HttpRequest.ResultListener<ModelResult<Model>> httpListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
//            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                billPinnedAdapter = request.getAttachment(BillPinnedAdapter.class);

                if(request.getAttachment(Integer.class) == 1){
                    billPinnedAdapter.setPinnedBills(new ArrayList<List<Model>>());
                }
                Model model = data.getModel();
                if(model != null ){
                    getBinder().bindData(model);
                    Model billModel = model.getModel("data");
                    if(billModel != null ){
                        ArrayList<Model> bills = (ArrayList) billModel.getList("pageItems", Model.class);
                        List<String> yearMonthList = new ArrayList<>();
                        for (Model bill : bills){
                            Date date = new Date();
                            try {
                                date= CommonUtil.Date.sdf_yyyy_MM_dd.parse(bill.getString("time"));
                            }catch (ParseException e){

                            }
                            String yearMonth = CommonUtil.Date.sdf_yyyy_MM.format(date);
                            bill.set("yearMonth", yearMonth);
                            if ( !yearMonthList.contains(yearMonth)){
                                yearMonthList.add(yearMonth);
                            }
                        }

                        List<List<Model>> pinnedBills = new ArrayList<>();

                        for(String yearMonth: yearMonthList) {

                            List<Model> pinnedSection = new ArrayList<>();

                            for (int i=0; i<bills.size(); i++){
                                Model model1 = bills.get(i);
                                if (yearMonth.equals(model1.getString("yearMonth"))) {
                                    pinnedSection.add(model1);
                                }

                            }
                            pinnedBills.add(pinnedSection);
                        }
//                        billPinnedAdapter.setBills(bills);
                        billPinnedAdapter.setPinnedBills(pinnedBills);
                    }

                }

                if(billPinnedAdapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
            }else{
                ResultHandler.handleError(data, BillsViewController.this);
            }
            hideLoading();
        }
    };

}
