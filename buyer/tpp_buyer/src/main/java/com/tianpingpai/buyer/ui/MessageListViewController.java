package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.MessageAdapter;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "消息列表")
@ActionBar(title = "推送消息")
@EmptyView(imageResource = R.drawable.empty_message,text = R.string.empty_message)
@Layout(id = R.layout.ui_message_list)
public class MessageListViewController extends BaseViewController {
    private MessageAdapter adapter = new MessageAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshLayoutListener);
        ListView messageListView = (ListView) rootView.findViewById(R.id.message_list_view);
        messageListView.setAdapter(adapter);
        messageListView.setOnItemClickListener(itemClickListener);
        refreshLayoutControl.triggerRefresh();
        adapter.setPageControl(pageControl);
    }

    private void loadData(int page) {
        String url = URLUtil.GETPUSHMESSAGE_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("user_id", user.getUserID() + "");
        req.addParam("user_type", UserModel.USER_TYPE_BUYER + "");
        req.addParam("page", page + "");
        req.addParam("page_size", "10");
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
        if(page == 1) {
            showLoading();
        }
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            hideLoading();
            showContent();
            if (data.isSuccess()) {
                adapter.setData(data);
                if(adapter.isEmpty()){
                    showEmptyView();
                }else{
                    hideEmptyView();
                }
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshLayoutListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,MessageDetailViewController.class);
            intent.putExtra(MessageDetailViewController.KEY_TITLE,adapter.getItem(i).getString("type_name"));
            intent.putExtra(MessageDetailViewController.KEY_CONTENT, adapter.getItem(i).getString("content"));
            getActivity().startActivity(intent);
        }
    };
}
