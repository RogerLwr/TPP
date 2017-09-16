package com.tianpingpai.crm.ui;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@SuppressWarnings("unused")
@ActionBar(layout = R.layout.ab_title_green,title = "订单")
@Layout(id = R.layout.fragment_main_order)
public class OrdersViewController extends BaseViewController{

    public void setMainFragment(MainViewController mainFragment) {
        this.mainFragment = mainFragment;
    }
    private MainViewController mainFragment;

    private TextView screenOrderTv;
    @Binding(id=R.id.all_orders_text_view, format ="{{order}}单" )
    private TextView allOrdersTextView;

    @Binding(id=R.id.suspending_orders_text_view, format = "{{orderSuspend}}单")
    private TextView suspendingOrdersTextView;
    @Binding(id=R.id.not_received_orders_text_view, format = "{{orderDeliver}}单")
    private TextView noHarvestOrdersTv;
    @Binding(id=R.id.complete_orders_text_view, format = "{{orderAccept}}单")
    private TextView completeOrdersTv;
    @Binding(id=R.id.new_customer_orders_text_view, format = "{{orderNewCustomer}}单")
    private TextView newCustomerOrdersTv;
    @Binding(id=R.id.order_refresh_layout)
    private SwipeRefreshLayout orderRefreshLayout;
    @Binding(id = R.id.ab_right_button)
    private TextView filterButton;
    public ActionSheet actionSheet;

    private Model model;

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(dateType);
        }
    };

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        filterButton.setText("本日");
        Toolbar toolbar = (Toolbar)getView().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        filterButton.setOnClickListener(onScreenClickListener);

        refreshLayoutControl.setSwipeRefreshLayout(orderRefreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(model == null) {
            loadData(dateType);
        }
    }

    int dateType = 0;
    public void setData(Model model, int dateType){
        this.dateType = dateType;
        getBinder().bindData(model);
    }

    private View.OnClickListener onScreenClickListener = new View.OnClickListener(){
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
                }
            });

            actionSheet.setViewController(selectDateViewController);
            actionSheet.setStyle(ActionSheet.STYLE_RIGHT_TO_LEFT);
            actionSheet.show();
        }
    };

    //全部订单
    @OnClick(R.id.all_order_container)
    private View.OnClickListener allOrdersOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
//            intent.putExtra(OrderListViewController.KEY_ORDER_STATUS, "1,2");// 未处理(即 待确认  待发货)
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, "全部订单");
            getActivity().startActivity(intent);

        }
    };
    //未处理订单
    @OnClick(R.id.suspending_orders_container)
    private View.OnClickListener suspendingOrdersOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
            intent.putExtra(OrderFilterListViewController.KEY_ORDER_STATUS, "1,2");// 未处理(即 待确认  待发货)
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, "未处理订单");
            getActivity().startActivity(intent);
        }
    };
    //未收货订单
    @OnClick(R.id.not_received_orders_container)
    private View.OnClickListener notReceivedOrdersOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
            intent.putExtra(OrderFilterListViewController.KEY_ORDER_STATUS, "3");// 未收货
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, "未收货订单");
            getActivity().startActivity(intent);
        }
    };
    //已完成订单
    @OnClick(R.id.complete_orders_container)
    private View.OnClickListener completeOrdersOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
            intent.putExtra(OrderFilterListViewController.KEY_ORDER_STATUS, "4,9");// 已完成(即 已收货 已评价)
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, "已完成订单");
            getActivity().startActivity(intent);
        }
    };
    //新客户订单
    @OnClick(R.id.new_customer_orders_container)
    private View.OnClickListener newCustomerOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, OrderFilterListViewController.class);
//            intent.putExtra(OrderListViewController.KEY_ORDER_STATUS, "1,2");// 新客户(即 不传)
            intent.putExtra(OrderFilterListViewController.KEY_DATE_TYPE, dateType + "");
            intent.putExtra(OrderFilterListViewController.KEY_TITLE, "新客户订单");
            intent.putExtra(OrderFilterListViewController.KEY_ORDER_TYPE, 1);
            getActivity().startActivity(intent);
        }
    };

    private void loadData(int dateType){
        String url = URLApi.getBaseUrl() + "/crm/marketer/index";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new GenericModelParser());
        req.addParam("date_type", dateType + "");
        req.setAttachment(dateType);
        if (orderRefreshLayout != null && !orderRefreshLayout.isRefreshing()) {
            orderRefreshLayout.setRefreshing(true);
        }
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                orderRefreshLayout.setRefreshing(false);
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HttpRequest.ResultListener<ModelResult<Model>> listener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                model = data.getModel();
                setData(data.getModel(), request.getAttachment(Integer.class));
            }else if(data.getCode() == 1){
//                UserManager.getInstance().loginExpired(getActivity());
            }
            if (orderRefreshLayout != null) {
                orderRefreshLayout.setRefreshing(false);
            }
        }
    };
}
