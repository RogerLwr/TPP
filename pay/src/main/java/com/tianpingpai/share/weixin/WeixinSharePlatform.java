package com.tianpingpai.share.weixin;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.pay.R;
import com.tianpingpai.share.ShareContent;
import com.tianpingpai.share.SharePlatform;
import com.tianpingpai.share.ShareService;

public class WeixinSharePlatform implements SharePlatform {

    public static final int SceneSession = 0;
    public static final int SceneTimeline = 1;
    public static final int SceneFavorite = 2;

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    private int scene = SceneSession;

    private static IWXAPI api;

    public static IWXAPI getWXAPI(){
        if(api == null){
            api = WXAPIFactory.createWXAPI(ContextProvider.getContext(), ShareService.getIntance().getWeixinConfig().getAppId(),true);
            api.registerApp(ShareService.getIntance().getWeixinConfig().getAppId());
        }
        return api;
    }

    @Override
    public int getIcon() {
        if(scene == SceneSession){
            return R.drawable._share_ic_weixin_session;
        }
        if(scene == SceneTimeline){

        }
        return 0;
    }

    @Override
    public int getName() {
        if(scene == SceneSession){
            return R.string._share_weixin_session;
        }
        return 0;
    }

    @Override
    public void share(ShareContent content) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = content.getLink();
        WXMediaMessage msg = new WXMediaMessage(webpage);

        msg.title = content.getTitle();
        msg.description = content.getDescription();
        msg.setThumbImage(content.getThumbImage());
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        getWXAPI().sendReq(req);
    }

    @Override
    public boolean isValid() {
        return getWXAPI().isWXAppInstalled() && getWXAPI().isWXAppSupportAPI();
    }

    @Override
    public int getInvalidHint() {
        return R.string._share_weixin_invalid_hint;
    }
}
