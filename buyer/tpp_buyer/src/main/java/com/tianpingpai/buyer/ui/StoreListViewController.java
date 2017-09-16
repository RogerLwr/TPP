package com.tianpingpai.buyer.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.SortStoreAdapter;
import com.tianpingpai.buyer.adapter.StoreAdapter;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.model.MarketModel;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.SortInfo;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@SuppressWarnings("unused")
@Statistics(page = "店铺列表")
@Layout(id = R.layout.ui_store_list)
public class StoreListViewController extends BaseViewController {

    public static final String KEY_SHOW_BACK_BUTTON = "showBackButton";

    @Binding(id = R.id.sort_container)
    private View sortContainer;
    @Binding(id = R.id.category_button)
    private TextView categoryButton;
    @Binding(id = R.id.sort_button)
    private TextView sortButton;
    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.not_validated_container)
    private View notValidatedContainer;

    public int categoryId = 0;
    private PopupWindow popupWindow;
    private View windowView;

    private StoreAdapter storeAdapter = new StoreAdapter();
    private SortStoreAdapter sortAdapter = new SortStoreAdapter();

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    private ModelStatusListener<UserEvent, UserModel> loginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event){
                case Login:
                case Logout:
                    refreshLayoutControl.triggerRefresh();
                    break;
            }
        }
    };
    private ModelStatusListener<ModelEvent, MarketModel> marketListener = new ModelStatusListener<ModelEvent, MarketModel>() {
        @Override
        public void onModelEvent(ModelEvent event, MarketModel model) {
            setTitle(MarketManager.getInstance().getCurrentMarket().getName());
            refreshLayoutControl.triggerRefresh();
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        Intent i = getActivity().getIntent();


        categoryId = i.getIntExtra("category_id",categoryId);
        boolean showBackButton = i.getBooleanExtra(KEY_SHOW_BACK_BUTTON,false);

        setupViews(rootView);

        Toolbar toolbar = (Toolbar) setActionBarLayout(R.layout.ab_title_white).findViewById(R.id.toolbar);
        if(!showBackButton) {
            toolbar.setNavigationIcon(null);
        }
        setTitle(MarketManager.getInstance().getCurrentMarket().getName());
        if(sortAdapter.shouldLoadCategories()) {
            loadCategories();
        }
        UserManager.getInstance().registerListener(loginListener);
        MarketManager.getInstance().registerListener(marketListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UserManager.getInstance().unregisterListener(loginListener);
        MarketManager.getInstance().unregisterListener(marketListener);
    }

    private void setupViews(View rootView) {
        notValidatedContainer.setVisibility(View.INVISIBLE);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        ListView storeListView = (ListView) rootView.findViewById(R.id.store_list_view);
        storeListView.setAdapter(storeAdapter);
        storeListView.setOnItemClickListener(storeItemOnClickListener);
        storeAdapter.setPageControl(pageControl);
        categoryButton.setCompoundDrawables(null, null, downArrow, null);
        sortButton.setCompoundDrawables(null, null, downArrow, null);
        storeAdapter.setActivity(getActivity());
        TextView serviceLineTextView = (TextView) rootView.findViewById(R.id.service_line_text_view);
        serviceLineTextView.setText(Html.fromHtml("<u>" + ContextProvider.getContext().getString(R.string.service_line) + "<u>"));
        serviceLineTextView.setOnClickListener(contactButtonListener);
        loadData(1);
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
    }

    private View.OnClickListener contactButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + ContextProvider.getContext().getString(R.string.service_line)));
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.category_button)
    private View.OnClickListener categoryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPopupWindow(categoryButton,true);
        }
    };

    @OnClick(R.id.sort_button)
    private View.OnClickListener sortButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPopupWindow(sortButton,false);
        }
    };

    Drawable upArrow = ContextProvider.getContext().getResources().getDrawable(R.drawable.arrow_up);
    Drawable downArrow = ContextProvider.getContext().getResources().getDrawable(R.drawable.arrow_down);

    {
        float ratio = 0.32f;
        int w = DimensionUtil.dip2px(34 * ratio);
        int h = DimensionUtil.dip2px(34 * ratio);
        upArrow.setBounds(0, 0, w, h);
        downArrow.setBounds(0, 0, h, w);
    }

    private void showPopupWindow(TextView button,boolean showCategory) {
        if (windowView == null) {
            windowView = getActivity().getLayoutInflater().inflate(R.layout.view_sort, null);
            windowView.setOnClickListener(dismissListener);
            popupWindow = new PopupWindow(windowView);
            ListView listView = (ListView) windowView.findViewById(R.id.sort_condition_list_view);
            listView.setAdapter(sortAdapter);
            listView.setOnItemClickListener(sortItemClickListener);

            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(onPopupWindowDismissListener);
        }

        if (showCategory) {
            sortAdapter.showCategoryList();
            categoryButton.setCompoundDrawables(null, null, upArrow, null);
        } else {
            sortAdapter.showSortList();
            sortButton.setCompoundDrawables(null, null, upArrow, null);
        }
        popupWindow.setWidth(getView().getWidth());//TODO
        popupWindow.setHeight(getView().getHeight());
        popupWindow.showAsDropDown(sortContainer, 0, 2);
        button.setTextColor(getActivity().getResources().getColor(R.color.green));
    }

    public void loadData(int page) {
        String url = URLUtil.GETSHOPS_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        MarketModel market = MarketManager.getInstance().getCurrentMarket();
        req.addParam("market_id", market.getId() + "");
        req.addParam("pageNo", page + "");
        req.addParam("pageSize", 10 + "");
        req.addParam("sort", sortAdapter.getSelectedSort().getSort() + "");
        SortInfo sort = sortAdapter.getSelectedCategory();
        if (sort != null) {
            req.addParam("category_id", sort.getCategory_id() + "");
        }else {
            req.addParam("category_id", categoryId + "");
        }

        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.setAttachment(page);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        refreshLayout.setRefreshing(true);
//        showLoading();
    }


    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            showContent();
//            hideLoading();
            refreshLayout.setRefreshing(false);
            if(data.isSuccess()){
                if (request.getAttachment(Integer.class) == 1) {
                    storeAdapter.clear();
                }
                storeAdapter.setData(data);
                loadCategories();
            }else if(data.getCode() == 5){//TODO
                notValidatedContainer.setVisibility(View.VISIBLE);
            }
        }
    };

    private void loadCategories() {
        String url = URLUtil.PROD_FIRST_DATA_LIST;
        HttpRequest<ListResult<SortInfo>> req = new HttpRequest<>(url, categoryListener);
        if(MarketManager.getInstance().getCurrentMarket() != null) {
            req.addParam("market_id", MarketManager.getInstance().getCurrentMarket().getId() + "");
        }
        req.setParser(new ListParser<>(SortInfo.class));
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                hideLoading();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<SortInfo>> categoryListener = new HttpRequest.ResultListener<ListResult<SortInfo>>() {
        @Override
        public void onResult(HttpRequest<ListResult<SortInfo>> request, ListResult<SortInfo> data) {
            if (data.isSuccess()) {
//                refreshLayoutControl.triggerRefresh();
                sortAdapter.setCategoryList(data.getModels());
                categoryButton.setText(sortAdapter.getCategoryName(""+categoryId));
            }
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

    private AdapterView.OnItemClickListener sortItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            sortAdapter.setSelection(position);
            popupWindow.dismiss();
            if(sortAdapter.isShowingCategory()){
                categoryButton.setText(sortAdapter.getSelectedCategory().getName());
            }else{
                sortButton.setText(sortAdapter.getSelectedSort().getName());
            }
            categoryId = Integer.parseInt(sortAdapter.getSelectedCategory().getCategory_id());
            refreshLayoutControl.triggerRefresh();
        }
    };

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            if (sortAdapter.shouldLoadCategories()) {
                loadCategories();
            } else {
                loadData(page);
            }
        }
    };

    private PopupWindow.OnDismissListener onPopupWindowDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            categoryButton.setCompoundDrawables(null, null, downArrow, null);
            sortButton.setCompoundDrawables(null, null, downArrow, null);
            sortButton.setTextColor(getActivity().getResources().getColor(R.color.gray_66));
            categoryButton.setTextColor(getActivity().getResources().getColor(R.color.gray_66));
        }
    };

    private AdapterView.OnItemClickListener storeItemOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);
            Model model = storeAdapter.getItem(position);

            intent.putExtra(StoreDataContainer.KEY_STORE_TYPE,
                    StoreDataContainer.STORE_TYPE_NORMAL);
            intent.putExtra("shop_id", model.getInt("user_id"));
            intent.putExtra(StoreDataContainer.KEY_SHOP_NAME, model.getString("sale_name"));
            getActivity().startActivity(intent);
        }
    };

    @OnClick(R.id.not_validated_container)
    private View.OnClickListener reloadButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            notValidatedContainer.setVisibility(View.GONE);
            refreshLayoutControl.triggerRefresh();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData(1);
    }
}
