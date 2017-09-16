package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.CommentAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetMessage;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "评论详情")
@ActionBar(title = "评论")
@Layout(id = R.layout.ui_comment_list)
public class CommentListViewController extends BaseViewController {

    public static final String KEY_ID = "key.id";
    public static final String KEY_MESSAGE = "key.message";
    public static final String KEY_IS_SHOW = "key.is.show";

    private ActionSheetMessage actionSheetMessage;

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout refreshLayout;
    private CommentAdapter commentAdapter = new CommentAdapter();

    private boolean isShow;
    private String message;
    private int sellerId;
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
        sellerId = getActivity().getIntent().getIntExtra(KEY_ID, -1);
        message = getActivity().getIntent().getStringExtra(KEY_MESSAGE);
        isShow = getActivity().getIntent().getBooleanExtra(KEY_IS_SHOW, false);
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
        ListView commentListView = (ListView) rootView.findViewById(R.id.comment_list_view);
        commentListView.setAdapter(commentAdapter);
        commentAdapter.setPageControl(pageControl);

        if(!isShow){
            refreshLayoutControl.triggerRefreshDelayed();
        }
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        if(isShow){
            actionSheetMessage = new ActionSheetMessage();
            ActionSheet actionSheet = new ActionSheet();
            actionSheet.setCancelable(false);
            actionSheetMessage.setActionSheet(actionSheet);
            actionSheetMessage.setActivity(getActivity());
            actionSheetMessage.setCancelButtonHidden(true);
            actionSheetMessage.setTitle(message);
            actionSheetMessage.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionSheetMessage.dismiss();
                    refreshLayoutControl.triggerRefreshDelayed();
                }
            });
            actionSheetMessage.show();
            isShow = false;
        }
    }

    private void loadPage(int page) {
        String url = URLUtil.COMMENT_LIST_URL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, commentsListener);
        req.addParam("saler_id", String.valueOf(sellerId));
        req.addParam("grade", String.valueOf(grade));
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("page_size", "10");
        req.setAttachment(page);
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
        req.setErrorListener(new CommonErrorHandler(this));
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        refreshLayoutControl.triggerRefreshDelayed();
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        if(actionSheetMessage != null){
            if(actionSheetMessage.isShow()){
                return true;
            }
        }
        return super.onBackKeyDown(a);
    }
}
