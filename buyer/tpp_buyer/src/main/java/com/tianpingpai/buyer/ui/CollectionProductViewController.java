package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.CollectionProductAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartEvent;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.parser.CollectionProductJSONListParser;
import com.tianpingpai.buyer.parser.CollectionProductListResult;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.JSONListParser;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.ModelAdapter;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
@ActionBar(hidden = true)
@EmptyView(emptyLayout = R.layout.empty_collection_product,imageResource = R.drawable.empty_collection_product,text = R.string.collection_product)
@Layout(id = R.layout.ui_collection_product)
public class CollectionProductViewController extends BaseViewController {

    private CollectionViewController mainViewController ;
    public void setMainViewController(CollectionViewController baseViewController){
        this.mainViewController = baseViewController;
    }

    private HashSet<ProductModel> selectProduct = new HashSet<>();

    @Binding(id = R.id.refresh_layout)
    private SwipeRefreshLayout refreshLayout;
    @Binding(id = R.id.collect_product_list_view)
    private ListView listView;
    @Binding(id = R.id.price_sum_text_view)
    private TextView priceSumTextView;
    @Binding(id = R.id.products_num_text_view)
    private TextView productsNumTextView;

    public SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            productsMap.clear();
            productModels.clear();
            loadData(1);
        }
    };

    private ModelAdapter.PageControl<Model> pageControl = new ModelAdapter.PageControl<Model>() {
        @Override
        public void onLoadPage(int page) {
            loadData(page);
        }
    };

    private CollectionProductAdapter collectionProductAdapter = new CollectionProductAdapter();

    private ModelStatusListener<ShoppingCartEvent, ProductModel> productsListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
        @Override
        public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
            updateNumbers();
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        collectionProductAdapter.setActivity(getActivity());
        collectionProductAdapter.setPageControl(pageControl);
        collectionProductAdapter.setCollectionProductViewController(this);
        listView.setAdapter(collectionProductAdapter);
        ShoppingCartManager.getInstance().registerListener(productsListener);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(mOnRefreshListener);
        refreshLayoutControl.triggerRefreshDelayed();
    }

    @Override
    protected void onDestroyView() {
        super.onDestroyView();
        ShoppingCartManager.getInstance().unregisterListener(productsListener);
    }

    private void loadData(int page){

        String url = URLApi.getBaseUrl() + "/api/prod/favourite";
        HttpRequest<CollectionProductListResult<Model>> req = new HttpRequest<>(url,listener);
        req.addParam("pageNo", page + "");
        req.addParam("pageSize", 10 + "");
        CollectionProductJSONListParser parser = new CollectionProductJSONListParser();
        req.setParser(parser);
        req.setAttachment(page);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
//        if(page == 1){
//            showLoading();
//        }
        if (page == 1) {//1 means onReloadData
            req.setAttachment(Boolean.TRUE);
            if (refreshLayout != null && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }
    }

    private LinkedHashMap<Integer,ArrayList<Model>> productsMap = new LinkedHashMap<>();
    private ArrayList<Model> productModels = new ArrayList<>() ;

    private HttpRequest.ResultListener<CollectionProductListResult<Model>> listener = new HttpRequest.ResultListener<CollectionProductListResult<Model>>() {
        @Override
        public void onResult(HttpRequest<CollectionProductListResult<Model>> request, CollectionProductListResult<Model> data) {
            hideLoading();
            if(data.isSuccess()){
                productModels = new ArrayList<>();
                if (request.getAttachment(Integer.class) == 1) {
                    collectionProductAdapter.clear();
                }
                mainViewController.setProductNum(data.getModel().getInt("num"));
                mainViewController.refreshView(false);
                ArrayList<Model> list = data.getModels();
                for (int i = 0 ; i < list.size() ; i++){
                    int salerId = list.get(i).getInt("salerId");
                    String salerName = list.get(i).getString("salerName");
                    int salesProdNum = list.get(i).getInt("salesProdNum");
                    ArrayList<Model> prodList = (ArrayList<Model>) list.get(i).getList("prodList",Model.class);
                    if(productsMap.containsKey(salerId)){
                        for (int o = 0 ; o < prodList.size() ; o++){
                            prodList.get(o).set("show_top",false);
                            prodList.get(o).set("salerName",salerName);
                            prodList.get(o).set("salerId",salerId);
                            prodList.get(o).set("salesProdNum",salesProdNum);
                            productsMap.get(salerId).add(prodList.get(o));
                        }
                    }else{
                        if(prodList != null){
                            productsMap.put(salerId,new ArrayList<Model>());
                            for (int j = 0 ; j < prodList.size() ; j++){
                                if(j == 0){
                                    prodList.get(j).set("show_top",true);
                                }else {
                                    prodList.get(j).set("show_top",false);
                                }
                                prodList.get(j).set("salerName",salerName);
                                prodList.get(j).set("salerId",salerId);
                                prodList.get(j).set("salesProdNum",salesProdNum);
                                productsMap.get(salerId).add(prodList.get(j));
                            }
                        }
                    }
                }
                for (Map.Entry<Integer, ArrayList<Model>> o : productsMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    ArrayList<Model> arrayList = (ArrayList<Model>) entry.getValue();
                    for (int m = 0; m < arrayList.size(); m++) {
                        productModels.add(arrayList.get(m));
                    }
                }

                data.setModels(productModels);
                collectionProductAdapter.setData(data);
            }else if(data.getCode() == 5){//TODO
//                notValidatedContainer.setVisibility(View.VISIBLE);
            }
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
            if(collectionProductAdapter.getModels().size() ==0){
                showEmptyView();
                getView().findViewById(R.id.button).setOnClickListener(buttonListener);
            }else{
                hideEmptyView();
            }

        }
    };

    private void updateNumbers(){
        int num = 0;
        double totalPrice = 0;
        ArrayList<Model> list = collectionProductAdapter.getModels();
        if(list != null){
            for (int i = 0 ;i <list.size() ; i++){
                Model m = list.get(i);
                if(m.getInt("prod_num") > 0){
                    num += 1;
                }
                Log.e("prod_num----couponPrice",""+m.getInt("prod_num"+ "----"+m.getDouble("couponPrice")));
                totalPrice += (m.getInt("prod_num")*m.getDouble("couponPrice"));
            }
        }
        String price = "￥" + PriceFormat.format(totalPrice);
        productsNumTextView.setText("数量:"+num);
        priceSumTextView.setText(price);
        mainViewController.refreshBadgeView(num);
    }

    @OnClick(R.id.add_shopping_cart_button)
    private View.OnClickListener addShoppingCartButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            ArrayList<Model> list = collectionProductAdapter.getModels();
            for (int i = 0 ;i < list.size() ; i++){
                ProductModel p = new ProductModel();
                p.setId(list.get(i).getInt("prodId"));
                p.setProductNum(list.get(i).getInt("prod_num"));
                selectProduct.add(p);
            }
            ShoppingCartManager.getInstance().addToShoppingCart(selectProduct);

        }
    };


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

    @Override
    public boolean onBackKeyDown(Activity a) {
        if(collectionProductAdapter.as != null){
            if(collectionProductAdapter.as.isShowing()){
                collectionProductAdapter.as.dismiss();
                return true;
            }
        }
        return super.onBackKeyDown(a);
    }
}
