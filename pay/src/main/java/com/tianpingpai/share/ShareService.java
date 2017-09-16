package com.tianpingpai.share;

import com.tianpingpai.core.ModelManager;
import com.tianpingpai.share.weixin.WeixinConfig;
import com.tianpingpai.utils.SingletonFactory;

public class ShareService extends ModelManager<ShareResult,ShareContent> {

    public static ShareService getIntance(){
        return SingletonFactory.getInstance(ShareService.class);
    }

    public WeixinConfig getWeixinConfig() {
        return weixinConfig;
    }

    public void setWeixinConfig(WeixinConfig weixinConfig) {
        this.weixinConfig = weixinConfig;
    }

    private WeixinConfig weixinConfig;

}
