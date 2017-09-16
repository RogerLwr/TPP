package com.tianpingpai.seller.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brother.tppseller.tools.ParseHrefUtil;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.ProductModel;
import com.tianpingpai.parser.JSONModelMapper;
import com.tianpingpai.seller.R;
import com.tianpingpai.seller.SellerUrlInterceptor;
import com.tianpingpai.seller.manager.ShoppingCartEvent;
import com.tianpingpai.seller.manager.ShoppingCartManager;
import com.tianpingpai.seller.model.ShopRemark;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.ui.js.JSActionBar;
import com.tianpingpai.web.WVJBWebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;

@Statistics(page = "网页")
@Layout(id = R.layout.fragment_web)
public class WebViewController extends BaseViewController {

    private ModelStatusListener<ShoppingCartEvent, ProductModel> shoppingCartListener = new ModelStatusListener<ShoppingCartEvent, ProductModel>() {
        @Override
        public void onModelEvent(ShoppingCartEvent event, ProductModel model) {
            if(event == ShoppingCartEvent.OnNewOrderCreated){
                getActivity().finish();
            }
        }
    };

    public interface URLInterceptor {
        boolean onIntercept(Activity a, String url);
    }

    private URLInterceptor interceptor;
    public static final String KEY_URL = "key.Url";
    public static final String KEY_URL_INTERCEPTOR = "key.UrlInterceptor";
    public static final String KEY_CAN_GO_BACK = "key.canGoBack";
    public static final String KEY_ACTION_BAR_STYLE = "key.actionBarStyle";

    public static final int ACTION_BAR_STYLE_NORMAL = 1;
    public static final int ACTION_BAR_STYLE_FLOAT = 2;
    public static final int ACTION_BAR_STYLE_HIDDEN = 3;

    private boolean canGoBack = true;
    private WebView webView;

    private String url;
    private int actionbarStyle = ACTION_BAR_STYLE_NORMAL;

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        url = a.getIntent().getStringExtra(KEY_URL);
        canGoBack = a.getIntent().getBooleanExtra(KEY_CAN_GO_BACK, true);
        actionbarStyle = a.getIntent().getIntExtra(KEY_ACTION_BAR_STYLE, ACTION_BAR_STYLE_NORMAL);
        Class<? extends URLInterceptor> interceptorClass = (Class<? extends URLInterceptor>) a.getIntent().getSerializableExtra(KEY_URL_INTERCEPTOR);
        if (interceptorClass == null) {
            interceptorClass = SellerUrlInterceptor.class;
        }
        try {
            interceptor = interceptorClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        ShoppingCartManager.getInstance().registerListener(shoppingCartListener);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        webView = (WebView) rootView.findViewById(R.id.web_view);
        webView.loadUrl(url);
        Log.e("xx", "66--------url=" + url);
        webView.getSettings().setJavaScriptEnabled(true);
        showLoading();

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    hideLoading();
                    String title = webView.getTitle();
                    if(title != null && "Webpage not available".equalsIgnoreCase(title)){
                        showActionBar();
                        setTitle("网页无法加载");
                    }else{
                        showContent();
                        setTitle(view.getTitle());
                    }
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				return ParseHrefUtil.handleURL(getActivity(), url);
                if (interceptor != null) {
                    Log.e("xx", "86--------url=" + url);
                    boolean r = interceptor.onIntercept(getActivity(), url);
                    Log.e("xx", "r:" + r);
                    if (r) {
                        getActivity().finish();
                    }
                    return r;
                }
                view.goBack();
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("xx","error:");
                showActionBar();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.e("xx", "error:");
                showActionBar();
            }

        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.e("xx", "back");
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        if (canGoBack) {
                            webView.goBack();
                            return true;    //已处理
                        } else {
                            getActivity().finish();
                        }
                    }
                }
                return false;
            }
        });

        setActionBarLayout(R.layout.ab_web);

        JSActionBar actionBar = new JSActionBar();
        actionBar.setViewController(this);
        actionBar.setWebView(webView);

        if (actionbarStyle == ACTION_BAR_STYLE_FLOAT || actionbarStyle == ACTION_BAR_STYLE_HIDDEN) {
            hideActionBar();
        }
        MyWebViewClient webViewClient = new MyWebViewClient(webView);
        webViewClient.enableLogging();
        webView.setWebViewClient(webViewClient);
        webViewClient.registerHandler("orderConfirm", orderConfirmHandler);
        webViewClient.registerHandler("exit", exitHandler);
        webViewClient.registerHandler("payMoney", payHandler);
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
    }


    protected View.OnClickListener getBackButtonListener() {
        return backButtonListener;
    }

    private View.OnClickListener backButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(webView.canGoBack()) {
                webView.goBack();
            }else {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        ShoppingCartManager.getInstance().unregisterListener(shoppingCartListener);
    }

    private WVJBWebViewClient.WVJBHandler exitHandler = new WVJBWebViewClient.WVJBHandler() {
        @Override
        public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
            getActivity().finish();
        }
    };

    private WVJBWebViewClient.WVJBHandler payHandler = new WVJBWebViewClient.WVJBHandler() {
        @Override
        public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
            Log.e("xx", "data" + data);
            JSONObject obj = (JSONObject) data;
//            Log.e("xx", "data" + obj.optString("order_id"));
            try {
                String orderId = obj.getString("order_id");
                int orderTypeGroup = 0;
                String orderTypeString = obj.getString("order_type");
                if( !TextUtils.isEmpty(orderTypeString) ){
                    orderTypeGroup = Integer.parseInt(orderTypeString);
                }
                int payType = 0;
                if( !TextUtils.isEmpty(obj.getString("pay_type"))){
                    payType = Integer.parseInt(obj.getString("pay_type"));
                }
                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, SelectPaymentViewController.class);
                intent.putExtra(SelectPaymentViewController.KEY_ORDER_ID, orderId);
                intent.putExtra(SelectPaymentViewController.KEY_PAY_TYPE, payType);
                intent.putExtra(SelectPaymentViewController.KEY_ORDER_TYPE_GROUP, orderTypeGroup);
                getActivity().startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private WVJBWebViewClient.WVJBHandler orderConfirmHandler = new WVJBWebViewClient.WVJBHandler() {
        @Override
        public void request(Object data, WVJBWebViewClient.WVJBResponseCallback callback) {
            if (data != null) {
                ShoppingCartManager.getInstance().clearShoppingCart();
                Model model = new Model();
                JSONModelMapper.mapObject((JSONObject) data, model);
                Log.e("xx", "255-----------" + data.toString());
                Log.e("xx", "256--------------" + model.toString());

                Bundle b = new Bundle();
                String orderTypeStr = model.getString("order_type");
                int orderTypeGroup = 0;
                int groupId = 0;
                int payType = 0;
                if( !TextUtils.isEmpty(model.getString("pay_type"))){
                    payType = Integer.parseInt(model.getString("pay_type"));
                }
                if( !TextUtils.isEmpty(orderTypeStr) ){
                    orderTypeGroup = Integer.parseInt(orderTypeStr);
                    groupId = Integer.parseInt( model.getString("group_id") );
                }
                List<Model> shopModels = model.getList("shops", Model.class);
                HashSet<ProductModel> products = new HashSet<>();

                for (Model shopModel : shopModels) {
                    List<Model> prodModels = shopModel.getList("products", Model.class);
//                    double sum = 0;

                    int shopId = Integer.parseInt(shopModel.getString("shopId"));
                    for (Model m : prodModels) {
                        //TODO
//                        sum += Double.parseDouble(m.getString("price")) * m.getInt("num");
                        ProductModel productModel = new ProductModel();
                        productModel.setId(Long.parseLong(m.getString("id")));
                        productModel.setUnit(m.getString("unit"));
                        String numString = m.getString("num");
                        int num = m.getInt("num");
                        if(numString != null){
                            num = Integer.parseInt(numString);
                        }
                        productModel.setProductNum(num);
                        productModel.setCouponPrice(Double.parseDouble(m.getString("price")));
                        productModel.setRemark(m.getString("remark"));
                        productModel.setCategoryId(Integer.parseInt(m.getString("categoryId")));
                        productModel.setName(m.getString("name"));
                        productModel.setSellerId(shopId);
                        productModel.setSellerName(shopModel.getString("shopName"));
                        productModel.setCartStatus(ProductModel.STATUS_IN_ORDER_IN_TIME);
                        products.add(productModel);
                    }
//                    if (Integer.parseInt(shopModel.getString("minAmount")) > sum) {
//					total += sm.first.getFreight();
                    double freight = 0;
                    try{
                        if(shopModel.getDouble("freight") > 0){
                            freight = shopModel.getDouble("freight");
                        }else {
                            freight = Double.parseDouble(shopModel.getString("freight"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    String remark = shopModel.getString("remark");
                    ShoppingCartManager.getInstance().clearRemarks();
                    if(!TextUtils.isEmpty(remark)){
                        ShopRemark shopRemark = new ShopRemark();
                        shopRemark.setRemark(remark);
                        shopRemark.setSellerId(shopId);
                        ShoppingCartManager.getInstance().saveRemark(shopRemark);
                    }

                    b.putDouble(shopModel.getString("shopId"), freight);
                }

                ShoppingCartManager.getInstance().setInTimeOrders(products);

                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,
                        ConfirmOrderViewController.class);
                intent.putExtra(ConfirmOrderViewController.KEY_FREIGHT,
                        b);
                intent.putExtra(ConfirmOrderViewController.KEY_PAY_TYPE, payType);
                intent.putExtra(ConfirmOrderViewController.KEY_ORDER_TYPE, ConfirmOrderViewController.ORDER_TYPE_IN_TIME);
                if(orderTypeGroup == 1){ //表示团购
                    intent.putExtra(ConfirmOrderViewController.KEY_ORDER_TYPE_GROUP, orderTypeGroup);
                    intent.putExtra(ConfirmOrderViewController.KEY_GROUP_ID, groupId);
                }
                getActivity().startActivity(intent);
            }
            callback.callback("1");
        }
    };

    @Override
    public boolean onBackKeyDown(Activity a) {
        return super.onBackKeyDown(a);
    }

    class MyWebViewClient extends WVJBWebViewClient {
        public MyWebViewClient(WebView webView) {
            // support js send
            super(webView, new WVJBWebViewClient.WVJBHandler() {
                @Override
                public void request(Object data, WVJBResponseCallback callback) {
                    Toast.makeText(ContextProvider.getContext(), "ObjC Received message from JS:" + data, Toast.LENGTH_LONG).show();
                    callback.callback("Response for message from ObjC!");
                }
            });
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setTitle(view.getTitle());
            hideLoading();
            showContent();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return ParseHrefUtil.handleURL(getActivity(), url) || super.shouldOverrideUrlLoading(view, url);
        }
    }
}
