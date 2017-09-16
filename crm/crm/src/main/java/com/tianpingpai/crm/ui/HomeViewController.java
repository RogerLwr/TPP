package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;
@SuppressWarnings("unused")
@Layout(id = R.layout.fragment_main_home)
public class HomeViewController extends BaseViewController {

    public void setMainFragment(MainViewController mainFragment) {
        this.mainFragment = mainFragment;
    }

    private MainViewController mainFragment;

    @Binding(id=R.id.add_new_customer_tv, format = "{{customerNew}}户")
    private TextView addNewCustomerTv;
    @Binding(id=R.id.place_order_customer_tv , format = "{{customerOrder}}户")
    private TextView placeOrderCustomerTv;
    @Binding(id=R.id.place_order_today_tv, format = "{{order}}单")
    private TextView placeOrderTodayTv;
    @Binding(id=R.id.visit_num_today_tv, format = "{{customerVisit}}家")
    private TextView visitNumTodayTv;
    @Binding(id=R.id.visit_time_today_tv, format = "{{visitsOfCustomer}}次")
    private TextView visitTimeTodayTv;
    @Binding(id=R.id.visit_seller_time_today_tv,format = "{{salerVisitNum}}次")//数据不正确,需修改
    private TextView visitSellerTimeTodayTv;
    @Binding(id=R.id.visit_seller_num_today_tv,format = "{{salerVisit}}家")//数据不正确,需修改
    private TextView visitSellerNumTodayTv;
    @Binding(id=R.id.my_integral_tv , format = "{{total_point}}分")
    private TextView myIntegralTv;


    @Binding(id=R.id.home_refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(dateType);
        }
    };

    @Binding(id = R.id.date_visit_text_view)
    private TextView dateVisitTV;
    @Binding(id = R.id.date_order_text_view)
    private TextView dateOrderTV;
    @Binding(id=R.id.date_visit_seller_text_view)
    private TextView dateVisitSellerTextView;


    private TextView filterButton;

    public ActionSheet actionSheet;

    int dateType=0;

    public void setData(Model model, int dateType){
        this.dateType = dateType;
        getBinder().bindData(model);
        switch (dateType){
            case -1:
                dateVisitTV.setText("全部买家拜访");
                dateVisitSellerTextView.setText("全部卖家拜访");
                dateOrderTV.setText("全部下单");
                break;
            case 0:
                dateVisitTV.setText("今日买家拜访");
                dateVisitSellerTextView.setText("今日卖家拜访");
                dateOrderTV.setText("今日下单");
                break;
            case 1:
                dateVisitTV.setText("本周买家拜访");
                dateVisitSellerTextView.setText("本周卖家拜访");
                dateOrderTV.setText("本周下单");
                break;
            case 2:
                dateVisitTV.setText("本月买家拜访");
                dateVisitSellerTextView.setText("本月卖家拜访");
                dateOrderTV.setText("本月下单");
                break;
        }
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();

//        hideActionBar();
        View actionBar = setActionBarLayout(R.layout.ab_title_green);
        filterButton = (TextView)actionBar.findViewById(R.id.ab_right_button);
        filterButton.setText("本日");
        Toolbar toolbar = (Toolbar)actionBar.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        filterButton.setOnClickListener(onScreenClickListener);
        setTitle("CRM");

        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(model == null)
            loadData(dateType);
    }

    View.OnClickListener onScreenClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet = getActionSheet(true);
            actionSheet.setHeight(mainFragment.getContentHeight());
            actionSheet.setWidth(getView().getWidth() * 3 / 4);
            SelectDateViewController selectDateViewController = new SelectDateViewController();
            selectDateViewController.setActivity(getActivity());
            selectDateViewController.setActionSheet(actionSheet);
            selectDateViewController.setOnSelectDateListener(new SelectDateViewController.OnSelectDateListener() {
                @Override
                public void onSelectDate(String s, int id) {
                    filterButton.setText(s);
                    loadData(id - 1);
                    dateType = id-1;
                }
            });

            actionSheet.setViewController(selectDateViewController);
            actionSheet.setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
//            actionSheet.show(getActivity().getSupportFragmentManager(), "");
            actionSheet.show();
        }

    };

    //新增客户
    @OnClick(R.id.add_new_customers_container)
    View.OnClickListener addNewCustomerButtonLister = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SellerCustomersViewController.class);
            intent.putExtra(SellerCustomersViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(SellerCustomersViewController.KEY_CUSTOMER_TYPE, 1);
            intent.putExtra(SellerCustomersViewController.KEY_TITLE, "新增客户");
            getActivity().startActivity(intent);
        }
    };
    //下单客户
    @OnClick(R.id.place_order_customer_container)
    View.OnClickListener orderCustomerOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SellerCustomersViewController.class);
            intent.putExtra(SellerCustomersViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(SellerCustomersViewController.KEY_CUSTOMER_TYPE, 2);
            intent.putExtra(SellerCustomersViewController.KEY_USER_TYPE,1);
            intent.putExtra(SellerCustomersViewController.KEY_TITLE, "下单客户");
            intent.putExtra(SellerCustomersViewController.KEY_APPROVAL_GONE,true);
            getActivity().startActivity(intent);
        }
    };
    //今日下单
    @OnClick(R.id.place_order_today_container)
    View.OnClickListener orderTodayOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            String title = dateOrderTV.getText().toString();
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, title);
            getActivity().startActivity(intent);
        }
    };
    //今日拜访
    @OnClick(R.id.visit_today_container)
    View.OnClickListener todayVisitOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, CustomersVisitedViewController.class);
            intent.putExtra(CustomersVisitedViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(CustomersVisitedViewController.KEY_CUSTOMER_TYPE,"1");//买家
            getActivity().startActivity(intent);
        }
    };

    //今日卖家拜访
    @OnClick(R.id.visit_seller_today_container)
    View.OnClickListener todaySellerVisitSellerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, CustomersVisitedViewController.class);
            intent.putExtra(CustomersVisitedViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(CustomersVisitedViewController.KEY_CUSTOMER_TYPE,"0");//卖家
            getActivity().startActivity(intent);
        }
    };
    //我的足迹
    @OnClick(R.id.my_foot_container)
    View.OnClickListener myFootOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, MyTracksViewController.class);
            intent.putExtra(MyTracksViewController.KEY_DATE_TYPE, dateType + "");
            getActivity().startActivity(intent);
        }
    };

    private void loadData(int dateType){
        String url = URLApi.getBaseUrl() + "/crm/marketer/index";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new GenericModelParser());

        req.addParam("date_type", dateType + "");
        req.setAttachment(dateType);
        if (refreshLayout != null && !refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    Model model;
    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                model = data.getModel();
                Log.e("xx", "225------model=" + model.toString());
                HomeViewController.this.setData(data.getModel(), request.getAttachment(Integer.class));
                Log.e("xx", "44----------model=" + data.getModel());
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
        }
    };

}
