package com.tianpingpai.seller.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.tianpingpai.seller.R;
import com.brother.tppseller.tools.ParseHrefUtil;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.seller.ui.WebViewController;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.seller.ui.MainViewController;
import com.tianpingpai.seller.ui.OrderDetailViewController;
import com.tianpingpai.ui.ContainerActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);
			Log.e("xx", "msg:" + bundle);
			printBundle(bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			Log.e("xx", "msg:" + bundle);
			printBundle(bundle);

			BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(ContextProvider.getContext());
			builder.statusBarDrawable = R.drawable.ic_app;
			builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; //设置为自动消失
			builder.notificationDefaults = Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE; //设置铃声振动高亮

			String extra = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
			if(extra != null && !extra.equals("")) {
				try {
					JSONObject object = new JSONObject(extra);
					String order_status = object.optString("order_status");
					Log.e("xx", "61-------------order_status="+order_status);
					if ("1".equals(order_status)) {
						Log.e("xx", "67----状态1 新订单");
						NotificationManager manger = (NotificationManager) ContextProvider.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
						Notification n = new Notification();
						n.sound = Uri.parse("android.resource://" + ContextProvider.getContext().getPackageName() + "/" + R.raw.tianpingpai_order);
//						n.sound = Uri.parse("android.resource://" + ContextProvider.getContext().getPackageName() + "/" + R.raw.orders_ring_by_ting);
						manger.notify(1, n);
						OrderModel order = new OrderModel();
						order.setId(object.getLong("order_id"));
						OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,order);
					} else {
						Log.e("xx", "69----状态2 其他");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			JPushInterface.setPushNotificationBuilder(1, builder);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.w(TAG, "[MyReceiver] 用户点击打开了通知");
            
            String extra = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
            if(extra != null && !extra.equals("")){
	            try {
					JSONObject object = new JSONObject(extra);

					if("1".equals(object.optString("type")) || "2".equals(object.optString("type"))){
						//打开自定义的Activity˛
						String orderId = object.getString("order_id");
						Intent i = new Intent(context, ContainerActivity.class);
						if("1".equals(object.optString("type"))){
							i.putExtra(ContainerActivity.KEY_CONTENT, OrderDetailViewController.class);
							i.putExtras(bundle);
							i.putExtra(OrderDetailViewController.KEY_ORDER_ID, Long.parseLong(object.getString("order_id")));
						}else{
							i.putExtra(ContainerActivity.KEY_CONTENT,WebViewController.class);
							String url = URLApi.getWebBaseUrl() + String.format("/saler/upstream/order/detail?order_id=%s&accessToken=%s",orderId, UserManager.getInstance().getCurrentUser().getAccessToken());
							i.putExtra(WebViewController.KEY_ACTION_BAR_STYLE, WebViewController.ACTION_BAR_STYLE_HIDDEN);
							i.putExtra(WebViewController.KEY_URL,url);
						}
						startActivity(context, i);
					}else {

						String href = object.optString("href");
						String path = getPath(context, href);
						Log.w(TAG, "57------------href=" + href + ",path=" + path);
						Intent i = ParseHrefUtil.handleAppAction(context,href, path);

						int queryIndex = href.indexOf("?");
						ArrayList<NameValuePair> params = new ArrayList<>();
						if (queryIndex != -1) {
							String queryStrings = href.substring(queryIndex + 1);
							String[] paramStrings = queryStrings.split("&");

							for (String ps : paramStrings) {
								int index = ps.indexOf("=");
								String key = ps.substring(0, index);
								String value = ps.substring(index + 1);
								params.add(new BasicNameValuePair(key, value));
							}
						}

						if (i != null) {
							for (NameValuePair p : params) {
								String key = p.getName();
								String value = p.getValue();
								i.putExtra(key, Integer.parseInt(value));
								Log.e("xx", "param:" + key + " = " + value);
							}
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							context.startActivity(i);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	private void startActivity(Context context, Intent i) {
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Intent intents[] = new Intent[2];
		intents[0] = new Intent(context, ContainerActivity.class);
		intents[0].putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
		intents[0].setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intents[1] = i;
		if (context != null) {
			context.startActivities(intents);
		}
	}

	private String getPath(Context context, String href) {

		String path = "";
		URI uri;
		try {
			uri = new URI(href);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
		String scheme = uri.getScheme();

		if ("http".equalsIgnoreCase(scheme)) {

			String host = uri.getHost();
			Log.e("xx", "host:" + host);
			path = uri.getPath();
			Log.w(TAG, "148----href=" + href + ",scheme=" + scheme + ",path=	" + path);
			Intent i = new Intent(context, WebViewController.class);
			i.putExtra(WebViewController.KEY_URL, href);
			startActivity(context, i);
			return "";

		} else if ("fake".equalsIgnoreCase(scheme)) {
			Log.e("xx", "href:" + href);
			Log.e("xx", "path:" + uri.getPath());
			Log.w("xx", "124----href=" + href + ",scheme=" + scheme + ",path=	" + uri.getPath());
			path = uri.getPath();
		}
		return path;

	}

}
