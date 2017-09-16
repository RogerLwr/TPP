package com.tianpingpai.seller.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.http.util.ErrorReporter;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.ProductManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.adapter.CategoryAdapter;
import com.tianpingpai.seller.adapter.ProductAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.LayeredProduct;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.parser.ShopProductsParser;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.widget.SwipeRefreshLayoutControl;

import java.util.ArrayList;

@SuppressWarnings("unused")
@ActionBar(layout = R.layout.ab_store_window)
@Statistics(page = "我的商品")
@Layout(id = R.layout.ui_store_window)
public class StoreWindowViewController extends BaseViewController {

    @Binding(id = R.id.tab_layout)
    private TabLayout tabLayout;
    @Binding(id = R.id.ab_edit_button)
    private TextView editButton;
    private ListView categoryListView;

    private CategoryAdapter categoryAdapter = new CategoryAdapter();
    private ProductAdapter productAdapter = new ProductAdapter();
    private SwipeRefreshLayoutControl refreshLayoutControl = new SwipeRefreshLayoutControl();

    private ModelStatusListener<ModelEvent, ProductModel> onModelStatusChangeListener = new ModelStatusListener<ModelEvent, ProductModel>() {
        @Override
        public void onModelEvent(ModelEvent event, ProductModel model) {
            switch (event){
                case OnModelUpdate:
                    refreshLayoutControl.triggerRefresh();
                    break;
            }
        }
    };
    private ModelStatusListener<UserEvent, UserModel> userLoginListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event){
                case Login:
                    refreshLayoutControl.triggerRefresh();
                    break;
                case Logout:
                    categoryAdapter.setLayeredProduct(null);
                    break;
            }
        }
    };
    private TabLayout.OnTabSelectedListener firstCategoryTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            categoryAdapter.selectFirstCategory(tab.getPosition());
            if(categoryAdapter.getCount() == 0){
                categoryListView.setVisibility(View.GONE);
            }else{
                categoryListView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {}

        @Override
        public void onTabReselected(TabLayout.Tab tab) {}
    };
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadProducts();
        }
    };

    //TODO bug fix refer to http://stackoverflow.com/a/12892317/1745885
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                View currentFocus = getActivity().getCurrentFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        refreshLayoutControl.setSwipeRefreshLayout(refreshLayout);
        refreshLayoutControl.setOnRefreshListener(onRefreshListener);
        categoryListView = (ListView) rootView
                .findViewById(R.id.category_list_view);
        categoryListView.setAdapter(categoryAdapter);
        productAdapter.setActivity(getActivity());
        productAdapter.setRefreshListener(new ProductAdapter.RefreshListener() {
            @Override
            public void refreshProduct() {
                refreshLayoutControl.triggerRefresh();
            }
        });
        categoryListView.setOnItemClickListener(categoryAdapter);
        ListView productListView = (ListView) rootView
                .findViewById(R.id.product_list_view);
        productListView.setAdapter(productAdapter);
//        productListView.setOnItemClickListener(productItemClickListener);
        productListView.setOnItemLongClickListener(productItemLongClickListener);
        productListView.setOnScrollListener(onScrollListener);
        categoryAdapter.setProductAdapter(productAdapter);
        tabLayout.setOnTabSelectedListener(firstCategoryTabSelectedListener);
        updateEditMode();
        ProductManager.getInstance().registerListener(onModelStatusChangeListener);
        UserManager.getInstance().registerListener(userLoginListener);
        refreshLayoutControl.triggerRefreshDelayed();
    }

    public void setEditMode(boolean editMode){
        productAdapter.setEditMode(editMode);
        updateEditMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProductManager.getInstance().unregisterListener(onModelStatusChangeListener);
        UserManager.getInstance().unregisterListener(userLoginListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryAdapter.refresh();
    }

    private HttpRequest.ResultListener<ModelResult<LayeredProduct>> listener = new HttpRequest.ResultListener<ModelResult<LayeredProduct>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<LayeredProduct>> request, ModelResult<LayeredProduct> data) {
            refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
            if(data.isSuccess()){
                if(data.getModel() == null){
                    //TODO 数据异常
                    ErrorReporter.reportError(request, "null pointer exception", new NullPointerException());
                    return;
                }
                int selected = tabLayout.getSelectedTabPosition();
                int originalX = tabLayout.getScrollX();
                selected = selected == -1 ? 0 : selected;
                tabLayout.removeAllTabs();
                for(LayeredProduct.FirstCategory fc:data.getModel().getCategories()){
                    tabLayout.addTab(tabLayout.newTab().setText(fc.getName()));
                }
                categoryAdapter.setLayeredProduct(data.getModel());
                if(categoryAdapter.getCount() == 0){
                    categoryListView.setVisibility(View.GONE);
                }else{
                    categoryListView.setVisibility(View.VISIBLE);
                }
                productAdapter.setModels(data.getModel().getProducts());
                SearchViewController.setSearchList(productAdapter.getModels());
                ProductManager.getInstance().notifyEvent(ModelEvent.OnModelsGet, null);
                if(selected < tabLayout.getTabCount()){
                    TabLayout.Tab tab = tabLayout.getTabAt(selected);
                    if(tab != null) {
                        tab.select();
                    }
                    tabLayout.scrollTo(originalX,0);
                    categoryAdapter.selectFirstCategory(selected);
                }
            }else{
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_LONG).show();
            }
        }
    };

    public void loadProducts() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            return;//TODO
        }
        String url = URLApi.GET_PROD_LIST_INFO_URL;
        HttpRequest<ModelResult<LayeredProduct>> req = new HttpRequest<>(
                url, listener);
        req.addParam("user_id", user.getUserID());
        ShopProductsParser parser = new ShopProductsParser();
        req.setParser(parser);
        parser.setShopName("");
        req.addParam("version", "1.1.1");//TODO
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                refreshLayoutControl.getSwipeRefreshLayout().setRefreshing(false);
                Toast.makeText(ContextProvider.getContext(),error.getErrorMsg(),Toast.LENGTH_LONG).show();
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private void updateEditMode(){
        if(editButton == null){
            return;
        }
        if (!productAdapter.isEditMode()) {
            editButton.setText("编辑");
        } else {
            editButton.setText("完成");
        }
    }

    @OnClick(R.id.ab_edit_button)
    private View.OnClickListener editButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(productAdapter.isEditMode()){
                submitPrices();
            }
            productAdapter.setEditMode(!productAdapter.isEditMode());
            updateEditMode();
        }
    };

    @OnClick(R.id.ab_add_button)
    private View.OnClickListener addButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(getActivity(),ContainerActivity.class);
            mIntent.putExtra(ContainerActivity.KEY_CONTENT,SelectCategoryViewController.class);
            getActivity().startActivity(mIntent);
        }
    };

    @OnClick(R.id.ab_search_button)
    private View.OnClickListener searchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SearchViewController.class);
            SearchViewController.setSearchList(categoryAdapter.getAllProducts());
            getActivity().startActivity(intent);
        }
    };
    /*private AdapterView.OnItemClickListener productItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent mIntent = new Intent(getActivity(),ContainerActivity.class);
            ProductModel productModel = productAdapter.getItem(position);
            mIntent.putExtra(ContainerActivity.KEY_CONTENT, EditProductViewController.class);
            mIntent.putExtra(EditProductViewController.KEY_PRODUCT, productModel);
            mIntent.putExtra(EditProductViewController.KEY_ADD, false);
            getActivity().startActivity(mIntent);
        }
    };*/

    private AdapterView.OnItemLongClickListener productItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            final long prodId = productAdapter.getItem(i).getId();
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("您确定要删除该商品吗?");
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteProd(prodId);
                    dialog.dismiss();
                }
            });
            dialog.show();

            return true;
        }
    };

    private void deleteProd(long prodId){
        String url = URLApi.DELETE_PROD_URL;
        HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, deleteProdListener);
        req.setMethod(HttpRequest.POST);
        req.setParser(new ModelParser<>(Void.class));
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError error) {
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        req.addParam("ids", String.valueOf(prodId));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private HttpRequest.ResultListener<ModelResult<Void>> deleteProdListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "删除商品成功!", Toast.LENGTH_SHORT).show();
                loadProducts();
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Void>> changePriceListener = new HttpRequest.ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request, ModelResult<Void> data) {
            if (data.isSuccess()) {
                Toast.makeText(ContextProvider.getContext(), "修改价格成功!", Toast.LENGTH_SHORT).show();
                loadProducts();
                productAdapter.clearChanged();
            } else {
                Toast.makeText(ContextProvider.getContext(), data.getDesc(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void submitPrices() {
        ArrayList<Model> changedModels = productAdapter.getChangedModels();
        if (changedModels.isEmpty()) {
            Toast.makeText(ContextProvider.getContext(), "无任何修改", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < changedModels.size(); i++) {
                Model m = changedModels.get(i);
                sb.append(m.toJsonString());
                if (i != changedModels.size() - 1) {
                    sb.append(",");
                }
            }
            //TODO move into some other class
            if (sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");
            String url = URLApi.UPDATE_PROD_PRICE_URL;
            HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, changePriceListener);
            req.setMethod(HttpRequest.POST);
            req.setParser(new ModelParser<>(Void.class));
            req.setErrorListener(new HttpRequest.ErrorListener() {
                @Override
                public void onError(HttpRequest<?> request, HttpError error) {
                    Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                }
            });
            req.addParam("prod_prices", sb.toString());
            VolleyDispatcher.getInstance().dispatch(req);
        }
    }
}
