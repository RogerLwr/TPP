package com.tianpingpai.buyer;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tianpingpai.buyer.manager.UserManager;
import com.tianpingpai.buyer.model.URLApi;
import com.tianpingpai.buyer.model.UserModel;
import com.tianpingpai.buyer.ui.GuideViewController;
import com.tianpingpai.buyer.ui.LoginViewController;
import com.tianpingpai.buyer.ui.MainViewController;
import com.tianpingpai.buyer.ui.SelectMarketViewController;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.OnPreDispatchListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.FragmentContainerActivity;
import com.tianpingpai.ui.FragmentContainerActivity.FragmentContainerCallback;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.Settings;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

public class BuyerApplication extends Application {

    private String versionString;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextProvider.init(this);
        ContextProvider.setBaseURL(URLApi.getBaseUrl());
        SDKInitializer.initialize(this);
//        LeakCanary.install(this);
        VolleyDispatcher.getInstance().setOnPreDispatchListener(mOnPreDispatchListener);
//        MarketManager.getInstance().refreshMarkets();//need to update market list
        FragmentContainerActivity.setFragmentContainerCallback(mFragmentContainerCallback);
        MobclickAgent.setDebugMode(true);//TODO
        JPushInterface.init(this);

        String systemVersion = Build.VERSION.RELEASE;
        String appVersion = "unknown";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String uuid = Settings.getInstance().getUUID();
        versionString = String.format("A^%s^B^%s^%s",systemVersion,appVersion,uuid);

        UserManager.getInstance().registerListener(expiredListener);
    }

    private ModelStatusListener<UserEvent, UserModel> expiredListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if(event == UserEvent.LoginExpired){
                Log.e("xx","expired");
                Intent intent = new Intent(ContextProvider.getContext(),ContainerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                ContextProvider.getContext().startActivity(intent);
            }
        }
    };


    private FragmentContainerCallback mFragmentContainerCallback = new FragmentContainerCallback() {
        @Override
        public Class<?> onFirstActivityCreated(
                FragmentContainerActivity a) {

            if(Settings.getInstance().isFirstOpen()){
                return GuideViewController.class;
            }

            if(!UserManager.getInstance().isLoggedIn()){
                return LoginViewController.class;
            }

            if (MarketManager.getInstance().getCurrentMarket() != null) {
                return MainViewController.class;
            } else {
                return SelectMarketViewController.class;
            }
        }
    };

    private OnPreDispatchListener mOnPreDispatchListener = new OnPreDispatchListener() {
        @Override
        public void onPreDispatchRequest(HttpRequest<?> req) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            Log.e("ss", "adding accessToken:" + user);
            if (user != null) {
                req.addParam("accessToken", user.getAccessToken());
            }
            req.addHeader("version",versionString);
        }
    };
}
