package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.StoreModel;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.ViewControllerAdapter;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;

@Statistics(page = "店铺主页")
@ActionBar(layout = R.layout.ab_store)
@EmptyView(imageResource = R.drawable.empty_store, text = R.string.empty_store)
@Layout(id = R.layout.ui_store)
public class StoreViewController extends BaseViewController {

    private StoreWindowViewController storeWindowFragment = new StoreWindowViewController();
    private StoreDetailViewController detailFragment = new StoreDetailViewController();
    private StoreCommonUseViewController commonUseFragment1511 = new StoreCommonUseViewController();

    private StoreDataContainer dataContainer = new StoreDataContainer();
    private BaseViewController currentViewController = storeWindowFragment;

    private boolean isFavorite;


    private View emptyViewContainer;
    @Binding(id = R.id.search_button)
    private View searchButton;
    @Binding(id = R.id.shopping_cart_view)
    private View shoppingCartButton;
    @Binding(id = R.id.fav_button)
    private ImageView favButton;
    @Binding(id = R.id.cart_badge_view)
    private BadgeView cartBadgeView;
    private View notValidatedView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ModelStatusListener<ModelEvent, StoreModel> listener = new ModelStatusListener<ModelEvent, StoreModel>() {
        @Override
        public void onModelEvent(ModelEvent event, StoreModel model) {
            switch (event) {
                case OnModelGetFailed:
                    hideLoading();
                    showEmptyView();
                    viewPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    emptyViewContainer.setVisibility(View.VISIBLE);
                    break;
                case OnNetworkError:
                    hideLoading();
                    break;
                case OnModelGet:
                    hideEmptyView();
                    if (dataContainer.getLayeredProduct() == null) {
                        emptyViewContainer.setVisibility(View.VISIBLE);
                    } else {
                        searchButton.setVisibility(View.VISIBLE);
                    }
                    loadFavInfo();
                    setTitle(dataContainer.getStoreModel().getName());
				    showContent();
                    break;
                default:
                    break;
            }
//            hideLoading();
        }
    };

    @Override
    protected void onReloadData() {
        super.onReloadData();
        dataContainer.loadData();
        showLoading();
    }

    private OnClickListener searchButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(dataContainer.getLayeredProduct() == null){
                return;
            }
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT, SearchViewController.class);
            int storeId = dataContainer.getStoreId();
            Log.e("xx", "85-------storeId=" + storeId);
            intent.putExtra(SearchViewController.KEY_STORE_ID, storeId);
            intent.putExtra(SearchViewController.KEY_IN_APP_SEARCH, true);
            SearchViewController.setSearchList(dataContainer.getLayeredProduct().getProducts());
            getActivity().startActivity(intent);
        }
    };

    private ModelStatusListener<Integer, ModelResult<?>> validationListener = new ModelStatusListener<Integer, ModelResult<?>>() {
        @Override
        public void onModelEvent(Integer event, ModelResult<?> model) {
            hideLoading();
            Log.e("xx", "not validated");
            notValidatedView.setVisibility(View.VISIBLE);
            showContent();
        }
    };

    private void loadFavInfo() {
        String url = URLUtil.SHOP_IS_FAVORITE;
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ModelResult<Boolean>> req = new HttpRequest<>(
                url, favListener);
        if (!UserManager.getInstance().isLoggedIn()) {
            return;
        }

        if (user != null) {
            req.addParam("accessToken", user.getAccessToken());
        }

        if (dataContainer.isMultiShop() || dataContainer.isMultiShopWithId()) {
            req.addParam("shop_type", "2");// TODO
        } else {
            req.addParam("shop_type", "1");
        }

        ModelParser<Boolean> parser = new ModelParser<>(Boolean.class);
        req.setParser(parser);

        req.addParam("shop_id", dataContainer.getStoreId() + "");
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private View.OnClickListener contactButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + ContextProvider.getContext().getString(R.string.service_line)));
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        dataContainer.parseParams(a.getIntent());
        dataContainer.setStoreCommonUseViewController(commonUseFragment1511);
        commonUseFragment1511.parseParams(a.getIntent());
    }

    public void setBadgeNumber(int number){
        cartBadgeView.setBadge(number);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        cartBadgeView.setStyle(BadgeView.STYLE_FILL);
        searchButton.setOnClickListener(searchButtonListener);
        View shoppingCartView = rootView.findViewById(R.id.shopping_cart_view);
        storeWindowFragment.setToShoppingCartView(shoppingCartView);
        commonUseFragment1511.setToShoppingCartView(shoppingCartView);
        setTitle(dataContainer.getStoreName());
        notValidatedView = rootView.findViewById(R.id.not_validated_container);
        notValidatedView.setVisibility(View.INVISIBLE);
        notValidatedView.setOnClickListener(reloadButtonListener);
        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        ViewControllerAdapter adapter = new ViewControllerAdapter();
        ArrayList<BaseViewController> viewControllers = new ArrayList<>();
        storeWindowFragment.setDataContainer(dataContainer);
        storeWindowFragment.setStoreFragment(this);
        detailFragment.setDataContainer(dataContainer);
        commonUseFragment1511.setDataContainer(dataContainer);
        viewControllers.add(storeWindowFragment);
        viewControllers.add(detailFragment);
        viewControllers.add(commonUseFragment1511);
        adapter.setViewControllers(viewControllers);
        for (BaseViewController vc : viewControllers) {
            vc.setActivity(getActivity());
        }
        adapter.setTitles(new String[]{"菜单", "店铺信息", "常用菜品"});

        viewPager.setAdapter(adapter);
        emptyViewContainer = rootView.findViewById(R.id.empty_container);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        dataContainer.getStoreManager().registerListener(listener);
        dataContainer.getNotValidatedManager().registerListener(validationListener);
        dataContainer.loadData();

        viewPager.removeOnPageChangeListener(pageChangeListener);
        viewPager.addOnPageChangeListener(pageChangeListener);

        TextView serviceLineTextView = (TextView) rootView.findViewById(R.id.service_line_text_view);
        serviceLineTextView.setText(Html.fromHtml("<u>" + ContextProvider.getContext().getString(R.string.service_line) + "<u>"));
        serviceLineTextView.setOnClickListener(contactButtonListener);
        showLoading();
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0 || position == 2) {
                searchButton.setVisibility(View.VISIBLE);
                favButton.setVisibility(View.GONE);
                shoppingCartButton.setVisibility(View.VISIBLE);
            } else {
                shoppingCartButton.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
                favButton.setVisibility(View.VISIBLE);
            }
            if (position == 0) {
                currentViewController = storeWindowFragment;
            } else if (position == 1) {
                currentViewController = detailFragment;
            } else {
                currentViewController = commonUseFragment1511;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataContainer.getNotValidatedManager().unregisterListener(validationListener);
        storeWindowFragment.destroyView();
        detailFragment.destroyView();
        commonUseFragment1511.destroyView();
    }

    private ResultListener<ModelResult<Boolean>> favListener = new ResultListener<ModelResult<Boolean>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Boolean>> request,
                             ModelResult<Boolean> data) {
            if (data != null) {
                Log.w("xx", "200-------------data.getCode()=" + data.getCode());
                if (data.getCode() == 1) {
                    Toast.makeText(getActivity(), "您还未登录", Toast.LENGTH_SHORT).show();
                }
                if (data.getModel() != null) {
                    isFavorite = data.getModel();
                }
            }
            updateFavButton();
        }
    };

    private ResultListener<ModelResult<Boolean>> addFavoriteListener = new ResultListener<ModelResult<Boolean>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Boolean>> request,
                             ModelResult<Boolean> data) {
            Boolean isFav = request.getAttachment(Boolean.class);
            if (isFav) {
                if (data.isSuccess()) {
                    Toast.makeText(ContextProvider.getContext(), "收藏成功！", Toast.LENGTH_SHORT)
                            .show();
                    isFavorite = true;
                }
            } else {
                if (data.isSuccess()) {
                    isFavorite = false;
                    Toast.makeText(ContextProvider.getContext(), "取消收藏成功！", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            updateFavButton();
        }
    };

    private void updateFavButton() {
        if (isFavorite) {
            favButton.setImageResource(R.drawable.ab_ic_favourite);
        } else {
            favButton.setImageResource(R.drawable.ab_ic_un_favourite);
        }
    }

    @OnClick(R.id.fav_button)
    private OnClickListener favButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isFavorite) {
                fav(false);
            } else {
                fav(true);
            }
        }
    };

    private void fav(boolean fav) {
        String url = fav ? URLUtil.ADD_FAVORITE_SALER_URL
                : URLUtil.DEL_FAVORITE_SALER_URL;
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ModelResult<Boolean>> req = new HttpRequest<>(
                url, addFavoriteListener);
        if (user != null) {
            req.addParam("accessToken", user.getAccessToken());
        }
        req.setMethod(HttpRequest.POST);
        if (dataContainer.isMultiShop() || dataContainer.isMultiShopWithId()) {
            req.addParam("type", "2");// TODO use constant
        } else {
            req.addParam("type", "1");
        }

        ModelParser<Boolean> parser = new ModelParser<>(Boolean.class);
        req.setParser(parser);
        req.setAttachment(fav);
        req.setErrorListener(new CommonErrorHandler(this));
        req.addParam("sale_user_id", dataContainer.getStoreId() + "");
        VolleyDispatcher.getInstance().dispatch(req);
    }

    private View.OnClickListener reloadButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataContainer.loadData();
            notValidatedView.setVisibility(View.INVISIBLE);
        }
    };


    @OnClick(R.id.shopping_cart_view)
    private View.OnClickListener goShoppingCartListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(getActivity(),
                    ContainerActivity.class);
            intent.putExtra(ContainerActivity.KEY_CONTENT,
                    ShoppingCartViewController.class);
            intent.putExtra("position", 2);
            intent.putExtra("is_shop_to_car", CommonUtil.SHOP_TO_CAR);
            getActivity().startActivity(intent);
        }
    };

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        storeWindowFragment.onActivityResumed(a);
    }

    @Override
    public void onActivityPaused(Activity a) {
        super.onActivityPaused(a);
        storeWindowFragment.onActivityPaused(a);
    }


    @Override
    public boolean onBackKeyDown(Activity a) {
        return currentViewController.onBackKeyDown(a);
    }
}
