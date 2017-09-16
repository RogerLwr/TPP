package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.StoreAdapter;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@Statistics(page = "收藏店铺")
@ActionBar(title = "收藏店铺")
@EmptyView(imageResource = R.drawable.empty_mushroom,text = R.string.empty_favorite_store)
@Layout(id = R.layout.ui_favourite_stores)
public class FavoriteStoresViewController extends BaseViewController{

    private StoreAdapter adapter = new StoreAdapter();

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();

        adapter.setShowCoupons(false);
        ListView storeListView = (ListView) rootView.findViewById(R.id.store_list_view);
        storeListView.setAdapter(adapter);
        storeListView.setOnItemClickListener(itemClickListener);
        storeListView.setOnItemLongClickListener(itemLongClickListener);
        adapter.setPageControl(pageControl);
        showContent();
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            refreshLayout.setRefreshing(false);
            if(data.isSuccess()) {
                if(request.getAttachment(Integer.class) == 1){
                    adapter.clear();
                }
                adapter.setData(data);
                if(adapter.isEmpty()){
                    showEmptyView();
                }else {
                    hideEmptyView();
                }
            }else{
                ResultHandler.handleError(data,FavoriteStoresViewController.this);
            }
        }
    };

    private void loadPage(int page){
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(URLUtil.GET_BUY_FAVORITE_LIST_INFO_URL,listener);
        req.addParam("page", "" + page);
        req.addParam("page_size", "10");
        req.setParser(new JSONListParser());
        req.setAttachment(page);
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadPage(page);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> deleteListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if(data.isSuccess()){
                refreshLayoutControl.triggerRefresh();
            }
        }
    };

    private void delete(Model m){
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(URLUtil.DEL_FAVORITE_SALER_URL,deleteListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("id", m.getInt("id") + "");
        req.addParam("sale_user_id", m.getInt("sale_user_id") + "");
        req.addParam("type",m.getInt("type") + "");
        req.setParser(new ModelParser<>(Void.class));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadPage(1);
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Model model = adapter.getItem(i);
            int type = model.getInt("type");
            Intent intent = new Intent(getActivity(),ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,StoreViewController.class);

            intent.putExtra(StoreDataContainer.KEY_STORE_ID, model.getInt("sale_user_id"));
            if(type == CommonUtil.TYPE_COLLECT_MULTI_SHOP){
                intent.putExtra(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_MULTI_WITH_ID);
            }else{
                intent.putExtra(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_NORMAL);
                intent.putExtra("sale_name", model.getString("sale_name"));
                intent.putExtra("shop_name", model.getString("sale_name"));
            }
            getActivity().startActivity(intent);
        }
    };

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            final Model model = adapter.getItem(i);
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("您确定要取消该店铺收藏吗?");
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(model);
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }
    };
}
