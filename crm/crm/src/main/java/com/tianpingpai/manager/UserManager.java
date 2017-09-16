package com.tianpingpai.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.crm.CrmApplication;
import com.tianpingpai.crm.tool.DateUtil;
import com.tianpingpai.dao.UserDao;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ErrorListener;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.model.URLApi;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.user.UserModel;
import com.tianpingpai.utils.SingletonFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class UserManager extends ModelManager<UserEvent, UserModel> {
	
	public static UserManager getInstance(){
		return SingletonFactory.getInstance(UserManager.class);
	}

	private ErrorListener mLoginErrorListener = new ErrorListener(){
		@Override
		public void onError(HttpRequest<?> request, HttpError error) {
			notifyEvent(UserEvent.LoginFailed, null);
			Log.e("xx","error:" + error.getErrorMsg());
		}
	};
	
	private ResultListener<ModelResult<UserModel>> loginListener = new ResultListener<ModelResult<UserModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<UserModel>> request,
				ModelResult<UserModel> data) {
			if(data.isSuccess()){
				isExpired = false;
				mCurrentUser = data.getModel();
				mUserDao.save(mCurrentUser);
				Log.e("xx", "accessToken" + data.getModel().getAccessToken());

				if (mCurrentUser != null) {
					registerJPush();
				}
				notifyEvent(UserEvent.Login, data.getModel());

				ContextProvider.getContext().getSharedPreferences("currentUser", 100).edit().putString("phoneNumber",mCurrentUser.getPhone()).commit();
			}else{
				notifyEvent(UserEvent.LoginFailed, null);
				Toast.makeText(ContextProvider.getContext(), data.getDesc() + "", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public UserManager() {
		mUserDao = new UserDao();
		mCurrentUser = mUserDao.getLast();
		HttpManager.getInstance().registerListener(httpListener);
	}
	
	public void login(String username,String password){
		HttpRequest<ModelResult<UserModel>> loginRequest = new HttpRequest<>(URLApi.User.login(),loginListener);
		loginRequest.setMethod(HttpRequest.POST);
		loginRequest.addParam("phone",username);
		loginRequest.addParam("password",password);
		ModelParser<UserModel> parser = new ModelParser<>(UserModel.class);
		loginRequest.setParser(parser);
		loginRequest.setErrorListener(mLoginErrorListener);
		VolleyDispatcher.getInstance().dispatch(loginRequest);
	}

	public void logout(){
		deleteCurrentUser();
		notifyEvent(UserEvent.Logout, null);
	}

	public void loginExpired(Activity a){
//		Toast.makeText(ContextProvider.getContext(),"登录验证已经过期，请重新登录！",Toast.LENGTH_LONG).show();
//		deleteCurrentUser();
//		CrmApplication.toLoginActivity(a);
//		CrmApplication.finishActivity();
	}

	public boolean isExpired;
	ModelStatusListener<HttpEvent, HttpResult<?>> httpListener = new ModelStatusListener<HttpEvent, HttpResult<?>>() {
		@Override
		public void onModelEvent(HttpEvent event, HttpResult<?> model) {
			switch (event) {
				case accessTokenExpired:
					if(isExpired){
						return;
					}
					isExpired = true;
					if (mCurrentUser != null) {
						mUserDao.delete(mCurrentUser);
					}
					notifyEvent(UserEvent.LoginExpired,mCurrentUser);
					mCurrentUser = null;
					break;
			}
		}
	};
	
	public UserModel getCurrentUser(){
		return mCurrentUser;
	}

	public boolean isLoggedIn() {
		return mCurrentUser != null;
	}

	public void deleteCurrentUser() {
		if(mCurrentUser != null){
			mUserDao.delete(mCurrentUser);
		}
		mCurrentUser = null;
	}
	
	private UserModel mCurrentUser;
	private UserDao mUserDao;

	private Handler mHandler = new Handler(Looper.getMainLooper());

	int pushRegistrationCounter = 0;

	private void registerJPush() {

		JPushInterface.init(ContextProvider.getContext());
		if (pushRegistrationCounter > 100) {
			return;
		}

		pushRegistrationCounter++;

		if (mCurrentUser != null) {
			// 极光注册别名 发送 至 极光服务器
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mCurrentUser != null) {
						String phone = mCurrentUser.getPhone();
						String alias = "1_"
								+ phone
								+ DateUtil.DateToString(new Date(),
								DateUtil.LOGIN_DATE_TWO);
						Log.e("xx", "166-------------注册极光前alias=" + alias);
						JPushInterface.resumePush(ContextProvider.getContext());
						JPushInterface.setAliasAndTags(
								ContextProvider.getContext(), alias, null,
								mAliasCallback);
						Log.e("xx", "1677777777-------------注册极光前alias=" + alias);
					}
				}
			}, 1000);

		}
	}

	private TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> arg2) {
			switch (code) {
				case 0:
					// register JPush();
					pushRegistrationCounter = 0;
					Log.e("xx", "133-----------注册成功后alias="+alias);
					registerTerminal(alias);
					break;
				default:
					Log.e("xx", "137---------注册失败,继续注册 alias= "+alias);
					registerJPush();
			}
		}
	};

	private void registerTerminal(String alias) {
		String platform = android.os.Build.MODEL + "_"
				+ android.os.Build.VERSION.SDK_INT + "_"
				+ android.os.Build.VERSION.RELEASE;
//		String url = URLUtil.REGISTERTERMINAL_URL;
		String url = URLApi.getBaseUrl()+"/api/user/registTerminal.json";
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(
				url, registerTerminalListener);//use it's actual type
		req.addParam("accessToken", mCurrentUser.getAccessToken());
		req.addParam("user_type", String.valueOf(3));//3表示CRM用户
		req.addParam("phone", mCurrentUser.getPhone());
		req.addParam("registration_id", "");// TODO
		req.addParam("ptype", "1");// 1表示android机
		try {
			req.addParam("platform", URLEncoder.encode(platform, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		req.addParam("tag", "");
		req.addParam("alias", alias);
		req.addParam("source", "");// APP安装来源
		req.addParam("version", "1.0");// TODO //APP版本号
		req.setParser(new GenericModelParser());
		VolleyDispatcher.getInstance().dispatch(req);
	}

	private ResultListener<ModelResult<Model>> registerTerminalListener = new ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request,
							 ModelResult<Model> data) {
			Log.e("xx", "registered" + data.getCode());

		}
	};

}
