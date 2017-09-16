package com.tianpingpai.pay.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tianpingpai.pay.PayEvent;
import com.tianpingpai.pay.PayService;

public class BaseWxPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, null);//TODO
        api.registerApp("wxe79020b52aadb108");//TODO
        api.handleIntent(getIntent(), this);
        Log.e("xx","oncreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e("xx", "onNewIntent");
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
//        Toast.makeText(ContextProvider.getContext(),"OnReq" + req,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WeixinPlatform weixin = PayService.getInstance().getWeixinPlatform();
            switch (resp.errCode){
                case 0:
                    weixin.notifyEvent(PayEvent.OnPaySuccess);
                    break;
                case -1:
                    weixin.notifyEvent(PayEvent.OnPayFailed);
                    break;
                case -2:
                    weixin.notifyEvent(PayEvent.OnPayCanceled);
                    break;
            }
            finish();
        }
    }
}
