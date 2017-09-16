package com.tianpingpai.pay;


import com.tianpingpai.core.ModelManager;
import com.tianpingpai.pay.alipay.AlipayConfig;
import com.tianpingpai.pay.alipay.AlipayPlatform;
import com.tianpingpai.pay.weixin.WeixinPayConfig;
import com.tianpingpai.pay.weixin.WeixinPlatform;
import com.tianpingpai.utils.SingletonFactory;

public class PayService extends ModelManager<PayResult,Payment>{

    public static PayService getInstance(){
        return SingletonFactory.getInstance(PayService.class);
    }

    WeixinPlatform weixinPlatform = new WeixinPlatform();
    public WeixinPlatform getWeixinPlatform(){
        weixinPlatform.setConfig(weixinPayConfig);
        weixinPlatform.setPayService(this);
        return weixinPlatform;
    }

    public AlipayPlatform getAlipayPlatform(){
        AlipayPlatform alipayPlatform =  new AlipayPlatform();
        alipayPlatform.setPayService(this);
        return alipayPlatform;
    }

    public void setWeixinPayConfig(WeixinPayConfig weixinPayConfig) {
        this.weixinPayConfig = weixinPayConfig;
    }

    public void setAlipayConfig(AlipayConfig alipayConfig) {
        this.alipayConfig = alipayConfig;
    }

    private WeixinPayConfig weixinPayConfig;
    private AlipayConfig alipayConfig;
}
