package com.tianpingpai.buyer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.brother.tpp.tools.ParseHrefUtil;
import com.tianpingpai.buyer.ui.MainViewController;
import com.tianpingpai.buyer.ui.OrderDetailViewController;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ContainerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class JpushBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "JpushBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            TLog.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            TLog.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            TLog.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            TLog.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            TLog.d(TAG, "[MyReceiver] 用户点击打开了通知");

            String extra = intent.getExtras().getString(JPushInterface.EXTRA_EXTRA);
            if (extra != null && !extra.equals("")) {
                TLog.w(TAG, "40-------------extra=" + extra);
                try {
                    JSONObject object = new JSONObject(extra);
                    if (object.optString("type").equals("1")) {
                        //打开自定义的Activity
                        Intent i = new Intent(context, ContainerActivity.class);
                        i.putExtra(ContainerActivity.KEY_CONTENT, OrderDetailViewController.class);
                        i.putExtra(OrderDetailViewController.KEY_ORDER_ID, Long.parseLong(object.getString("order_id")));
                        i.putExtras(bundle);
                        startActivity(context, i);
                    } else {
                        String href = object.optString("href");
                        ParseHrefUtil.handleURL(context,href);//TODO
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Intent mainFragmentIntent = new Intent(context, ContainerActivity.class);
                mainFragmentIntent.putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
                startActivity(context, mainFragmentIntent);
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            TLog.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            TLog.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            TLog.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void startActivity(Context context, Intent i) {
        Intent intents[] = new Intent[2];
        intents[0] = new Intent(context, ContainerActivity.class);
        intents[0].putExtra(ContainerActivity.KEY_CONTENT, MainViewController.class);
        intents[0].putExtra("position", 0);
        intents[0].setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intents[1] = i;
        if (context != null) {
            context.startActivities(intents);
        }
    }
}
