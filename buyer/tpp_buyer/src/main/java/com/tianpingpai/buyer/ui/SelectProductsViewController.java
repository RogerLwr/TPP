package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.FirstCategoryAdapter;
import com.tianpingpai.buyer.adapter.ProductSelectionAdapter;
import com.tianpingpai.buyer.adapter.ProductSelectionAdapter.ProductSelectionListener;
import com.tianpingpai.buyer.manager.ShoppingCartEvent;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.LayeredProduct;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.parser.MultiShopParser;
import com.tianpingpai.buyer.parser.ShopProductsParser;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.DimensionUtil;
import com.tianpingpai.widget.TabBarView;

import java.util.ArrayList;
import java.util.HashSet;

@ActionBar(title = "增加常用")
@Layout(id = R.layout.ui_select_products)
public class SelectProductsViewController extends BaseViewController {
    private int storeId;
    private LayeredProduct layeredProduct;

    private SubCategoryAdapter adapter;
//    private String shopName;
    private TabBarView tabBarView;
    private TextView selectionNumberTextView;
    private HashSet<ProductModel> selectionSet = new HashSet<>();

    private ResultListener<ModelResult<LayeredProduct>> listener = new ResultListener<ModelResult<LayeredProduct>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<LayeredProduct>> request,
                             ModelResult<LayeredProduct> data) {
            hideLoading();
            showContent();
            if (data.isSuccess()) {
                layeredProduct = data.getModel();
                adapter.notifyDataSetChanged();
                tabBarView.notifyDataSetChanged();
            } else {
                ResultHandler.handleError(data, SelectProductsViewController.this);
            }
        }
    };

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        tabBarView = (TabBarView) rootView.findViewById(R.id.tab_bar_view);
        tabBarView.setStripColor(getActivity().getResources().getColor(R.color.green_9b));
        tabBarView.setStripHeight(DimensionUtil.dip2px(2));
        adapter = new SubCategoryAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabBarView.setViewPager(viewPager);
        rootView.findViewById(R.id.select_button).setOnClickListener(selectButtonListener);
        selectionNumberTextView = (TextView) rootView.findViewById(R.id.selection_number_text_view);
        storeId = getActivity().getIntent().getIntExtra("shop_id", 0);
        loadData(null);
    }

    private boolean isMultiShop() {
        return getActivity().getIntent().getIntExtra("shop_type",
                StoreDataContainer.STORE_TYPE_NORMAL) == StoreDataContainer.STORE_TYPE_MULTI;
    }

    private void loadData(String shopName) {
        String url = isMultiShop() ? URLUtil.GET_MULTI_PROD_LIST_INFO_URL
                : URLUtil.GET_SALE_USER_PROD_LIST_INFO_URL;

        HttpRequest<ModelResult<LayeredProduct>> req = new HttpRequest<>(url,
                listener);

        if (shopName != null) {
            req.setAttachment(shopName);
        }

        int catId = getActivity().getIntent().getIntExtra("category_id", -1);
        if (catId != -1) {
            req.addParam("category_id", catId + "");
        }

        if (storeId != CommonUtil.DEFAULT_ARG) {
            req.addParam("user_id", storeId + "");
        }

        if (isMultiShop()) {
            MultiShopParser parser = new MultiShopParser();
            req.setParser(parser);
        } else {
            ShopProductsParser parser = new ShopProductsParser();
            req.setParser(parser);
            parser.setShopName(shopName);
        }
		req.addParam("version", getVersion(ContextProvider.getContext()));

//        MarketModel market = MarketManager.getInstance().getCurrentMarket();
//        if (market != null) {
//			req.addParam("market_id", market.getId() + "");
//        }
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    //TODO
    public static String getVersion(Context context) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.4.0";
    }

    private ResultListener<ModelResult<Void>> submitListener = new ResultListener<ModelResult<Void>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Void>> request,
                             ModelResult<Void> data) {
            if (data.isSuccess()) {
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            } else {
                ResultHandler.handleError(data, SelectProductsViewController.this);
            }
        }
    };

    private OnClickListener selectButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            HashSet<Integer> shopIds = new HashSet<>();

            for (ProductModel pm : selectionSet) {
                shopIds.add(pm.getSellerId());
            }

            for (Integer i : shopIds) {
                StringBuilder sb = new StringBuilder();
                for (ProductModel pm : selectionSet) {
                    int sid = i;
                    if (sid == pm.getSellerId()) {
                        sb.append(pm.getId());
                        sb.append(",");
                    }
                }
                String stringProd_ids = sb.toString().trim();
                String prod_ids = null;
                final int length = stringProd_ids.length();
                if (length > 1) {
                    prod_ids = stringProd_ids.substring(0, length - 1);
                }

                String url = URLUtil.ADD_COMMONUSE_URL;

                UserModel user = UserManager.getInstance().getCurrentUser();
                HttpRequest<ModelResult<Void>> req = new HttpRequest<>(url, submitListener);
                req.addParam("saler_id", i + "");
                req.addParam("prod_id", prod_ids);
                ModelParser<Void> parser = new ModelParser<>(Void.class);
                req.setParser(parser);
                if (user != null) {
                    req.addParam("accessToken", user.getAccessToken());
                }
                req.setErrorListener(new CommonErrorHandler(SelectProductsViewController.this));
                VolleyDispatcher.getInstance().dispatch(req);
            }
        }
    };

    private void updateBottomPanel() {
        selectionNumberTextView.setText(String.format("已选品类:%d", selectionSet.size()));
    }

    public static class SubCategoryFragment extends Fragment {

        private LayeredProduct.FirstCategory firstCategory;
        private int storeId;
        private SelectProductsViewController productsFragment;

        public void notifyDataSetChanged() {
            productAdapter.notifyDataSetChanged();
        }

        public int getStoreId() {
            return storeId;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }

        public void setFirstCategory(LayeredProduct.FirstCategory firstCategory) {
            this.firstCategory = firstCategory;
        }

        private FirstCategoryAdapter catAdapter = new FirstCategoryAdapter();

        {
            catAdapter.setEnableBadge(false);
        }

        private ProductSelectionAdapter productAdapter = new ProductSelectionAdapter();

        {
            productAdapter.setSelectionListener(new ProductSelectionListener() {
                @Override
                public void onSelectionChange(ProductModel pm, boolean isSelected) {
                    if (isSelected) {
                        productsFragment.selectionSet.add(pm);
                    } else {
                        productsFragment.selectionSet.remove(pm);
                    }
                    productsFragment.updateBottomPanel();
                }

                @Override
                public boolean contains(ProductModel pm) {
                    return productsFragment.selectionSet.contains(pm);
                }
            });
        }

        private OnItemClickListener categoryItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (catAdapter.getCurrentSelection() == catAdapter.getItem(position)) {
                    return;
                }
                ArrayList<ProductModel> products = firstCategory.getSecondCategories().get(position).getProducts();
                productAdapter.setModels(products);
                catAdapter.setCurrentSelection(catAdapter.getItem(position));
            }
        };
        private View rootView;

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null) {
                    parent.removeView(rootView);
                }
                return rootView;
            } else {
                rootView = inflater.inflate(R.layout.fragment_store_sub_category, null);
            }

            ListView categoryListView = (ListView) rootView.findViewById(R.id.category_list_view);
            categoryListView.setAdapter(catAdapter);
            categoryListView.setOnItemClickListener(categoryItemClickListener);
            if (firstCategory != null) {
                catAdapter.setModels(firstCategory.getSecondCategories());
                catAdapter.setStoreId(storeId);
                if (firstCategory.getSecondCategories() != null && !firstCategory.getSecondCategories().isEmpty()) {
                    categoryListView.performItemClick(null, 0, 0);
                }
            } else {
                getActivity().recreate();
            }
            ListView productListView = (ListView) rootView.findViewById(R.id.product_list_view);
            productListView.setAdapter(productAdapter);
            ShoppingCartManager.getInstance().registerListener(mListener);

            return rootView;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ShoppingCartManager.getInstance().unregisterListener(mListener);
        }

        private ModelStatusListener<ShoppingCartEvent, ProductModel> mListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
            @Override
            public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
                catAdapter.notifyDataSetChanged();
            }
        };
    }

    private class SubCategoryAdapter extends FragmentPagerAdapter {
        @Override
        public CharSequence getPageTitle(int position) {
            return "  "
                    + layeredProduct.getCategories().get(position).getName()
                    + "  ";
        }

        public SubCategoryAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SubCategoryFragment fragment = new SubCategoryFragment();
            fragment.setFirstCategory(layeredProduct.getCategories().get(
                    position));
            fragment.setStoreId(storeId);
            fragment.productsFragment = SelectProductsViewController.this;
            return fragment;
        }

        @Override
        public int getCount() {
            return layeredProduct == null ? 0 : layeredProduct
                    .getCategories().size();
        }
    }
}
