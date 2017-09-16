package com.tianpingpai.pay.weixin;

import android.app.Activity;
import android.util.Log;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.pay.PayEvent;
import com.tianpingpai.pay.PayResult;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.pay.Platform;

import static com.tianpingpai.pay.PayEvent.OnPayFailed;
import static com.tianpingpai.pay.PayEvent.OnPrepared;
import static com.tianpingpai.pay.PayEvent.OnPreparing;

public class WeixinPlatform extends Platform {

    public void setConfig(WeixinPayConfig config) {
        this.config = config;
    }
    private WeixinPayConfig config;

    private Payment payment;

    public Payment getPayment(){
        return payment;
    }

    private HttpRequest.ResultListener<ModelResult<Model>> prepayIdListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Log.e("xx", "result:" + data);
            Payment payment = request.getAttachment(Payment.class);
            if (data.isSuccess()) {
                Model model = data.getModel();
                PayReq req = new PayReq();
                req.prepayId = model.getString("prepayId");
                req.partnerId = model.getString("partnerId");
                req.appId = model.getString("appId");
                req.nonceStr = model.getString("noncestr");
                req.sign = model.getString("sign");
                req.timeStamp = model.getString("timestamp");
                req.sign = model.getString("sign");
                notifyEvent(OnPrepared);
                doPay(req);
            } else {
                com.tianpingpai.pay.PayResult payResult = new PayResult();
                payResult.setEvent(OnPayFailed);
                payResult.setErrorMsg(data.getDesc());
                notifyResult(payment, payResult);
            }
        }
    };


    public void notifyEvent(PayEvent event){
        com.tianpingpai.pay.PayResult payResult = new PayResult();
        payResult.setEvent(event);
        notifyResult(payment,payResult);
    }

    private void getPrepayId(Payment payment) {
        String url = ContextProvider.getBaseURL() + "/pay/mobile/weixin/prepay";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, prepayIdListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("order_ids", payment.getOrderId());
        if(config != null && config.getType() != null){
            req.addParam("type",config.getType());
        }
        if(payment.getCouponIds() != null){
            req.addParam("couponId",payment.getCouponIds());
        }
        req.setAttachment(payment);
        req.setParser(new GenericModelParser());
        req.addHeader("ticket", payment.getTicket());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                notifyEvent(OnPayFailed);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        notifyEvent(OnPreparing);
    }

    private void doPay(PayReq req) {
        req.packageValue = "Sign=WXPay";
        msgApi.registerApp(req.appId);
        msgApi.sendReq(req);
    }

    IWXAPI msgApi = null;

    @Override
    public void pay(Payment payment, Activity activity) {
        config = new WeixinPayConfig();//TODO
        config.setAPIKEY("2ae0e33acfb30aa735f1c7b852145b6d");//TODO
        msgApi = WXAPIFactory.createWXAPI(activity, "wxe79020b52aadb108");//TODO
        msgApi.registerApp("wxe79020b52aadb108");//TODO
        this.payment = payment;
        getPrepayId(payment);
    }

    @Override
    public boolean isValid(Activity a) {
        IWXAPI api = WXAPIFactory.createWXAPI(a, null);
        return api.isWXAppInstalled() && api.isWXAppSupportAPI();
    }
}
