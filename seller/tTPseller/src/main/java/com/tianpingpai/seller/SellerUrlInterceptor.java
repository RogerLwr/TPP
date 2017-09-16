package com.tianpingpai.seller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.seller.ui.EnvelopeDetailViewController;
import com.tianpingpai.seller.ui.LoginViewController;
import com.tianpingpai.seller.ui.WalletViewController;
import com.tianpingpai.seller.ui.WebViewController;
import com.tianpingpai.ui.ContainerActivity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SellerUrlInterceptor implements WebViewController.URLInterceptor {

//    public static final String APP_ENVELOPE_DETAIL = "//app/bonus";
//    public static final String APP_WALLET = "//app/wallet";
    public static final String APP_ENVELOPE_DETAIL = "/bonus";
    public static final String APP_WALLET = "/wallet";
    public static final String APP_LOGIN = "://app/login";

    @Override
    public boolean onIntercept(Activity a, String href) {
        Log.e("xx","url:" + href);
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
        ArrayList<Pair<String,String>> params = new ArrayList<>();
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

        if ("http".equalsIgnoreCase(scheme)){
            return false;
        } else if ("fake".equalsIgnoreCase(scheme)) {
            Log.e("xx", "getSchemeSpecificPart:" + uri.getSchemeSpecificPart());
            Intent intent = handleAppAction(a, uri.getPath());
            if(href.equals("fake://app/login")){
                intent = new Intent(a,ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT,LoginViewController.class);
            }
            if(href.equals("fake://app/exit")){
                a.finish();
                return true;
            }
            Log.e("xx", "href:" + href);
            Log.e("xx", "path:" + uri.getPath());
            Log.e("xx", "path:" + uri.getFragment());
            if (intent != null) {
                for (Pair<String,String> p : params) {
                    String key = p.first;
                    String value = p.second;
                    intent.putExtra(key, Integer.parseInt(value));
                    Log.e("xx", "param" + key + " = " + value);
                }
                a.startActivity(intent);
                return true;
            }
        }
        return true;
    }

    public static Intent handleAppAction(Context mCtx, String path) {
        Log.e("Interceptor", "82-------path="+path);
        if(APP_ENVELOPE_DETAIL.equals(path)){
            Intent i = new Intent(mCtx, ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,EnvelopeDetailViewController.class);
            return i;
        }

        if(APP_WALLET.equals(path)){
            Intent i = new Intent(mCtx, ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,WalletViewController.class);
            return i;
        }

        if(APP_LOGIN.equals(path)){
            Intent i = new Intent(mCtx, ContainerActivity.class);
            i.putExtra(ContainerActivity.KEY_CONTENT,LoginViewController.class);
            return i;
        }
        return null;

    }
}
