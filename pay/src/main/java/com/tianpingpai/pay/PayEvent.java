package com.tianpingpai.pay;

public enum PayEvent {
    OnPreparing("正在准备"),
    OnPrepared("已经准备"),
    OnPaySuccess("支付成功"),
    OnPayCanceled("支付取消"),
    OnPayFailed("支付失败"),
    OnPayPending("支付正在进行"),;

    private String desc;
    PayEvent(String s) {
        this.desc = s;
    }

    @Override
    public String toString() {
        return desc;
    }
}
