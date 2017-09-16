package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.SellerUrlInterceptor;
import com.tianpingpai.seller.adapter.EnvelopeAdapter;
import com.tianpingpai.seller.fragment.ResultHandler;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.AutoResizeActivity;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Layout(id = R.layout.ui_envelope_list,
        actionBarLayout = R.layout.ab_title_white)
@Statistics(page = "红包列表")
@SuppressWarnings("unused")
public class EnvelopeListViewController extends BaseViewController {

//    private TextView sumAmountTextView;
    @Binding(id = R.id.sum_number_text_view,format = "您共收到{{bonusNum}}个红包")
    private TextView sumNumberTextView;

    private EnvelopeAdapter adapter = new EnvelopeAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> mPageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadPage(page);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPage(1);
        }
    };

    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        View view = setActionBarLayout(R.layout.ab_title_green);
        Toolbar toolbar = (Toolbar) view.findViewById(com.tianpingpai.foundation.R.id.toolbar);
        if(toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back_white);
        }
//        sumAmountTextView = (TextView) rootView.findViewById(R.id.sum_amount_text_view);
        sumNumberTextView = (TextView) rootView.findViewById(R.id.sum_number_text_view);
        ListView envelopesListView = (ListView) rootView.findViewById(R.id.envelopes_list_view);
        envelopesListView.setAdapter(adapter);
        envelopesListView.setOnItemClickListener(envelopeItemClickListener);
        adapter.setPageControl(mPageControl);
        adapter.setListView(envelopesListView);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
        showContent();
    }

    private void loadPage(int page) {
        String url = ContextProvider.getBaseURL() + "/api/bonus/list";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, resultListener);
        req.setParser(new JSONListParser());
        req.setAttachment(page);
        req.addParam("pageNo", page + "");
        req.addParam("pageSize", "10");
        if (page == 1) {
            loadSummary();
        }
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ListResult<Model>> resultListener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if (data.isSuccess()) {
                showContent();
                if (request.getAttachment(Integer.class) == 1) {
                    adapter.clear();
                }
                adapter.setData(data);
            } else {
                ResultHandler.handleError(data, EnvelopeListViewController.this);
            }
        }
    };

    private void loadSummary() {
        String url = ContextProvider.getBaseURL() + "/api/bonus/summary";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, summaryListener);
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayoutControl.triggerRefresh();
    }

    private HttpRequest.ResultListener<ModelResult<Model>> summaryListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            if (data.isSuccess()) {
                showContent();
                Model m = data.getModel();
//                sumAmountTextView.setText("￥" + m.getDouble("bonusMny") + "");
//                int num = (int) m.getDouble("bonusNum");
//                sumNumberTextView.setText("您共收到" + num + "个红包");
                getBinder().bindData(m);
            } else {
                ResultHandler.handleError(data, EnvelopeListViewController.this);
            }
        }
    };

    private AdapterView.OnItemClickListener envelopeItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int status = adapter.getItem(position).getInt("status");
            //TODO 1:open 2:not_open 3:expired
            if (status == 1) {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, EnvelopeDetailViewController.class);
                int eid = adapter.getItem(position).getInt("id");
                intent.putExtra(EnvelopeDetailViewController.KEY_ID, eid);
                getActivity().startActivity(intent);
            } else if (status == 2) {
                Intent intent = new Intent(getActivity(), AutoResizeActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, WebViewController.class);
                intent.putExtra(WebViewController.KEY_CAN_GO_BACK, false);
                intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_NORMAL);
                String url = URLApi.getWebBaseUrl() + "/apply/bonus/open?id=" + adapter.getItem(position).getInt("id");
                Log.e("xx", "url:" + url);
                intent.putExtra(WebViewController.KEY_URL, url);
                intent.putExtra(WebViewController.KEY_URL_INTERCEPTOR, SellerUrlInterceptor.class);
                getActivity().startActivity(intent);
            } else {
                Toast.makeText(ContextProvider.getContext(), "红包已过期", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
