package com.tianpingpai.crm;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.ui.LauncherViewController;
import com.tianpingpai.crm.ui.LoginViewController;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.OnPreDispatchListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.MarketManager;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.FragmentContainerActivity;
import com.tianpingpai.ui.FragmentContainerActivity.FragmentContainerCallback;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.Settings;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class CrmApplication extends Application {

	private static ArrayList<Activity> activityArrayList = new ArrayList<>();

	private static CrmApplication instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		ContextProvider.init(this);
		ContextProvider.setBaseURL(URLApi.getBaseUrl());
		SDKInitializer.initialize(this);
		JPushInterface.init(this);
		JPushInterface.setDebugMode(URLApi.debugMode);
		
		MarketManager.getInstance().refreshAllMarkets();//need to update market list
		FragmentContainerActivity.setFragmentContainerCallback(mFragmentContainerCallback);

		String systemVersion = Build.VERSION.RELEASE;
		String appVersion = "unknown";
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		String uuid = Settings.getInstance().getUUID();
		final String versionString = String.format("A^%s^B^%s^%s", systemVersion, appVersion, uuid);
		VolleyDispatcher.getInstance().setOnPreDispatchListener(new OnPreDispatchListener() {
			@Override
			public void onPreDispatchRequest(HttpRequest<?> req) {
				UserModel user = UserManager.getInstance().getCurrentUser();
				if (user != null) {
					req.addParam("accessToken", user.getAccessToken());
				}
//				req.addParam("version",versionString);
				req.addHeader("version",versionString);
			}
		});

		MarketManager.getInstance().getMarkets();

		UserManager.getInstance().registerListener(expiredListener);
	}

	private ModelStatusListener<UserEvent, UserModel> expiredListener = new ModelStatusListener<UserEvent, UserModel>() {
		@Override
		public void onModelEvent(UserEvent event, UserModel model) {
			if(event == UserEvent.LoginExpired){
				finishActivity();
				Log.e("xx", "expired");
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
//			if (Settings.getInstance().isFirstOpen()) {
//				return GuideViewController.class;
//			}
			return LauncherViewController.class;
		}
	};

	public static CrmApplication getInstance(){
		return instance;
	}

	/*
	public static void toLoginActivity(Activity a){
		Intent i = new Intent(a, ContainerActivity.class);
		i.putExtra(ContainerActivity.KEY_CONTENT,
				LoginViewController.class);
		a.startActivity(i);
		a.finish();
	}

	*/

	public static void putActivity(Activity a){
		if(!activityArrayList.contains(a)){
			activityArrayList.add(a);
		}
	}

	public static void removeActivity(Activity a){
		if(activityArrayList.contains(a)){
			activityArrayList.remove(a);
		}
	}

	public static void finishActivity(){
		for (Activity a:activityArrayList){
			if(a!=null){
				a.finish();
			}
		}
		activityArrayList.clear();
	}

}
