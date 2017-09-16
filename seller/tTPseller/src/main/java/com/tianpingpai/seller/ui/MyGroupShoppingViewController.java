package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.adapter.GroupShoppingAdapter;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

/**
 * 我的明细
 */
@SuppressWarnings("unused")
@Statistics(page = "我的团购")
@ActionBar(title = "团购")
@Layout(id = R.layout.ui_my_group_shopping)
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_group)
public class MyGroupShoppingViewController extends BaseViewController {

    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.group_shopping_list_view)
    private ListView groupShoppingListView;
    @Binding(id = R.id.tab_layout)
    private TabLayout tabLayout;
    @Binding(id = R.id.empty_container)
    private ViewGroup emptyView;
    private GroupShoppingAdapter groupShoppingAdapter = new GroupShoppingAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };

    @Override
    protected ViewGroup getEmptyContainer() {
        return emptyView;
    }

    private int mType = 0; //0团购中 1往前团购

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        showContent();
        tabLayout.addTab(tabLayout.newTab().setText("团购中"));
        tabLayout.addTab(tabLayout.newTab().setText("往期团购"));
        groupShoppingAdapter.setPageControl(mPageControl);
        refreshLayout.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        groupShoppingListView.setAdapter(groupShoppingAdapter);
        groupShoppingListView.setOnItemClickListener(onItemClickListener);
//        loadData(1);

    }

    private void loadData(int pageNo){

        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLApi.getBaseUrl() + "/api/groupbuy/list", resultListener);
//        req.setMethod(HttpRequest.POST);
        req.setParser(new JSONListParser());
        req.addParam("type", mType + "");
        req.addParam("pageNo", pageNo + "");
        req.setAttachment(pageNo);
        req.addParam("pageSize", String.valueOf(CommonUtil.PAGESIZE));
        VolleyDispatcher.getInstance().dispatch(req);
//        showLoading();

    }

    private HttpRequest.ResultListener<ListResult<Model>> resultListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            if(data.isSuccess()){
                if(request.getAttachment(Integer.class) == 1){
                    groupShoppingAdapter.clear();
                }
                groupShoppingAdapter.setData(data);
                if (refreshLayout != null) {
                    if (groupShoppingAdapter.isEmpty()) {
                        showEmptyView();
                        refreshLayout.setVisibility(View.GONE);
                    } else {
                        hideEmptyView();
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }

            }
//            hideLoading();
            refreshLayout.setRefreshing(false);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, GroupDetailViewController.class);
            intent.putExtra(GroupDetailViewController.KEY_GROUP_ID, groupShoppingAdapter.getItem(i).getInt("group_id"));
            getActivity().startActivity(intent);
        }
    };

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int pos = tab.getPosition();
            if (pos == 0) {
                mType = 0;
            } else if (pos == 1) {
                mType = 1;
            }
            refreshLayoutControl.triggerRefresh();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

}
