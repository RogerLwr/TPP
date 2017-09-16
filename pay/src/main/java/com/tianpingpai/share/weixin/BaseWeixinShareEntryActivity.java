package com.tianpingpai.share.weixin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tianpingpai.share.ShareService;

public class BaseWeixinShareEntryActivity  extends Activity implements IWXAPIEventHandler{

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO
        api = WXAPIFactory.createWXAPI(this, ShareService.getIntance().getWeixinConfig().getAppId(), false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                Log.e("xx","分享成功");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                Log.e("xx","取消分享");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                Log.e("xx","分享拒绝");
                break;
        }
    }
}
