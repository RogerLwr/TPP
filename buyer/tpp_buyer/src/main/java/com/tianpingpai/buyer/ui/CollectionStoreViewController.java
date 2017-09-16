package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brother.tpp.net.URLUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.StoreAdapter;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

@EmptyView(emptyLayout = R.layout.empty_collection_product,imageResource = R.drawable.empty_collection_store,text = R.string.collection_store)
@ActionBar(hidden = true)
@Layout(id = R.layout.ui_collection_store)
public class CollectionStoreViewController extends BaseViewController {

    private CollectionViewController mainViewController ;
    public void setMainViewController(CollectionViewController baseViewController){
        this.mainViewController = baseViewController;
    }


    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.store_list_view)
    private ListView listView;

    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadData(1);
        }
    };

    private StoreAdapter storeAdapter = new StoreAdapter();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        storeAdapter.setPageControl(pageControl);
        View v = getActivity().getLayoutInflater().inflate(R.layout.bg_gray_dc,null);
        listView.addHeaderView(v);
        listView.setAdapter(storeAdapter);
        listView.setOnItemClickListener(storeItemOnClickListener);
        listView.setOnItemLongClickListener(storeItemLongClickListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
    }

    private void loadData(int page){

        String url = URLApi.getBaseUrl()+"/api/favorite/getBuyFavoriteListInfo.json";
        HttpRequest<ListResult<Model>> req = new HttpRequest<>(url, listener);
        req.addParam("page", page + "");
        req.addParam("pageSize", 10 + "");
        JSONListParser parser = new JSONListParser();
        req.setParser(parser);
        req.setAttachment(page);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        if (page == 1) {//1 means onReloadData
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }
    }

    private HttpRequest.ResultListener<ListResult<Model>> listener = new HttpRequest.ResultListener<ListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ListResult<Model>> request, ListResult<Model> data) {
            showContent();
            if(data.isSuccess()){
                mainViewController.setStoreNum(data.getPage().getTotalItemCount());
                mainViewController.refreshView(true);
                if (request.getAttachment(Integer.class) == 1) {
                    storeAdapter.clear();
//                    mainViewController.refreshView(true);
                }
                storeAdapter.setData(data);
            }else if(data.getCode() == 5){//TODO
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if(storeAdapter.getModels().size() == 0){
                showEmptyView();
                getView().findViewById(R.id.button).setOnClickListener(buttonListener);
            }else {
                hideEmptyView();
            }
        }
    };

    private AdapterView.OnItemClickListener storeItemOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(position == 0){
                return;
            }else {
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, StoreViewController.class);
                Model model = storeAdapter.getItem(position-1);

                intent.putExtra(StoreDataContainer.KEY_STORE_TYPE,
                        StoreDataContainer.STORE_TYPE_NORMAL);
                intent.putExtra("shop_id", model.getInt("sale_user_id"));
                intent.putExtra(StoreDataContainer.KEY_SHOP_NAME, model.getString("sale_name"));
                getActivity().startActivity(intent);
            }
        }
    };

    private AdapterView.OnItemLongClickListener storeItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i == 0){
                return false;
            }else {
                final int ii = i-1;
                final ActionSheetDialog dialog = new ActionSheetDialog();
                dialog.setActionSheet(new ActionSheet());
                dialog.setMessage("");
                dialog.setCancelButtonHidden(false);
                dialog.setActivity(getActivity());
                dialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteStore(storeAdapter.getItem(ii));
                        dialog.dismiss();
                    }
                });
                dialog.setTitle("确定删除该店铺吗?");
                dialog.show();
                return true;
            }
        }
    };

    private void deleteStore(final Model model){
        String url = URLUtil.DEL_STORE_OR_PROD_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url,new HttpRequest.ResultListener<ModelResult<Model>>(){
            @Override
            public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
                if(data.isSuccess()){
                    storeAdapter.getModels().remove(model);
                    storeAdapter.notifyDataSetChanged();
                    hideSubmitting();
                    refreshLayoutControl.triggerRefreshDelayed();
                }
            }
        });
        req.setMethod(HttpRequest.POST);
        req.addParam("salerId", model.getInt("sale_user_id") + "");
        req.addParam("type","1");
        req.addParam("salerType",model.getInt("type")+"");
        GenericModelParser parser = new GenericModelParser();
        req.setParser(parser);
        if(user != null){
            req.addParam("accessToken", user.getAccessToken());
        }
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showSubmitting();
    }

    private View.OnClickListener buttonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.e("button","onclick");
            Intent i = new Intent(getActivity(), ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,MainViewController.class);
            getActivity().startActivity(i);
            getActivity().finish();
        }
    };

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData(1);
    }
}
