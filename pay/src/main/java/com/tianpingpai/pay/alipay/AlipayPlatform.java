package com.tianpingpai.pay.alipay;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.pay.Platform;

import static com.tianpingpai.pay.PayEvent.OnPayCanceled;
import static com.tianpingpai.pay.PayEvent.OnPayFailed;
import static com.tianpingpai.pay.PayEvent.OnPayPending;
import static com.tianpingpai.pay.PayEvent.OnPaySuccess;
import static com.tianpingpai.pay.PayEvent.OnPrepared;
import static com.tianpingpai.pay.PayEvent.OnPreparing;


public class AlipayPlatform extends Platform {

    public void setConfig(AlipayConfig config) {
        this.config = config;
    }

    private AlipayConfig config;

    @Override
    public void pay(Payment payment, final Activity activity) {
        getPrepayId(payment, activity);
    }

    private HttpRequest.ResultListener<ModelResult<Model>> prepayIdListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(final HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            final Payment payment = request.getAttachment(Payment.class);

            if(data.getCode() == STATUS_CODE_PAY_BY_BALANCE){
                notifyEvent(payment, OnPaySuccess);
                return;
            }

            if (data.isSuccess()) {
                Model model = data.getModel();
                String payInfo = model.getString("link");//TODO
                String sign = model.getString("sign");
                payInfo = payInfo + "&sign=\"" + sign + "\"&"
                        + getSignType();

                Class<? extends Activity> aClass = request.getAttachment(Class.class);
                final PayTask task = new PayTask(request.getAttachment(aClass));
                final String finalPayInfo = payInfo;
                notifyEvent(payment, OnPrepared);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String result = task.pay(finalPayInfo,true);
                        com.tianpingpai.pay.alipay.PayResult payResult = new com.tianpingpai.pay.alipay.PayResult(result);
                        String resultStatus = payResult.getResultStatus();
                        if (TextUtils.equals(resultStatus, "9000")) {
                            //成功
                            notifyEvent(payment, OnPaySuccess);
                        } else {
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                notifyEvent(payment, OnPayPending);
                            } else {
                                //支付失败
                                com.tianpingpai.pay.PayResult failedPayResult = new com.tianpingpai.pay.PayResult();
                                failedPayResult.setEvent(OnPayFailed);

                                String errorMsg = "支付宝失败失败!错误码:" + resultStatus;
                                //TODO
                                if("4000".equals(resultStatus)){
                                    errorMsg = "支付宝失败失败!";
                                }else if("6001".equals(resultStatus)){
                                    failedPayResult.setEvent(OnPayCanceled);
                                    errorMsg = "用户中途取消 !";
                                }else if("6002".equals(resultStatus)){
                                    errorMsg = "支付宝网络错误.";
                                }
                                failedPayResult.setErrorMsg(errorMsg);
                                notifyResult(payment, failedPayResult);
                            }
                        }
                    }
                }.start();
            } else {
                com.tianpingpai.pay.PayResult failedPayResult = new com.tianpingpai.pay.PayResult();
                failedPayResult.setEvent(OnPayFailed);
                failedPayResult.setErrorMsg(data.getDesc());
                notifyResult(payment, failedPayResult);
            }
        }
    };

    private void getPrepayId(final Payment payment, Activity a) {
        String url = ContextProvider.getBaseURL() + "/pay/mobile/alipay/prepay";
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, prepayIdListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("order_ids", payment.getOrderId());
        if(payment.getCouponIds() != null){
            req.addParam("couponId",payment.getCouponIds());
            Log.e("xx","couponId:" + payment.getCouponIds());
        }
        req.setAttachment(payment);
        req.setAttachment(a.getClass());
        req.setAttachment(a);
        req.addHeader("ticket",payment.getTicket());
        if(config.getType() != null){
            req.addParam("type",config.getType());
        }
        if(payment.getValidationCode() != null){
            req.addParam("code",payment.getValidationCode());
        }

        if(payment.getBalance() > 0){
            req.addParam("balance",payment.getBalance() + "");
        }

        req.setParser(new GenericModelParser());
        req.setErrorListener(new HttpRequest.ErrorListener() {
            @Override
            public void onError(HttpRequest<?> request, HttpError eror) {
                com.tianpingpai.pay.PayResult failedPayResult = new com.tianpingpai.pay.PayResult();
                failedPayResult.setEvent(OnPayFailed);
                //TODO
                failedPayResult.setErrorMsg(eror.getErrorMsg());
                notifyResult(payment, failedPayResult);
            }
        });
        VolleyDispatcher.getInstance().dispatch(req);
        notifyEvent(payment, OnPreparing);
    }

    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
