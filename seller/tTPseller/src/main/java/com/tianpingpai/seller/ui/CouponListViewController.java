package com.tianpingpai.seller.ui;


import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tianpingpai.seller.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.adapter.CouponAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@ActionBar(title = "我的优惠券")
@SuppressWarnings("unused")
@Layout(id = R.layout.ui_coupon_list)
@EmptyView(imageResource = R.drawable.ic_coupon_empty_view,text = R.string.empty_coupons)
public class CouponListViewController extends BaseViewController {

    @Binding(id = R.id.empty_container)
    private ViewGroup emptyView;

    private SwipeRefreshLayout refreshLayout;
    private CouponAdapter couponAdapter = new CouponAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getCoupons(1);
        }
    };

    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            getCoupons(page);
        }
    };

    @Override
    protected ViewGroup getEmptyContainer() {
        return emptyView;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        ListView couponListView = (ListView) rootView
                .findViewById(R.id.coupon_list_view);
        couponListView.setAdapter(couponAdapter);
        couponAdapter.setPageControl(mPageControl);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("未使用"));
        tabLayout.addTab(tabLayout.newTab().setText("已使用"));
        tabLayout.addTab(tabLayout.newTab().setText("已过期"));
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        showContent();
    }

    private int couponState = 1;
    private void getCoupons(int page) {
        String url = URLApi.getBaseUrl() + "/api/coupon/list";
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            refreshLayout.setRefreshing(false);
            return;
        }

        final HttpRequest<ListResult<Model>> req = new HttpRequest<>(
                url, listener);
        JSONListParser parser = new JSONListParser();
        parser.setPaged(true);
        req.setParser(parser);

        req.addParam("accessToken", user.getAccessToken());

        req.addParam("status", String.valueOf(couponState));
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("pageSize", String.valueOf(CommonUtil.PAGESIZE));

        if (page == 1) {//1 means onReloadData
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }
        couponAdapter.setCouponStatus(couponState);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        getCoupons(1);
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request,
                             ListResult<Model> data) {

            if (request.getAttachment(Boolean.class) == Boolean.TRUE) {
                couponAdapter.clear();
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if (data.isSuccess()) {
                couponAdapter.setData(data);
                if (refreshLayout != null) {
                    if (couponAdapter.isEmpty()) {
                        showEmptyView();
                        refreshLayout.setVisibility(View.GONE);
                    } else {
                        hideEmptyView();
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                couponAdapter.setRefreshing(false);
                ResultHandler.handleError(data, CouponListViewController.this);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();
            if (pos == 0) {
                couponState = CommonUtil.Coupon.NOT_USE;
            } else if (pos == 1) {
                couponState = CommonUtil.Coupon.USED;
            } else if(pos == 2){
                couponState = CommonUtil.Coupon.OUT_DATE;//TODO
            }
            refreshLayoutControl.triggerRefresh();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    };
}
