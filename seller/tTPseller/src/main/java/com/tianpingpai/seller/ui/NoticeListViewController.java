package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.NoticeManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.NoticeAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.CommonUtil;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "我的公告")
@Layout(id = R.layout.ui_notice_list)
public class NoticeListViewController extends BaseViewController{

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout refreshLayout;
    private NoticeAdapter noticeAdapter = new NoticeAdapter();

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

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if(data.isSuccess()){
                if(request.getAttachment(Integer.class) == 1){
                    noticeAdapter.clear();
                }
                noticeAdapter.setData(data);
            }else{
                noticeAdapter.setRefreshing(false);
            }
        }
    };
    private ModelStatusListener<ModelEvent, Model> noticeListener = new ModelStatusListener<ModelEvent, Model>() {
        @Override
        public void onModelEvent(ModelEvent event, Model model) {
            refreshLayoutControl.triggerRefresh();
        }
    };


    private View.OnClickListener onPublishClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, NoticeDetailViewController.class);
			intent.putExtra(NoticeDetailViewController.KEY_TYPE, CommonUtil.DEFAULT_ARG);
			getActivity().startActivityForResult(intent, 1);
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        ImageView publicButton = (ImageView) setActionBarLayout(R.layout.ab_title_white_btn).findViewById(R.id.ab_right_button);
        publicButton.setImageResource(R.drawable.ic_151020_add_notice);
        publicButton.setOnClickListener(onPublishClickListener);
        setTitle("我的公告");
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(refreshListener);
        ListView noticeListView = (ListView) rootView.findViewById(R.id.notice_list_view);
        noticeListView.setAdapter(noticeAdapter);
        noticeListView.setOnItemClickListener(onItemClickListener);
        noticeListView.setOnItemLongClickListener(onItemLongClickListener);
        noticeAdapter.setPageControl(pageControl);
        refreshLayoutControl.triggerRefreshDelayed();
        NoticeManager.getInstance().registerListener(noticeListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NoticeManager.getInstance().unregisterListener(noticeListener);
    }

    private void loadPage(int page) {
        UserModel user = UserManager.getInstance().getCurrentUser();
        int sellerId;
        if(user != null){
            sellerId = Integer.parseInt(user.getUserID());
        }else{
            Toast.makeText(ContextProvider.getContext(),"请登录后重试",Toast.LENGTH_LONG).show();
            return;
        }
        String url = URLApi.NOTIFICATIONS_ALL;
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("saler_id", String.valueOf(sellerId));
        req.addParam("pageNo", String.valueOf(page));
        req.addParam("page_size", "10");
        req.setAttachment(page);
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void delNotice(long id) {
        String url = URLApi.NOTIFICATIONS_DEL;
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, deleteListener);
        req.addParam("id", String.valueOf(id));
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> deleteListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if(data.isSuccess()){
                refreshLayoutControl.triggerRefresh();
            }else{
                ResultHandler.handleError(data, NoticeListViewController.this);
            }
        }
    };

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
            final Model model = noticeAdapter.getItem(position);
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("您确定要取消该活动公告吗?");
            dialog.setActionSheet(getActionSheet(true));
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delNotice(model.getLong("id"));
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Model notice = noticeAdapter.getItem(i);
            if(notice.getBoolean("valid")){
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, NoticeDetailViewController.class);
                intent.putExtra(NoticeDetailViewController.KEY_NOTICE, notice);
                intent.putExtra(NoticeDetailViewController.KEY_TYPE, CommonUtil.TYPE_UPDATE_NOTICE);
                getActivity().startActivityForResult(intent, 1);
            }
        }
    };

}
