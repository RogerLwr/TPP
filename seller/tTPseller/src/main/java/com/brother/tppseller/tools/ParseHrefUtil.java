package com.brother.tppseller.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.seller.ui.AddressListViewController;
import com.tianpingpai.seller.ui.CommentListViewController;
import com.tianpingpai.seller.ui.CouponListViewController;
import com.tianpingpai.seller.ui.MainViewController;
import com.tianpingpai.seller.tools.TLog;
import com.tianpingpai.seller.ui.MessageListViewController;
import com.tianpingpai.seller.ui.OrdersViewController;
import com.tianpingpai.seller.ui.SearchViewController;
import com.tianpingpai.seller.ui.WalletViewController;
import com.tianpingpai.seller.ui.WebViewController;
import com.tianpingpai.ui.ContainerActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
//        //综合店铺.
//        Link multiShopLink = new Link(StoreViewController.class);
//        multiShopLink.getExtras().putInt(StoreDataContainer.KEY_STORE_TYPE, StoreDataContainer.STORE_TYPE_MULTI_WITH_ID);
//        httpRoutesMap.put("/m/shop/multishop", multiShopLink);

        appRoutesMap.put("/dashboard", new Link(MainViewController.class));
        //我的优惠券
        appRoutesMap.put("/coupon", new Link(CouponListViewController.class));
        //订单列表
        appRoutesMap.put("/order", new Link(OrdersViewController.class));
        //商品管理
        //TODO
        Link link = new Link(MainViewController.class);
        appRoutesMap.put("/product", link);
        //地址列表
        appRoutesMap.put("/address", new Link(AddressListViewController.class));
        //TODO 店铺公告
        appRoutesMap.put("/notice", null);
        //所有评价
        appRoutesMap.put("/comment", new Link(CommentListViewController.class));
        //我的钱包
        appRoutesMap.put("/money", new Link(WalletViewController.class));
        //消息列表
        appRoutesMap.put("/message", new Link(MessageListViewController.class));
        //TODO 积分商城 T
        appRoutesMap.put("/mall", null);
        //搜索
        appRoutesMap.put("/search", new Link(SearchViewController.class));

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


//    public static final String HOST = "tianpingpai";
//    private static final String TAG = "ParseHrefUtil";
//
//    public static boolean handleURL(Context c, String href) {
//        // href += "?s=234";
//        if (TextUtils.isEmpty(href)) {
//            return false;
//        }
//        URI uri;
//        try {
//            uri = new URI(href);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return false;
//        }
//        String scheme = uri.getScheme();
//        int queryIndex = href.indexOf("?");
//        ArrayList<NameValuePair> params = new ArrayList<>();
//        if (queryIndex != -1) {
//            String queryStrings = href.substring(queryIndex + 1);
//            String[] paramStrings = queryStrings.split("&");
//
//            for (String ps : paramStrings) {
//                int index = ps.indexOf("=");
//                try {
//                    String key = ps.substring(0, index);
//                    String value = ps.substring(index + 1);
//                    Log.e("xx", "===key:" + key + " := " + value);
//                    params.add(new BasicNameValuePair(key, value));
//                }catch(Exception e){
//
//                }
//            }
//        }
//
//        Intent intent = null;
//        outter:
//        if ("http".equalsIgnoreCase(scheme)) {
//
//            String host = uri.getHost();
//            Log.e("xx", "host:" + host);
//            if (TextUtils.isEmpty(host) || !host.contains(HOST)) {// TODO
//                break outter;
//            }
//            String path = uri.getPath();
//            TLog.w(TAG, "124----href=" + href + ",scheme=" + scheme + ",path=	" + path);
//            if (TextUtils.isEmpty(path)) {
//                break outter;
//            }
//
//        } else if ("fake".equalsIgnoreCase(scheme)) {
//            intent = handleAppAction(c,href, uri.getPath());
//            Log.e("xx", "href:" + href);
//            Log.e("xx", "path:" + uri.getPath());
//            TLog.w(TAG, "124----href=" + href + ",scheme=" + scheme + ",path=	" + uri.getPath());
//            if (intent != null && params != null) {
//                for (NameValuePair p : params) {
//                    String key = p.getName();
//                    String value = p.getValue();
//                    intent.putExtra(key, Integer.parseInt(value));
//                    Log.e("xx", "param" + key + " = " + value);
//                }
//            }
//        }
//
//        if (intent != null) {
//            for (NameValuePair p : params) {
//                String key = p.getName();
//                String value = p.getValue();
//                try {
//                    intent.putExtra(key, Integer.parseInt(value));
//                    Log.e("xx", key + " = " + value);
//                } catch (NumberFormatException e) {
//                    return false;
//                }
//            }
//            c.startActivity(intent);
//            return true;
//        }
//        return false;
//    }
//
    public static Intent handleAppAction(Context mCtx, String href, String path) {
        if (href.equals("fake:://app/close") || href.equals("fake:://app/exit") || href.equals("fake:://exit")) {
            if(mCtx instanceof Activity) {
                ((Activity)mCtx).finish();
            }
            return null;
        } else {
            return handleAppAction(mCtx,path);
        }
    }
}
