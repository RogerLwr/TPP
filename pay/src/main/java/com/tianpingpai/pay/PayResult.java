package com.tianpingpai.pay;

public class PayResult {
    public PayEvent getEvent() {
        return event;
    }

    public void setEvent(PayEvent event) {
        this.event = event;
    }

    private PayEvent event;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    private Platform platform;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String errorMsg;

    public boolean isSuccess(){
        return event == PayEvent.OnPaySuccess;
    }
}
