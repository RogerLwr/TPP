package com.tianpingpai.buyer.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.brother.tpp.net.URLUtil;
import com.brother.tpp.tools.DateUtil;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.buyer.db.UserDao;
import com.tianpingpai.buyer.model.UserModel;
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
import com.tianpingpai.user.UserEvent;
import com.tianpingpai.utils.Settings;
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
		HttpRequest<ModelResult<UserModel>> loginRequest = new HttpRequest<>(
				URLUtil.LOGIN_URL, loginListener);
		loginRequest.setMethod(HttpRequest.POST);
		loginRequest.addParam("user_type", UserModel.USER_TYPE_BUYER + "");
		loginRequest.addParam("phone", username);
		loginRequest.addParam("password", password);
		ModelParser<UserModel> parser = new ModelParser<>(
				UserModel.class);
		loginRequest.setParser(parser);
		loginRequest.setErrorListener(mLoginErrorListener);
		VolleyDispatcher.getInstance().dispatch(loginRequest);
	}
	
	public void saveUser(UserModel user){
		if(mCurrentUser != null){
			mUserDao.delete(mCurrentUser);
		}
		mUserDao.save(user);
		mCurrentUser = user;
		notifyEvent(UserEvent.UserInfoUpdate, user);
	}

	public void logout() {
		isExpired = true;
		if (mCurrentUser != null) {
			mUserDao.delete(mCurrentUser);
		}
		mCurrentUser = null;
		notifyEvent(UserEvent.Logout, null);
	}

	private UserModel mCurrentUser;
	private boolean isExpired = false;

	private ModelStatusListener<HttpEvent, HttpResult<?>> httpListener = new ModelStatusListener<HttpEvent, HttpResult<?>>() {

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

	{
		mCurrentUser = mUserDao.getLast();
		HttpManager.getInstance().registerListener(httpListener);
	}

	public UserModel getCurrentUser() {
		return mCurrentUser;
	}

	private ResultListener<ModelResult<UserModel>> loginListener = new ResultListener<ModelResult<UserModel>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<UserModel>> request,
				ModelResult<UserModel> data) {
			if (data.isSuccess()) {
				isExpired = false;
				Log.e("xx", "user:" + data.getModel());
				mCurrentUser = data.getModel();
				mUserDao.save(mCurrentUser);
				// 登录 成功 后 设置极光的别名
				if (mCurrentUser != null) {
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
	private HttpRequest.ErrorListener mLoginErrorListener = new HttpRequest.ErrorListener() {
		@Override
		public void onError(HttpRequest<?> request, HttpError eror) {
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
				TLog.w("xx", "133-----------注册成功后alias="+alias);
				registerTerminal(alias);
				break;
			default:
				TLog.w("xx", "137---------注册失败,继续注册 alias= "+alias);
				registerJPush();
			}
		}
	};

	private Handler mHandler = new Handler(Looper.getMainLooper());

	int pushRegistrationCounter = 0;

	private void registerJPush() {
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
						TLog.w("xx", "166-------------注册极光前alias="+alias);
						JPushInterface.resumePush(ContextProvider.getContext());
						JPushInterface.setAliasAndTags(
								ContextProvider.getContext(), alias, null,
								mAliasCallback);
					}
				}
			}, 1000);

		}
	}

	private ResultListener<ModelResult<Model>> registerTerminalListener = new ResultListener<ModelResult<Model>>() {
		@Override
		public void onResult(HttpRequest<ModelResult<Model>> request,
				ModelResult<Model> data) {
			Log.e("xx", "registered" + data.getCode());

		}
	};

	private void registerTerminal(String alias) {
		String platform = android.os.Build.MODEL + "_"
				+ android.os.Build.VERSION.SDK_INT + "_"
				+ android.os.Build.VERSION.RELEASE;
		String url = URLUtil.REGISTERTERMINAL_URL;
		HttpRequest<ModelResult<Model>> req = new HttpRequest<>(
				url, registerTerminalListener);//use it's actual type
		req.addParam("accessToken", mCurrentUser.getAccessToken());
		req.addParam("user_type", String.valueOf(UserModel.USER_TYPE_BUYER));
		req.addParam("phone", mCurrentUser.getPhone());
		req.addParam("registration_id", "");// TODO
		req.addParam("ptype", "1");// 1表示android机
		try {
			req.addParam("platform", URLEncoder.encode(platform, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		req.addParam("equip_id", Settings.getInstance().getUUID());
		req.addParam("tag", "");
		req.addParam("alias", alias);
		req.addParam("source", "");// APP安装来源
		req.addParam("version", "1.0");// TODO //APP版本号
		req.setParser(new GenericModelParser());
		VolleyDispatcher.getInstance().dispatch(req);
	}
}
