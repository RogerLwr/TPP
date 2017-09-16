package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.ShoppingCartAdapter;
import com.tianpingpai.buyer.adapter.ShoppingCartAdapter.OnProductSelectionChangeListener;
import com.tianpingpai.buyer.manager.ShoppingCartEvent;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.parser.ShoppingCartSyncParser;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.NoticeManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.model.SellerModel;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ListResult;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.EmptyView;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserEvent;

import java.util.ArrayList;
import java.util.HashSet;

@SuppressWarnings("unused")
@EmptyView(imageResource = R.drawable.empty_shopping_cart,text = R.string.empty_shopping_cart)
@Statistics(page = "购物车")
@Layout(id = R.layout.ui_shopping_cart)
public class ShoppingCartViewController extends BaseViewController {

    protected static final int REQUEST_CODE_CONFIRM = 200;
    private MainViewController mainFragment;

    @Binding(id = R.id.total_price_online_pay_text_view)
    private TextView totalPriceTextView;
    @Binding(id = R.id.select_button)
    private ImageView selectionButton;
    @Binding(id = R.id.action_button)
    private Button buyButton;
    @Binding(id = R.id.cart_container)
    private View cartContainer;
    @Binding(id = R.id.msg_text_view)
    private TextView msgTextView;

    private TextView deleteButton;
    @Binding(id = R.id.sync_container)
    private View syncContainer;

    private ListView shoppingCartListView;

    public void setMainFragment(MainViewController mf) {
        this.mainFragment = mf;
    }

    public MainViewController getMainFragment(){
        return mainFragment;
    }

    private ShoppingCartAdapter shoppingCartAdapter = new ShoppingCartAdapter();
    private ModelStatusListener<UserEvent, UserModel> userListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            switch (event) {
                case Login:
                    loadData();
                    break;
                case Logout:
                    break;
                default:
                    break;
            }
        }
    };
    private OnProductSelectionChangeListener productSelectionChangeListener = new OnProductSelectionChangeListener() {
        @Override
        public void onProductSelectionChange() {
            updateTotalPrice(false);
        }
    };

    private boolean isStandaloneMode() {
        return mainFragment == null;
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ShoppingCartManager.getInstance().resetShoppingCartStatus();
        shoppingCartAdapter.setShoppingCartViewController(this);

        View actionBarView = setActionBarLayout(R.layout.ab_title_white);
        deleteButton = (TextView) actionBarView.findViewById(R.id.ab_right_button);
        setTitle(R.string.shopping_cart);
        deleteButton.setText(R.string.delete);
        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setTextColor(getActivity().getResources().getColor(R.color.green));
        deleteButton.setOnClickListener(deleteButtonListener);
        Toolbar toolbar = (Toolbar) actionBarView.findViewById(R.id.toolbar);
        if (isStandaloneMode()) {
            toolbar.setNavigationIcon(R.drawable.ic_back_green);
        } else {
            toolbar.setNavigationIcon(null);
        }

        shoppingCartListView = (ListView) rootView
                .findViewById(R.id.shopping_cart_list_view);
        shoppingCartListView.addFooterView(new View(getActivity()),null,true);
        shoppingCartListView.setFooterDividersEnabled(false);
        shoppingCartListView.setAdapter(shoppingCartAdapter);

        shoppingCartAdapter.setActivity(getActivity());
        shoppingCartAdapter
                .setOnProductSelectionChangeListener(productSelectionChangeListener);

        loadData();

        //TODO
        selectionButton.setImageResource(R.drawable.checkbox_checked);
        selectionButton.setTag(true);
        UserManager.getInstance().registerListener(userListener);
        ShoppingCartManager.getInstance().registerListener(
                shoppingCartListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ShoppingCartManager.getInstance().unregisterListener(
                shoppingCartListener);
        UserManager.getInstance().unregisterListener(userListener);
    }

    public int getContentHeight() {
        if (mainFragment != null) {
            return mainFragment.getView().getHeight();
        }
        return getView().getHeight();
    }

    private ResultListener<ListResult<Pair<SellerModel, ArrayList<ProductModel>>>> listener = new ResultListener<ListResult<Pair<SellerModel, ArrayList<ProductModel>>>>() {
        @Override
        public void onResult(
                HttpRequest<ListResult<Pair<SellerModel, ArrayList<ProductModel>>>> request,
                ListResult<Pair<SellerModel, ArrayList<ProductModel>>> data) {
//            ArrayList<ProductModel> products = new ArrayList<>();
//            if (data != null && data.getModels() != null) {
//                for (Pair<SellerModel, ArrayList<ProductModel>> d : data.getModels()) {
//                    products.addAll(d.second);
//                }
//            }

//            hideLoading();
            hideSubmitting();
            if (getActivity() == null || data == null) {
                return;
            }
            if (data.isSuccess()) {
//                if(data.getModels() != null){
//                    if(data.getModels().size() == 1){
//                        shoppingCartListView.setDivider(ContextProvider.getContext().getResources().getDrawable(R.color.white));
//                    }else {
//                        shoppingCartListView.setDivider(ContextProvider.getContext().getResources().getDrawable(R.color.gray_ec));
//                        shoppingCartListView.setDividerHeight(10);
//                    }
//                }
                shoppingCartAdapter.setModels(data.getModels());
                shoppingCartAdapter.selectAll();
                Log.e("xx", "updating");
                updateTotalPrice(false);
            } else {
                if (data.getCode() != HttpResult.CODE_AUTH) {
                    msgTextView.setVisibility(View.VISIBLE);
                    msgTextView.setText(data.getDesc());
                }
            }
        }
    };

    private void loadData() {
        UserModel user = UserManager.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String url = ContextProvider.getBaseURL() + "/api/shoppingCart/list";

        HttpRequest<ListResult<Pair<SellerModel, ArrayList<ProductModel>>>> req = new HttpRequest<>(
                url, listener);
        ShoppingCartSyncParser parser = new ShoppingCartSyncParser();
        req.setParser(parser);
        req.setErrorListener(new CommonErrorHandler(this));
        VolleyDispatcher.getInstance().dispatch(req);
//        showLoading();
        showSubmitting();
    }

    @Override
    protected void onReloadData() {
        super.onReloadData();
        loadData();
    }

    //TODO
    private OnClickListener deleteButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if(shoppingCartAdapter.getSelectedProducts().size() == 0){
                return;
            }

            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setActionSheet(getActionSheet(true));
            dialog.setTitle("确定删除？");
            dialog.setPositiveButtonListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashSet<ProductModel> selection = shoppingCartAdapter
                            .getSelectedProducts();
                    ShoppingCartManager.getInstance().delete(selection);
//                    ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> ls = ShoppingCartManager
//                            .getInstance().getShoppingCartItems();
//                    shoppingCartAdapter.clear();
//                    shoppingCartAdapter.setModels(ls);
//                    updateTotalPrice(false);
//                    NoticeManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,new Model());
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    @SuppressWarnings("unused")
    @OnClick(R.id.action_button)
    private OnClickListener actionButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }
            HashSet<ProductModel> products = shoppingCartAdapter
                    .getSelectedProducts();
            Log.e("products==",products.size()+"");
            if (products.isEmpty()) {
                Toast.makeText(ContextProvider.getContext(), "没有选中商品！", Toast.LENGTH_SHORT).show();
                return;
            }

            //ky
            ArrayList<Model> productList = new ArrayList<>();
            for (ProductModel p : products){
                Model product = new Model();
                product.set("prod_id", p.getId());
                product.set("num", p.getProductNum());
                productList.add(product);
            }
            String url = URLUtil.GENERATE_CHECK_SHOP_CART_URL;
            String productParams = Model.valueString(productList);
            HttpRequest<ModelResult> req = new HttpRequest<>(url,checkShoppingCartListener);
            req.setMethod(HttpRequest.POST);
            req.addParam("prods", productParams);
            req.addParam("accessToken", user.getAccessToken());
            req.setParser(new GenericModelParser());
            req.setErrorListener(new CommonErrorHandler(ShoppingCartViewController.this));
            VolleyDispatcher.getInstance().dispatch(req);
            showSubmitting();
        }
    };

    private ResultListener<ModelResult> checkShoppingCartListener = new ResultListener<ModelResult>() {
        @Override
        public void onResult(HttpRequest<ModelResult> request, ModelResult data) {
            hideSubmitting();
            if(data.isSuccess()){
                HashSet<ProductModel> products = shoppingCartAdapter
                        .getSelectedProducts();
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,
                        ConfirmOrderViewController.class);
                for (ProductModel pm : products) {
                    pm.setCartStatus(ProductModel.STATUS_IN_ORDER);
                }

                ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> sellers = shoppingCartAdapter.getSelectionStores();
                ShoppingCartManager.getInstance().setInOrderItems(sellers);

                Bundle b = new Bundle();
                for (Pair<SellerModel, ArrayList<ProductModel>> sm : sellers) {
                    double sum = 0;
                    for (ProductModel pm : sm.second) {
                        sum += pm.getCouponPrice() * pm.getProductNum();
                    }
                    if (sm.first.getMinAmount() > sum) {
                        b.putDouble(sm.first.getId() + "", sm.first.getFreight());
                    }else{
                        b.putDouble(sm.first.getId() + "", 0);
                    }
                }
                intent.putExtra(ConfirmOrderViewController.KEY_FREIGHT,
                        b);
                getActivity().startActivityForResult(intent, REQUEST_CODE_CONFIRM);
            }else{
                final ActionSheetDialog dialog = new ActionSheetDialog();
                dialog.setActionSheet(new ActionSheet());
                dialog.setActivity(getActivity());
                dialog.setTitle(data.getDesc());
                dialog.setMessage("购物车商品数量超出限购数量");
                dialog.setCancelButtonHidden(true);
                dialog.setPositiveButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    };

    private void updateTotalPrice(@SuppressWarnings("unused") boolean loading) {
        if (shoppingCartAdapter.getCount() == 0) {
            showEmptyView();
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            hideEmptyView();
            deleteButton.setVisibility(View.VISIBLE);
            ArrayList<Pair<SellerModel, ArrayList<ProductModel>>> sellers = shoppingCartAdapter.getSelectionStores();
            double total = 0;
            for (Pair<SellerModel, ArrayList<ProductModel>> sm : sellers) {
                ArrayList<ProductModel> products = sm.second;
                double sum = 0;
                for (ProductModel pm : products) {
                    sum += pm.getCouponPrice() * pm.getProductNum();
                }
                if (sm.first.getMinAmount() > sum) {
                    sum += sm.first.getFreight();
                }
                total += sum;
            }
            String totalPrice = "￥" + PriceFormat.format(total)+"元";
            totalPriceTextView.setText(totalPrice);
            if (shoppingCartAdapter.isAllValid()) {
                buyButton.setEnabled(true);
            } else {
                buyButton.setEnabled(false);
            }
            //ky
            if(shoppingCartAdapter.isCheckOutSeller()){
                buyButton.setEnabled(true);
            }else{
                buyButton.setEnabled(false);
//                Toast.makeText(getActivity(),"有店铺没达到起送金额!",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShoppingCartManager.getInstance().resetShoppingCartStatus();
        Log.e("resume","resetShoppingCartStatus");
        loadData();
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        ShoppingCartManager.getInstance().resetShoppingCartStatus();
        Log.e("onActivityResumed", "resetShoppingCartStatus");
//        loadData();
    }

    private ModelStatusListener<ShoppingCartEvent, ProductModel> shoppingCartListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
        @Override
        public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
            if(event == ShoppingCartEvent.OnSync){
                showSync();
                return;
            }
            if(event == ShoppingCartEvent.OnFailure){
                loadData();
                hideSync();
                //TODO
                return;
            }
            if(event == ShoppingCartEvent.OnSuccess){
                loadData();
                shoppingCartAdapter.notifyDataSetChanged();
                hideSync();
                return;
            }
            if(event != ShoppingCartEvent.OnProductNumberChange) {
                loadData();
                shoppingCartAdapter.notifyDataSetChanged();
            }
            if(event == ShoppingCartEvent.OnDeleteSuccess){
                loadData();
                hideSync();
            }
            updateTotalPrice(false);
        }
    };

    @OnClick(R.id.select_button_container)
    private OnClickListener selectionButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean b = (Boolean) v.getTag();
            if (b != null && b) {
                selectionButton.setImageResource(R.drawable.checkbox_checked);
                shoppingCartAdapter.selectAll();
                v.setTag(false);
            }else{
                selectionButton.setImageResource(R.drawable.checkbox_unchecked);
                shoppingCartAdapter.removeAll();
                v.setTag(true);
            }
        }
    };

    private void showSync(){
        handler.postDelayed(showSyncRun,300);
    }

    private void hideSync(){
        handler.removeCallbacks(showSyncRun);
        syncContainer.setVisibility(View.INVISIBLE);
    }

    private Handler handler = new Handler();

    private Runnable showSyncRun = new Runnable() {
        @Override
        public void run() {
            syncContainer.setVisibility(View.VISIBLE);
        }
    };
}
