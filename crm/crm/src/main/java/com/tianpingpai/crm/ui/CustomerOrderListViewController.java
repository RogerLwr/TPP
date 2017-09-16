package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.OrdersAdapter;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.CustomerModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

@EmptyView(imageResource = R.drawable.ic_1510_empty_view,text = R.string.empty_orders)
@Layout(id= R.layout.view_controll_order_list)
public class CustomerOrderListViewController extends CrmBaseViewController {

    private SwipeRefreshLayout refreshLayout;

    private ArrayList<String> myArrayList = new ArrayList<>();

    public static final String KEY_CUSTOMER = "Key.customer";
    public static final String KEY_DATE_TYPE = "Key.dateType";
    public static final String KEY_TITLE = "Key.title";
    public static final String KEY_ORDER_TYPE = "Key.orderType";// 订单类型 0 订单列表  1 新客户订单列表 2 某个客户的订单

    private CustomerModel mCustomer;
    private String dateType;
    private OrdersAdapter ordersAdapter = new OrdersAdapter();
//    private OrdersFilterViewController filterViewController1509;

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            getOrders(page);
        }
    };

    private View actionBarView;

//    @Binding(id = R.id.select_container)
//    private View selectContainer;
    @Binding(id = R.id.select_listView)
    private ListView selectListView;
    private ImageButton upOrDown;

    private ArrayAdapter<String> myArrayAdapter;

    private PopupWindow popupWindow;
    private View selectWindowView;

    {
        myArrayList.add("餐厅采购订单");
        myArrayList.add("上游采购订单");
    }

    private ListView ordersListView;

    private boolean isHaveGrade;
    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View rootView = getView();
        mCustomer = (CustomerModel)getActivity().getIntent().getSerializableExtra(KEY_CUSTOMER);
        isHaveGrade = getActivity().getIntent().getBooleanExtra(OrderDetailViewController.KEY_IS_HAVE_GRADE, false);
        dateType = getActivity().getIntent().getStringExtra(KEY_DATE_TYPE);

        actionBarView = setActionBarLayout(R.layout.ab_back_title_right);
        actionBarView.findViewById(R.id.ab_right_button).setVisibility(View.GONE);

        upOrDown = (ImageButton) actionBarView.findViewById(R.id.drop_view_button);
//        upOrDown.setOnClickListener(upOrDownOnClickListener);
        TextView titleButton = (TextView) actionBarView.findViewById(R.id.ab_title_text_view);
//        titleButton.setOnClickListener(upOrDownOnClickListener);

        myArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, myArrayList);
        selectListView.setAdapter(myArrayAdapter);
        selectListView.setOnItemClickListener(selectOnItemClickListener);

        ordersListView = (ListView) rootView
                .findViewById(R.id.orders_list_view);
        ordersAdapter.setCustomerModel(mCustomer);
        ordersListView.setAdapter(ordersAdapter);
        ordersAdapter.setPageControl(mPageControl);
        ordersAdapter.setActivity(getActivity());
        ordersListView.setAdapter(ordersAdapter);
        ordersListView.setOnItemClickListener(ordersItemClickListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);

        UserManager.getInstance().registerListener(userInfoListener);
        if(null!=mCustomer){
            if(0==mCustomer.getGrade()){
                upOrDown.setVisibility(View.VISIBLE);
                setTitle("餐厅采购订单");
                titleButton.setOnClickListener(upOrDownOnClickListener);
                type = 0;
            }else if(1==mCustomer.getGrade()){
//                upOrDown.setVisibility(View.VISIBLE);
                setTitle("上游采购订单");
                type = 1;
            }
            grade = mCustomer.getGrade();

            if(mCustomer.getUserType()==1){
                upOrDown.setVisibility(View.GONE);
                titleButton.setClickable(false);
                setTitle("买家采购订单");
            }
        }
        refreshLayoutControl.triggerRefresh();

    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(userInfoListener);
    }

    private void getOrders(int page) {
        if (UserManager.getInstance().getCurrentUser() == null) {
            ordersAdapter.setData(null);
            refreshLayout.setRefreshing(false);
            return;
        }
        if(grade==1){
            getUpStreamOrder(page);
            return;
        }
        String url = URLApi.getBaseUrl() + "/crm/customer/order";

        final HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
//        ListParser<Model> parser = new ListParser<>(
//                OrderModel.class);
        req.setParser(new JSONListParser());

        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });

//        req.addParam("status", orderStatus);
        req.addParam("date_type", dateType);
        req.addParam("user_type", mCustomer.getUserType()+"");//默认是买家
        req.addParam("customer_id", mCustomer.getId()+"");
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());

        if (page == 1) {//1 means reload
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
            showLoading();
        }
        Log.e("xx", "url:" + req.getUrl());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void getUpStreamOrder(int page){
        if (UserManager.getInstance().getCurrentUser() == null) {
            ordersAdapter.setData(null);
            refreshLayout.setRefreshing(false);
            return;
        }
        String url = URLApi.getBaseUrl() + "/crm/order/purchase_list";
        final HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.setParser(new JSONListParser());

        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                refreshLayout.setRefreshing(false);
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), eror.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        req.addParam("user_id",mCustomer.getUserId()+"");
        req.addParam("grade",mCustomer.getGrade()+"");
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("accessToken", UserManager.getInstance().getCurrentUser()
                .getAccessToken());

        if (page == 1) {//1 means reload
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
            showLoading();
        }
        Log.e("xx", "url:" + req.getUrl());
        VolleyDispatcher.getInstance().dispatch(req);

    }

    private AdapterView.OnItemClickListener ordersItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            Intent intent = new Intent(getActivity(),
                    ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,
                    OrderDetailViewController.class);
            intent.putExtra(OrderDetailViewController.KEY_ORDER,
                    ordersAdapter.getItem(position).toJsonString());
//            intent.putExtra(OrderDetailViewController.KEY_ORDER_ID,ordersAdapter.getItem(position).getString(""));
            intent.putExtra(OrderDetailViewController.KEY_GRADE,grade);
            intent.putExtra(OrderDetailViewController.KEY_IS_HAVE_GRADE,isHaveGrade);
            intent.putExtra(OrderDetailViewController.KEY_TYPE,type);
            getActivity().startActivity(intent);
        }
    };

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if(data.getCode()==1){
                UserManager.getInstance().loginExpired(getActivity());
                return;
            }
            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                ordersAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if (data.isSuccess()) {
                ordersAdapter.setData(data);
//                if (filterViewController1509 != null) {
//                    titleTV.setText(filterPopup.getOrderStatus() + "订单");
//                    setTitle(filterViewController1509.getOrderStatus() + "订单");
//                }
                if (ordersAdapter.isEmpty()) {
                    showEmptyView();
                } else {
                    hideEmptyView();
                }
            } else {
                ordersAdapter.setData(data);
                Toast.makeText(ContextProvider.getContext(), "" + data.getDesc(), Toast.LENGTH_SHORT).show();
            }
            hideLoading();
        }
    };

    private ModelStatusListener<UserEvent, UserModel> userInfoListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            refreshLayoutControl.triggerRefresh();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getOrders(1);
        }
    };


    boolean isOpen;
    private View.OnClickListener upOrDownOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(!isOpen){
                showSelectWindow();
                showTopButton();
                isOpen = true;
            }else{
                isOpen = false;
            }
        }
    };

    int grade = -1;
    int type = -1;
    AdapterView.OnItemClickListener selectOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            showTopButton();
            isOpen =false;
            if(i==0){
                grade = 0;
                setTitle("餐厅采购订单");
                type = 0;
            }else if(i==1){
                grade = 1;
                type = 1;
                setTitle("上游采购订单");
            }
            ordersAdapter.setType(type);
            refreshLayoutControl.triggerRefresh();
            ordersListView.smoothScrollToPosition(0);
            popupWindow.dismiss();
        }
    };
    boolean isDown = true;
    private void showTopButton(){
        Log.e("isdown-----",""+isDown);
        if(isDown){
            isDown = false;
            upOrDown.setImageResource(R.drawable.ar);
        }else{
            isDown = true;
            upOrDown.setImageResource(R.drawable.ad);
        }
    }

    private void showSelectWindow(){
        if(selectWindowView==null){
            selectWindowView = getActivity().getLayoutInflater().inflate(R.layout.time_view_sort, null);
            selectWindowView.setOnClickListener(dismissListener);
            ListView listView = (ListView) selectWindowView.findViewById(R.id.sort_condition_list_view);
            listView.setAdapter(myArrayAdapter);
            listView.setOnItemClickListener(selectOnItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(selectWindowView);
        popupWindow.showAsDropDown(actionBarView, 0, 5);
    }


    private PopupWindow getPopupWindow(){
        if(popupWindow==null){
            popupWindow = new PopupWindow();
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(onPopupWindowDismissListener);
            popupWindow.setWidth(getView().getWidth());//TODO
            popupWindow.setHeight(getView().getHeight());
            return popupWindow;
        }
        return popupWindow;
    }

    private PopupWindow.OnDismissListener onPopupWindowDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            showTopButton();
//            isOpen = false;
        }
    };

    private View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                isOpen=false;
            }
        }
    };
}
