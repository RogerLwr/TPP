package com.tianpingpai.pay;

import android.app.Activity;

public abstract class Platform {

    public static final int STATUS_CODE_PAY_BY_BALANCE = 10001;

    public abstract void pay(Payment payment,Activity activity);

    public PayService getPayService() {
        return payService;
    }

    public void setPayService(PayService payService) {
        this.payService = payService;
    }

    private PayService payService;

    protected void notifyResult(Payment payment, PayResult payResult) {
        getPayService().notifyEvent(payResult, payment);
    }

    protected void notifyEvent(Payment payment,PayEvent event){
        PayResult result = new PayResult();
        result.setEvent(event);
        notifyResult(payment,result);
    }

    public  boolean isValid(Activity activity){
        return true;
    }
}
