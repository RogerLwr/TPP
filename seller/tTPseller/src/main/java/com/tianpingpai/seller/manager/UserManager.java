package com.tianpingpai.seller.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelManager;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpEvent;
import com.tianpingpai.http.HttpManager;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.HttpRequest.ResultListener;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.seller.dao.UserDao;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.DateFormatter;
import com.tianpingpai.utils.SingletonFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class UserManager extends ModelManager<UserEvent, UserModel> {

	public static UserManager getInstance() {
		return SingletonFactory.getInstance(UserManager.class);
	}

	private UserDao mUserDao = new UserDao();

	public void login(String username, String password) {
		String url = URLApi.LOGIN_URL;
		HttpRequest<ModelResult<UserModel>> loginRequest = new HttpRequest<>(
				url, loginListener);
		loginRequest.setMethod(HttpRequest.POST);
		loginRequest.addParam("user_type", UserModel.USER_TYPE_SELLER + "");
		loginRequest.addParam("phone", username);
		loginRequest.addParam("password", password);
		ModelParser<UserModel> parser = new ModelParser<>(
				UserModel.class);
		loginRequest.setParser(parser);
		loginRequest.setErrorListener(mLoginErrorListener);
		VolleyDispatcher.getInstance().dispatch(loginRequest);
	}

	private ResultListener<ModelResult<UserModel>> loginListener = new ResultListener<ModelResult<UserModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<UserModel>> request,
							 ModelResult<UserModel> data) {
			if (data.isSuccess()) {
				mCurrentUser = data.getModel();
				mUserDao.save(mCurrentUser);
				if (mCurrentUser != null) {
					Log.e("xx", "67------注册极光");
					registerJPush();
				}
				notifyEvent(UserEvent.Login, data.getModel());
			} else {
				notifyEvent(UserEvent.LoginFailed, null);
				Toast.makeText(ContextProvider.getContext(),
						data.getDesc() + "", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void saveUser(UserModel user){
		if(mCurrentUser != null){
			mUserDao.delete(mCurrentUser);
		}
		mUserDao.save(user);
		mCurrentUser = user;
		notifyEvent(UserEvent.UserInfoUpdate, user);
	}

	public void logout() {
		if (mCurrentUser != null) {
			mUserDao.delete(mCurrentUser);
		}
		mCurrentUser = null;
		notifyEvent(UserEvent.Logout, null);
	}

	private UserModel mCurrentUser;
	private ModelStatusListener<HttpEvent, HttpResult<?>> httpListener = new ModelStatusListener<HttpEvent, HttpResult<?>>() {

		@Override
		public void onModelEvent(HttpEvent event, HttpResult<?> model) {
			switch (event) {
			case accessTokenExpired:
				Log.e("xx", "97--------过期转为登出状态");
				if (mCurrentUser != null) {
					mUserDao.delete(mCurrentUser);
				}
				mCurrentUser = null;
				notifyEvent(UserEvent.Logout, null);
				break;
			}
		}
	};

	{
		mCurrentUser = mUserDao.getLast();
		HttpManager.getInstance().registerListener(httpListener);
	}

	public UserModel getCurrentUser() {
		return mCurrentUser;
	}


	private HttpRequest.ErrorListener mLoginErrorListener = new HttpRequest.ErrorListener() {
		@Override
		public void onError(HttpRequest<?> request, HttpError eror) {
			Log.e("xx","failed: time out");
			Toast.makeText(ContextProvider.getContext(), "网络不给力，请稍候再试！",
					Toast.LENGTH_SHORT).show();// TODO
			notifyEvent(UserEvent.LoginFailed, null);
		}
	};

	public boolean isLoggedIn() {
		mCurrentUser = mUserDao.getLast();
		return mCurrentUser != null;
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
				registerJPushOK = true;
				break;
			default:
				Log.e("xx", "137---------注册失败,继续注册 alias= "+alias);
				registerJPush();
			}
		}
	};

	private Handler mHandler = new Handler(Looper.getMainLooper());

	int pushRegistrationCounter = 0;

	boolean registerJPushOK = false;

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
						if(!registerJPushOK){

							String phone = mCurrentUser.getPhone();
							String alias = "0_"
									+ phone
									+ DateFormatter.format(new Date());
							Log.e("xx", "166-------------注册极光前alias="+alias);
							JPushInterface.resumePush(ContextProvider.getContext());
							JPushInterface.setAliasAndTags(
									ContextProvider.getContext(), alias, null,
									mAliasCallback);
//							mHandler.postDelayed(this, 2000);
						}

					}
				}
			}, 1000);
		}
	}

	private ResultListener<ModelResult<String>> registerTerminalListener = new ResultListener<ModelResult<String>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<String>> request,
				ModelResult<String> data) {
			Log.e("xx", "registered" + data.getCode());

		}
	};

	private void registerTerminal(String alias) {
		String platform = android.os.Build.MODEL + "_"
				+ android.os.Build.VERSION.SDK_INT + "_"
				+ android.os.Build.VERSION.RELEASE;
		String url = URLApi.REGISTER_TERMINAL_URL;
		HttpRequest<ModelResult<String>> req = new HttpRequest<>(
				url, registerTerminalListener);//use it's actual type
		req.addParam("accessToken", mCurrentUser.getAccessToken());
		req.addParam("user_type", String.valueOf(UserModel.USER_TYPE_SELLER));
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
}
