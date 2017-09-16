package com.tianpingpai.pay.weixin;

public class WeixinPayConfig {
    private String appId;
    private String partnerId;
    private String APIKEY;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getApiKey() {
        return APIKEY;
    }

    public void setAPIKEY(String APIKEY) {
        this.APIKEY = APIKEY;
    }
}
