
package com.tianpingpai.seller;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.baidu.mapapi.SDKInitializer;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.OnPreDispatchListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.seller.ui.LoginViewController;
import com.tianpingpai.seller.ui.SplashViewController;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.share.ShareService;
import com.tianpingpai.share.weixin.WeixinConfig;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.FragmentContainerActivity;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.Settings;

import cn.jpush.android.api.JPushInterface;

public class SellerApplication extends Application {

    private String versionString;
    private ModelStatusListener<UserEvent, UserModel> loginExpireListener = new ModelStatusListener<UserEvent, UserModel>() {
        @Override
        public void onModelEvent(UserEvent event, UserModel model) {
            if(event == UserEvent.LoginExpired){
                Intent intent = new Intent(SellerApplication.this, ContainerActivity.class);
                intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        ContextProvider.init(this);
        ContextProvider.setBaseURL(URLApi.getBaseUrl());
        SDKInitializer.initialize(this);
//        LeakCanary.install(this);
        VolleyDispatcher.getInstance().setOnPreDispatchListener(new OnPreDispatchListener() {
            @Override
            public void onPreDispatchRequest(HttpRequest<?> req) {
                UserModel user = UserManager.getInstance().getCurrentUser();
                if (user != null) {
                    req.addParam("accessToken", user.getAccessToken());
                }
            }
        });

        WeixinConfig config = new WeixinConfig();
        config.setAppId("wx51c113c72adac0e2");
        ShareService.getIntance().setWeixinConfig(config);

        FragmentContainerActivity.setFragmentContainerCallback(new FragmentContainerActivity.FragmentContainerCallback() {
            @Override
            public Class<?> onFirstActivityCreated(
                    FragmentContainerActivity a) {
                return SplashViewController.class;
            }
        });
        JPushInterface.setDebugMode(URLApi.IS_DEBUG); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush

        String systemVersion = Build.VERSION.RELEASE;
        String appVersion = "unknown";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String uuid = Settings.getInstance().getUUID();
        versionString = String.format("A^%s^S^%s^%s",systemVersion,appVersion,uuid);
        VolleyDispatcher.getInstance().setOnPreDispatchListener(mOnPreDispatchListener);
        UserManager.getInstance().registerListener(loginExpireListener);
    }

    private OnPreDispatchListener mOnPreDispatchListener = new OnPreDispatchListener() {
        @Override
        public void onPreDispatchRequest(HttpRequest<?> req) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if (user != null) {
                req.addParam("accessToken", user.getAccessToken());
            }
            req.addHeader("version",versionString);
        }
    };
}
