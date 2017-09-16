package com.tianpingpai.crm.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.crm.adapter.MessageAdapter;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@ActionBar(title = "消息列表")
@EmptyView(imageResource = R.drawable.empty_message,text = R.string.empty_message)
@Layout(id = R.layout.view_controller_messages_list)
public class MessagesListViewController extends CrmBaseViewController{


    private MessageAdapter adapter = new MessageAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshLayoutListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
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
//        messageListView.setOnItemClickListener(itemClickListener);
        refreshLayoutControl.triggerRefresh();
        adapter.setPageControl(pageControl);

    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        }
    };

    private void loadData(int page){
        String url = URLApi.getBaseUrl()+"/api/msg/getMsgInfo.json";
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("user_id", user.getId() + "");
        req.addParam("user_type", "3");
        req.addParam("page", page + "");
        req.addParam("page_size", "10");
        req.setParser(new JSONListParser());
        VolleyDispatcher.getInstance().dispatch(req);
        if(page==1){
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
}
