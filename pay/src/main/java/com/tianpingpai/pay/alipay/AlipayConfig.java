package com.tianpingpai.pay.alipay;

/**
 * Created by Chon on 15/8/7.
 */
public class AlipayConfig {
    private String parter;
    private String sellerId;
    private String rsaPrivate;
    private String type;

    public void setType(String t){
        this.type = t;
    }

    public String getType(){
        return type;
    }

    public String getParter() {
        return parter;
    }

    public void setParter(String parter) {
        this.parter = parter;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getRsaPrivate() {
        return rsaPrivate;
    }

    public void setRsaPrivate(String rsaPrivate) {
        this.rsaPrivate = rsaPrivate;
    }
}
