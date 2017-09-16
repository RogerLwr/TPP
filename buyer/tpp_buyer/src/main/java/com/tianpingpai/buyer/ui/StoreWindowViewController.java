package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.CategoryAdapter;
import com.tianpingpai.buyer.adapter.ProductAdapter;
import com.tianpingpai.buyer.manager.ShoppingCartEvent;
import com.tianpingpai.buyer.manager.ShoppingCartManager;
import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.model.LayeredProduct;
import com.tianpingpai.buyer.model.LayeredProduct.FirstCategory;
import com.tianpingpai.buyer.model.LayeredProduct.SecondCategory;
import com.tianpingpai.buyer.model.StoreModel.Notification;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.viewController.NotificationViewController;
import com.tianpingpai.widget.BadgeView;

import java.util.ArrayList;

@ActionBar(hidden = true)
@Layout(id = R.layout.ui_sotre_window)
public class StoreWindowViewController extends BaseViewController {

    private String notificationStr;

    public void setStoreFragment(StoreViewController storeFragment) {
        this.storeFragment = storeFragment;
    }

    private StoreViewController storeFragment;

    @Binding(id = R.id.price_sum_text_view)
    private TextView priceSumTextView;
    @Binding(id = R.id.category_num_text_view)
    private BadgeView categoryNumberTextView;
    @Binding(id = R.id.notification_text_view)
    private TextView notificationTextView;
    @Binding(id = R.id.notification_line_view)
    private View notificationLineView;
    @Binding(id = R.id.freight_text_view)
    private TextView freightTextView;
    @Binding(id = R.id.min_amount_text_view)
    private TextView minAmountTextView;
//    @Binding(id = R.id.add_to_cart_button)
//    private TextView addToCartButton;

//    AnimationTool animationTool = new AnimationTool();
//    private ImageView moveImageView;
//    private View toShoppingCartView;

    public void setToShoppingCartView(View toShoppingCartView){
//        this.toShoppingCartView = toShoppingCartView;
    }

    private CategoryAdapter categoryAdapter = new CategoryAdapter();
    private ProductAdapter productAdapter = new ProductAdapter();
    private StoreDataContainer dataContainer;
    ActionSheet as;

    private ModelStatusListener<ShoppingCartEvent, ProductModel> productsListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
        @Override
        public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
            categoryAdapter.notifyDataSetChanged();
            updateNumbers();
        }
    };

    private ModelStatusListener<ModelEvent, LayeredProduct> listener = new ModelStatusListener<ModelEvent, LayeredProduct>() {
        @Override
        public void onModelEvent(ModelEvent event, LayeredProduct model) {
            switch (event) {
                case OnModelGet:
                    categoryAdapter.setLayeredProduct(model);
                    updateNumbers();
                    updateNotifications();
                    if(storeFragment != null){
                        storeFragment.showContent();
                        storeFragment.hideLoading();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void updateNumbers() {
        int sum = 0;
        double totalPrice = 0;
        LayeredProduct layeredProduct = categoryAdapter.getLayeredProduct();
        if (layeredProduct != null) {
            ArrayList<FirstCategory> cats = layeredProduct.getCategories();
            if (cats != null) {
                for (FirstCategory cat : cats) {
                    ArrayList<SecondCategory> secondCats = cat
                            .getSecondCategories();
                    if (secondCats != null) {
                        for (SecondCategory sc : secondCats) {
                            ArrayList<ProductModel> products = sc.getProducts();
                            for (ProductModel pm : products) {
                                if (pm.getProductNum() > 0) {
                                    sum++;
                                    totalPrice += pm.getProductNum()
                                            * pm.getCouponPrice();
                                }
                            }
                        }
                    }
                }
            }
        }

        if(sum > 0){
            categoryNumberTextView.setText(sum + "");
        }else{
            categoryNumberTextView.setText("");
        }

        if (dataContainer.getStoreModel() != null) {
            String price = "￥" + PriceFormat.format(totalPrice);

            String freight = "运费:" + dataContainer.getStoreModel().getFreight() + "元";
            priceSumTextView.setText(price);

            minAmountTextView.setText(freight);

            String text = "￥"
                    + dataContainer.getStoreModel().getStartingPrice() + "起送";
            text += "  " + "满" + dataContainer.getStoreModel().getMinAmount() + "免运费";
            freightTextView.setText(text);
        }
        storeFragment.setBadgeNumber(sum);
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        productAdapter.setActivity(getActivity());
//        productAdapter.setBuyCartView(scaleImageView);
        dataContainer.getProductManager().registerListener(listener);
        ShoppingCartManager.getInstance().registerListener(productsListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ShoppingCartManager.getInstance().unregisterListener(productsListener);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        ListView categoryListView = (ListView) rootView
                .findViewById(R.id.category_list_view);
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(categoryAdapter);
        ListView productListView = (ListView) rootView
                .findViewById(R.id.product_list_view);
        productListView.setAdapter(productAdapter);
        categoryAdapter.setProductAdapter(productAdapter);
        categoryAdapter.setListView(categoryListView);
    }

    public void setDataContainer(StoreDataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.notification_text_view)
    private OnClickListener notificationClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            as = getActionSheet(true);
            WindowManager wm = StoreWindowViewController.this.getActivity().getWindowManager();
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            as.setHeight(dm.heightPixels);// 设置为屏幕高度
            NotificationViewController nvc = new NotificationViewController();
            nvc.setNotificationStr(notificationStr);
            nvc.setActivity(StoreWindowViewController.this.getActivity());
            as.setViewController(nvc);
            as.show();
        }
    };

    @SuppressWarnings("unused")
    @OnClick(R.id.add_to_cart_button)
    private OnClickListener toShoppingCartButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean valid = false;
            if(categoryAdapter.getLayeredProduct() == null){
                return;
            }

            ArrayList<ProductModel> products = categoryAdapter.getLayeredProduct().getProducts();
            for(ProductModel pm:products){
                if(pm.getProductNum() > 0){
                    valid = true;
                    break;
                }
            }
            if(!valid){
                Toast.makeText(ContextProvider.getContext(),"没有选中任何商品!",Toast.LENGTH_LONG).show();
                return;
            }

            ShoppingCartManager.getInstance().addToShoppingCart(
                    categoryAdapter.getLayeredProduct().getProducts());
        }
    };

    private void updateNotifications() {
        StringBuilder sb = new StringBuilder();
        if (dataContainer.getStoreModel() != null) {
            ArrayList<Notification> notifications = dataContainer
                    .getStoreModel().getNotifications();
            if (notifications != null && !notifications.isEmpty()) {
                for (Notification s : notifications) {
                    sb.append(s.getContent());
                    sb.append("                ");
                }
                notificationTextView.setVisibility(View.VISIBLE);
                notificationLineView.setVisibility(View.VISIBLE);
                notificationStr = sb.toString();
                notificationTextView.setText(notificationStr);
            } else {
                notificationTextView.setVisibility(View.GONE);
                notificationLineView.setVisibility(View.GONE);
            }
        } else {
            notificationTextView.setVisibility(View.GONE);
            notificationLineView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResumed(Activity a) {
        super.onActivityResumed(a);
        categoryAdapter.refresh();
        productAdapter.syncWithDb();
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        productAdapter.notifyDataSetChanged();
        if(as != null && productAdapter.as != null){
            if(as.handleBack(a) || productAdapter.as.handleBack(a))
                return true;
        }
        if(as != null){
            return as.handleBack(a);
        }
        if(productAdapter.as != null){
            if(productAdapter.pdvc != null && productAdapter.pdvc.actionSheetMessage != null){
                productAdapter.pdvc.actionSheetMessage.dismiss();
                productAdapter.pdvc.actionSheetMessage = null;
                return true;
            }
            return productAdapter.as.handleBack(a);
        }
        return super.onBackKeyDown(a);
    }
}
