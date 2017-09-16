package com.brother.tpp.tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.buyer.manager.StoreDataContainer;
import com.tianpingpai.buyer.ui.ActivityDetailViewController;
import com.tianpingpai.buyer.ui.AddressListViewController;
import com.tianpingpai.buyer.ui.CommonUseViewController;
import com.tianpingpai.buyer.ui.FavoriteStoresViewController;
import com.tianpingpai.buyer.ui.JournalViewController;
import com.tianpingpai.buyer.ui.MainViewController;
import com.tianpingpai.buyer.ui.MessageListViewController;
import com.tianpingpai.buyer.ui.OrdersViewController;
import com.tianpingpai.buyer.ui.ProductDetailViewController;
import com.tianpingpai.buyer.ui.SearchViewController;
import com.tianpingpai.buyer.ui.ShoppingCartViewController;
import com.tianpingpai.buyer.ui.StoreListViewController;
import com.tianpingpai.buyer.ui.StoreViewController;
import com.tianpingpai.buyer.ui.WebViewController;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ContainerActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author lwr
 */
public class ParseHrefUtil {

    static class Link {
        Class<?> target;
        Bundle extras;

        boolean isWebView = false;

        Link(Class<?> target) {
            this.target = target;
        }

        Bundle getExtras() {
            if (extras == null) {
                extras = new Bundle();
            }
            return extras;
        }
    }

    private static HashMap<String, Link> httpRoutesMap = new HashMap<>();
    private static HashMap<String, Link> appRoutesMap = new HashMap<>();

    static {

        /*** Http 跳转路径.*/
        //综合店铺.
        Link multiShopLink = new Link(StoreViewController.class);
        multiShopLink.getExtras().putInt(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_MULTI_WITH_ID);
        httpRoutesMap.put("/m/shop/multishop", multiShopLink);
        //店铺详情.
        Link storeDetail = new Link(StoreViewController.class);
        storeDetail.getExtras().putInt(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_NORMAL);

        /*** App 虚拟路径.*/
        appRoutesMap.put("/dashboard", new Link(MainViewController.class));
        //店铺列表
        Link storeList = new Link(StoreListViewController.class);
        storeList.getExtras().putBoolean(StoreListViewController.KEY_SHOW_BACK_BUTTON,true);
        appRoutesMap.put("/store/list", storeList);
        //个人中心
        Link mine = new Link(MainViewController.class);
        mine.getExtras().putInt(MainViewController.Key_pos, 3);
        appRoutesMap.put("/user", mine);
        //收藏店铺
        appRoutesMap.put("/favorite", new Link(FavoriteStoresViewController.class));
        //店铺详情.
        Link store = new Link(StoreViewController.class);
        store.getExtras().putInt(StoreDataContainer.KEY_STORE_TYPE,
                StoreDataContainer.STORE_TYPE_NORMAL);
        appRoutesMap.put("/store", store);
        //购物车
        appRoutesMap.put("/cart", new Link(ShoppingCartViewController.class));
        //商品详情
        appRoutesMap.put("/product", new Link(ProductDetailViewController.class));
        //地址列表
        appRoutesMap.put("/address", new Link(AddressListViewController.class));
        //搜索
        appRoutesMap.put("/search", new Link(SearchViewController.class));
        //消息列表
        appRoutesMap.put("/message", new Link(MessageListViewController.class));
        //订单列表
        appRoutesMap.put("/order", new Link(OrdersViewController.class));
        //活动详情
        appRoutesMap.put("/activity", new Link(ActivityDetailViewController.class));
        //交易明细
        appRoutesMap.put("/trade", new Link(JournalViewController.class));
        //待支付
        Link notPayedOrders = new Link(OrdersViewController.class);
        notPayedOrders.getExtras().putInt(OrdersViewController.KEY_ORDER_FORM, CommonUtil.NOT_PAY_MONEY);
        appRoutesMap.put("/order/pay", notPayedOrders);
        //待收货
        Link sendOrders = new Link(OrdersViewController.class);
        sendOrders.getExtras().putInt(OrdersViewController.KEY_ORDER_FORM, CommonUtil.NOT_PAY_MONEY);
        appRoutesMap.put("/order/send", sendOrders);
        //常用采购
        appRoutesMap.put("/speedbuy", new Link(CommonUseViewController.class));
        Link multiShop = new Link(StoreViewController.class);
        multiShop.getExtras().putInt(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_MULTI);
        appRoutesMap.put("/colligateShop", multiShop);


        //积分商城入口
//        Link scoreShop = new Link(WebViewController.class);
//        scoreShop.getExtras().putInt("key",2);
//        scoreShop.isWebView = true;
//        httpRoutesMap.put("/app/score/integral/shop",scoreShop);
    }


    public static final String HOST = "tianpingpai";
    private static final String TAG = "ParseHrefUtil";

    public static boolean handleURL(Context c, String href) {
        // href += "?s=234";
        if (TextUtils.isEmpty(href)) {
            return false;
        }
        URI uri;
        try {
            uri = new URI(href);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        String scheme = uri.getScheme();
        int queryIndex = href.indexOf("?");
        ArrayList<Pair<String, String>> params = new ArrayList<>();
        if (queryIndex != -1) {
            String queryStrings = href.substring(queryIndex + 1);
            String[] paramStrings = queryStrings.split("&");

            for (String ps : paramStrings) {
                int index = ps.indexOf("=");
                String key = ps.substring(0, index);
                String value = ps.substring(index + 1);
                Log.e("xx", "===key:" + key + " := " + value);
                params.add(new Pair<>(key, value));
            }
        }

        Intent intent = null;
        outer:
        if ("http".equalsIgnoreCase(scheme)) {
            String host = uri.getHost();
            Log.e("host====",host+"");
            if (TextUtils.isEmpty(host) || !host.contains(HOST)) {// TODO
                break outer;
            }
            String path = uri.getPath();
            Log.e("path===",""+path);
            Link link = httpRoutesMap.get(path);
            if (link != null) {
                intent = new Intent(c, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, link.target);
                if (link.target != null) {
                    intent.putExtras(link.extras);
                }
                if(link.isWebView){
                    intent.putExtra(WebViewController.KEY_URL,href);
                }
            }

        } else if ("fake".equalsIgnoreCase(scheme)) {
            intent = handleAppAction(c, uri.getPath());
            Log.e("xx", "href:" + href);
            Log.e("xx", "path:" + uri.getPath());
            TLog.w(TAG, "124----href=" + href + ",scheme=" + scheme + ",path=	" + uri.getPath());
            if (intent != null) {
                for (Pair<String, String> p : params) {
                    String key = p.first;
                    String value = p.second;
                    intent.putExtra(key, Integer.parseInt(value));
                    Log.e("xx", "param" + key + " = " + value);
                }
            }
        }

        if (intent != null) {
            Link l = httpRoutesMap.get(uri.getPath());
            if(l != null){
                if(l.isWebView){
                    intent.putExtra(WebViewController.KEY_ACTION_BAR_STYLE,WebViewController.ACTION_BAR_STYLE_HIDDEN);
                    c.startActivity(intent);
                    return true;
                }
            }
            for (Pair<String, String> p : params) {
                String key = p.first;
                String value = p.second;
                try {
                    intent.putExtra(key, Integer.parseInt(value));
                    Log.e("xx", key + " = " + value);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            c.startActivity(intent);
            return true;
        }
        return false;
    }

    public static Intent handleAppAction(Context mCtx, String path) {
        Intent intent = new Intent(mCtx, ContainerActivity.class);
        Link target = appRoutesMap.get(path);
        if (target != null) {
            intent.putExtra(ContainerActivity.KEY_CONTENT, target.target);
            if (target.extras != null) {
                intent.putExtras(target.extras);
            }
        } else {
            intent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
        }
        return intent;
    }
}
