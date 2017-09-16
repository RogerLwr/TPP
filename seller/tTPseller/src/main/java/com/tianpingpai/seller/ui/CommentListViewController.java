package com.tianpingpai.seller.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.seller.adapter.CommentAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@ActionBar(title = "评论详情")
@Statistics(page = "评论详情")
@Layout(id = R.layout.ui_comment_list)
public class CommentListViewController extends BaseViewController {

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout refreshLayout;
    private CommentAdapter commentAdapter = new CommentAdapter();

    private int grade = -1;

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
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

    private HttpRequest.ResultListener<ListResult<Model>> commentsListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if(data.isSuccess()){
                if(request.getAttachment(Integer.class) == 1){
                    commentAdapter.clear();
                }
                commentAdapter.setData(data);
            }else{
                commentAdapter.setRefreshing(false);
            }
        }
    };
    private TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if(tab.getPosition() == 0){
                grade = -1;
            }else{
                grade = 4 - tab.getPosition();
            }
            loadPage(1);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("全部评价"));
        tabLayout.addTab(tabLayout.newTab().setText("好评"));
        tabLayout.addTab(tabLayout.newTab().setText("中评"));
        tabLayout.addTab(tabLayout.newTab().setText("差评"));
        tabLayout.setOnTabSelectedListener(tabSelectedListener);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        ListView commentListView = (ListView) rootView.findViewById(R.id.notice_list_view);
        commentListView.setAdapter(commentAdapter);
        commentAdapter.setPageControl(pageControl);
        refreshLayoutControl.triggerRefreshDelayed();
    }

    private void loadPage(int page) {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(ContextProvider.getContext(),"请登录后重试!",Toast.LENGTH_SHORT).show();
            return;
        }
        String url = URLApi.COMMENT_LIST_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, commentsListener);
        req.addParam("saler_id", user.getUserID());
        req.addParam("grade", String.valueOf(grade));
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("page_size", "10");
        req.setAttachment(page);
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }
}
