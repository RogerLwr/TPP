package com.tianpingpai.buyer.ui;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.JournalAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "查询明细")
@ActionBar(title = "查询明细")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_journal)
@Layout(id = R.layout.ui_journal)
public class JournalViewController extends BaseViewController {

    private ViewGroup emptyContainer;

    @Binding(id = R.id.select_container)
    private View selectContainer;
    private PopupWindow popupWindow;
    private View typeWindowView;
    private View payTypeWindowView;
    private View timeWindowView;
    private View orderIdWindowView;

    @Binding(id = R.id.type_text_view)
    private TextView typeTextView;
    @Binding(id = R.id.pay_type_text_view)
    private TextView payTypeTextView;
    @Binding(id = R.id.time_text_view)
    private TextView timeTextView;
    @Binding(id = R.id.orderId_text_view)
    private TextView orderIdTextView;

    @Binding(id = R.id.type_image)
    private ImageView typeImage;
    @Binding(id = R.id.pay_type_image)
    private ImageView payTypeImage;
    @Binding(id = R.id.time_image)
    private ImageView timeImage;
    @Binding(id = R.id.orderId_image)
    private ImageView orderIdImage;

    private int greenColor = ContextProvider.getContext().getResources().getColor(R.color.green);
    private int blackColor = ContextProvider.getContext().getResources().getColor(R.color.gray_33);

    private String [] typeArray = {"全部","支付宝支付","微信支付","退款"};
    private String [] timeArray = {"全部","近一天","近一周","近一月"};

    private String [] payTypeArray = {"全部","微信支付","支付宝支付"};

    private int [] arrayType = {-1,1,2};
    private int [] arrayCategory = {-1,1,6,11};
    private int [] arrayDays = {-1,1,7,30};


    private ArrayAdapter typeAdapter = new ArrayAdapter(ContextProvider.getContext(),R.layout.item_text_center,typeArray);
    private ArrayAdapter payTypeAdapter = new ArrayAdapter(ContextProvider.getContext(),R.layout.item_text_center,payTypeArray);
    private ArrayAdapter timeAdapter = new ArrayAdapter(ContextProvider.getContext(),R.layout.item_text_center,timeArray);


    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    private int category = -1;//交易分类，默认为-1表示所有分类，1表示支付宝支付，6表示微信支付，5表示订单货款，11表示退款
    private int days = -1;//时间/天，默认为所有，例如days=30表示一个月内，days=7表示一周内
    private int type = 1;//交易类型，默认为空，表示所有交易类型，1表示收入，2表示支出

    private JournalAdapter allDataAdapter = new JournalAdapter();

    private ListView journalListView;

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadPage(page);
        }
    };

    {
        allDataAdapter.setPageControl(pageControl);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        journalListView = (ListView) rootView.findViewById(R.id.journal_list_view);
        journalListView.setAdapter(allDataAdapter);
        journalListView.setOnItemClickListener(onItemClickListener);

        emptyContainer = (ViewGroup) rootView.findViewById(R.id.empty_container);

        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        showContent();
    }

    @Override
    public ViewGroup getEmptyContainer() {
        return emptyContainer;
    }

    private void loadPage(int page){

        String url = ContextProvider.getBaseURL() + "/api/user/account/list";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url,httpListener);
        req.setAttachment(page);
        req.setAttachment(journalListView.getAdapter());
        req.setParser(new JSONListParser());
        req.addParam("days",String.valueOf(days));
        req.addParam("category",String.valueOf(category));
        req.addParam("type", String.valueOf(type));
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
                JournalAdapter adapter = request.getAttachment(JournalAdapter.class);
                if(request.getAttachment(Integer.class) == 1){
                    adapter.clear();
                }
                adapter.setData(data);
                if(adapter.isEmpty()){
                    showEmptyView();

                }else{
                    hideEmptyView();
                }
            }else{
                ResultHandler.handleError(data, JournalViewController.this);
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPage(1);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent = new Intent(getActivity(), FragmentContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT,OrderStatusFragment.class);
//            startActivity(intent);
        }
    };

    @OnClick(R.id.type_container)
    private View.OnClickListener typeOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switchButton(R.id.type_container);
        }
    };

    @OnClick(R.id.pay_type_container)
    private View.OnClickListener payTypeOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switchButton(R.id.pay_type_container);
        }
    };

    @OnClick(R.id.time_container)
    private View.OnClickListener timeOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switchButton(R.id.time_container);
        }
    };

    @OnClick(R.id.orderId_container)
    private View.OnClickListener orderIdOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switchButton(R.id.orderId_container);
        }
    };

    private PopupWindow getPopupWindow(){
        if(popupWindow==null){
            popupWindow = new PopupWindow();
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x33000000));
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
            int drawableDown = R.drawable.arrow_down;
            typeImage.setBackgroundResource(drawableDown);
            payTypeImage.setBackgroundResource(drawableDown);
            timeImage.setBackgroundResource(drawableDown);
            orderIdImage.setBackgroundResource(drawableDown);

            typeTextView.setTextColor(blackColor);
            payTypeTextView.setTextColor(blackColor);
            timeTextView.setTextColor(blackColor);
            orderIdTextView.setTextColor(blackColor);

        }
    };
    private View.OnClickListener dismissListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();

            }
        }
    };

    private void showTypeView(){
        if(typeWindowView == null){
            typeWindowView = getActivity().getLayoutInflater().inflate(R.layout.type_view_sort, null);
            typeWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(typeWindowView);
            ListView listView = (ListView) typeWindowView.findViewById(R.id.type_list_view);
            listView.setAdapter(typeAdapter);
            listView.setOnItemClickListener(typeSortItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(typeWindowView);
        popupWindow.showAsDropDown(selectContainer, 0, 2);
    }

    private AdapterView.OnItemClickListener typeSortItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            typeTextView.setText(typeArray[i]);
            typeTextView.setTextColor(greenColor);
            category = arrayCategory[i];
            popupWindow.dismiss();
            refreshLayoutControl.triggerRefreshDelayed();
        }
    };
    private void showPayTypeView(){
        if(payTypeWindowView == null){
            payTypeWindowView = getActivity().getLayoutInflater().inflate(R.layout.type_view_sort, null);
            payTypeWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(typeWindowView);
            ListView listView = (ListView) payTypeWindowView.findViewById(R.id.type_list_view);
            listView.setAdapter(payTypeAdapter);
            listView.setOnItemClickListener(payTypeSortItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(payTypeWindowView);
        popupWindow.showAsDropDown(selectContainer, 0, 2);
    }

    private AdapterView.OnItemClickListener payTypeSortItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            payTypeTextView.setText(payTypeArray[i]);
            payTypeTextView.setTextColor(greenColor);
            category = arrayCategory[i];
            popupWindow.dismiss();
            refreshLayoutControl.triggerRefreshDelayed();
        }
    };
    private void showTimeView(){
        if(timeWindowView == null){
            timeWindowView = getActivity().getLayoutInflater().inflate(R.layout.type_view_sort, null);
            timeWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(timeWindowView);
            ListView listView = (ListView) timeWindowView.findViewById(R.id.type_list_view);
            listView.setAdapter(timeAdapter);
            listView.setOnItemClickListener(timeSortItemClickListener);
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(timeWindowView);
        popupWindow.showAsDropDown(selectContainer, 0, 2);
    }

    private AdapterView.OnItemClickListener timeSortItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            timeTextView.setText(timeArray[i]);
            timeTextView.setTextColor(greenColor);
            days = arrayDays[i];
            popupWindow.dismiss();
            refreshLayoutControl.triggerRefreshDelayed();
        }
    };
    private void showOrderIdView(){
        if(orderIdWindowView == null){
            orderIdWindowView = getActivity().getLayoutInflater().inflate(R.layout.order_view_sort, null);
            orderIdWindowView.setOnClickListener(dismissListener);
            popupWindow = getPopupWindow();
            popupWindow.setContentView(orderIdWindowView);
            final EditText orderIdEdt = (EditText)orderIdWindowView.findViewById(R.id.edt_container);
            orderIdWindowView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderIdEdt.setText(null);
                    popupWindow.dismiss();
                }
            });
            orderIdWindowView.findViewById(R.id.affirm_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(orderIdEdt.getText().toString())) {
                        searchOrderId(orderIdEdt.getText().toString());
                        popupWindow.dismiss();
                    }
                }
            });
        }
        popupWindow = getPopupWindow();
        popupWindow.setContentView(orderIdWindowView);
        popupWindow.showAsDropDown(selectContainer, 0, 2);
    }

    private void switchButton(int id){
        int drawableUp = R.drawable.arrow_up;

        switch (id){
            case R.id.type_container:
                typeTextView.setTextColor(greenColor);
                payTypeTextView.setTextColor(blackColor);
                timeTextView.setTextColor(blackColor);
                orderIdTextView.setTextColor(blackColor);
                typeImage.setBackgroundResource(drawableUp);
                showTypeView();
                break;
            case R.id.pay_type_container:
                typeTextView.setTextColor(blackColor);
                orderIdTextView.setTextColor(blackColor);
                payTypeTextView.setTextColor(greenColor);
                timeTextView.setTextColor(blackColor);
                orderIdTextView.setTextColor(blackColor);
                payTypeImage.setBackgroundResource(drawableUp);
                showPayTypeView();
                break;
            case R.id.time_container:
                typeTextView.setTextColor(blackColor);
                payTypeTextView.setTextColor(blackColor);
                timeTextView.setTextColor(greenColor);
                orderIdTextView.setTextColor(blackColor);
                timeImage.setBackgroundResource(drawableUp);
                showTimeView();
                break;
            case R.id.orderId_container:
                typeTextView.setTextColor(blackColor);
                payTypeTextView.setTextColor(blackColor);
                timeTextView.setTextColor(blackColor);
                orderIdTextView.setTextColor(greenColor);
                orderIdImage.setBackgroundResource(drawableUp);
                showOrderIdView();
                break;
        }
    }

    private void searchOrderId(String key){

    }
}
