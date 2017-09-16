package com.tianpingpai.buyer.ui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.brother.tpp.tools.PriceFormat;
import com.tianpingpai.buyer.R;
import com.tianpingpai.buyer.adapter.SubOrdersAdapter;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelEvent;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.OrderManager;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.pay.PayResult;
import com.tianpingpai.pay.PayService;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.pay.alipay.AlipayConfig;
import com.tianpingpai.pay.alipay.AlipayPlatform;
import com.tianpingpai.pay.weixin.WeixinPlatform;
import com.tianpingpai.tools.OrderStatusTool;
import com.tianpingpai.tools.TLog;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.utils.JsonObjectMapper;
import com.tianpingpai.utils.TicketLoader;
import com.tianpingpai.widget.OnSelectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ActionBar(title = "支付")
@Layout(id = R.layout.ui_select_payment)
public class SelectPaymentViewController extends BaseViewController {

    public static final String KEY_ORDER_ID = "Key.orderId";
    private static final int REQUEST_CODE_PAYMENT = 101;
    public static final String KEY_SELECT_COUPON = "Key.selectCoupon";
    public static final int REQUEST_CODE_COUPON = 818;
    public static final String KEY_NEXT_INTENT = "nextIntent";
    public static final String KEY_GO_COMMENT = "goComment";

    private boolean isGoComment = false;
    private Model model;
    private Model selectCoupon;
    private String orderIds;
    private TicketLoader ticketLoader = new TicketLoader();

    @Binding(id = R.id.coupon_btn_text_view)
    private TextView couponBtnTextView;
    @Binding(id = R.id.total_amount_text_view)
    private TextView totalAmountTextView;
    @Binding(id = R.id.total_mny_text_view)
    private TextView totalMnyTextView;
    @Binding(id = R.id.weixin_pay_container)
    private View weixinPayContainer;
    @Binding(id = R.id.weixin_pay_button)
    private RadioButton weixinPayButton;
    @Binding(id = R.id.alipay_container)
    private View alipayContainer;
    @Binding(id = R.id.alipay_button)
    private RadioButton alipayButton;
    @Binding(id = R.id.orders_list_view)
    private ListView ordersListView;
    @Binding(id = R.id.submit_button)
    private View submitButton;

    private Intent nextIntent;
    private ButtonGroup buttonGroup = new ButtonGroup();

    private SubOrdersAdapter ordersAdapter = new SubOrdersAdapter();
    private ActionSheetDialog dialog = new ActionSheetDialog();

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        orderIds = a.getIntent().getStringExtra(KEY_ORDER_ID);
        nextIntent =  a.getIntent().getParcelableExtra(KEY_NEXT_INTENT);
        isGoComment = a.getIntent().getBooleanExtra(KEY_GO_COMMENT, isGoComment);
        PayService.getInstance().registerListener(payResultListener);
    }

    @Override
    public void onActivityDestroyed(Activity a) {
        super.onActivityDestroyed(a);
        PayService.getInstance().unregisterListener(payResultListener);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        showContent();
        ordersListView.setAdapter(ordersAdapter);
        buttonGroup.add(weixinPayButton, rootView.findViewById(R.id.weixin_pay_container));
        buttonGroup.add(alipayButton, rootView.findViewById(R.id.alipay_container));

        couponBtnTextView = (TextView) rootView.findViewById(R.id.coupon_btn_text_view);
        couponBtnTextView.setOnClickListener(couponOnclickListener);
        submitButton.setOnClickListener(submitButtonListener);
        ticketLoader.setTicketLoaderListener(new TicketLoader.TicketLoaderListener() {
            @Override
            public void onTicketLoaded(TicketLoader.Ticket t) {
                loadData();
            }

            @Override
            public void onTicketFailed(HttpError error) {
                hideLoading();
                Toast.makeText(ContextProvider.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        });
        ticketLoader.load();
        showLoading();

        ArrayList<String> platforms = getActivity().getIntent().getStringArrayListExtra(KEY_SELECT_COUPON);

        if(platforms == null){
            weixinPayContainer.setVisibility(View.VISIBLE);
            weixinPayButton.setChecked(true);
            alipayContainer.setVisibility(View.VISIBLE);
            alipayButton.setChecked(false);
        }else{
            for (String p : platforms) {
                try {
                    JSONObject obj = new JSONObject(p);
                    int id = obj.getInt("id");
                    boolean isDefault = obj.optBoolean("default");
                    if (id == 0) {
                        weixinPayContainer.setVisibility(View.VISIBLE);
                        weixinPayButton.setChecked(isDefault);
                    } else {
                        alipayContainer.setVisibility(View.VISIBLE);
                        alipayButton.setChecked(isDefault);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handlePaymentDataError();
                }
            }
            if(!weixinPayButton.isChecked() && !alipayButton.isChecked()){
                alipayButton.setChecked(true);
            }
        }
    }

    private void handlePaymentDataError(){
        weixinPayContainer.setVisibility(View.VISIBLE);
        weixinPayButton.setChecked(true);
        alipayContainer.setVisibility(View.VISIBLE);
        alipayButton.setChecked(false);
    }

    private SelectCouponViewController selectCouponViewController = new SelectCouponViewController();

    private OnSelectListener couponSelectListener = new OnSelectListener() {
        @Override
        public void onSelect() {
            selectCoupon = selectCouponViewController.getSelectedCoupon();
            updateCouponMoney();
        }
    };
    private View.OnClickListener couponOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(model == null){
                return;
            }
            List<Model> couponList = model.getList("coupons", Model.class);

            selectCouponViewController.setActionSheet(getActionSheet(true));
            selectCouponViewController.setOnSelectListener(couponSelectListener);
            selectCouponViewController.setCoupons((ArrayList<Model>) couponList);
            selectCouponViewController.setSelectedCoupon(selectCoupon);
            getActionSheet(true).setViewController(selectCouponViewController);
            getActionSheet(true).setHeight(getView().getHeight());
            getActionSheet(true).show();
        }
    };

    private HttpRequest.ResultListener<ModelResult<Model>> dataListener = new HttpRequest.ResultListener<ModelResult<Model>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Model>> request, ModelResult<Model> data) {
            Log.e("xx", "data:" + data.getModel());
            hideLoading();
            if (data.isSuccess()) {
                Model m = data.getModel();
                model = m;
                List<Model> orders = m.getList("orders", Model.class);
                ordersAdapter.setModels(new ArrayList<>(orders));
                List<Model> coupons = m.getList("coupons", Model.class);
                for (Model model : coupons) {
                    if (model.getBoolean("valid")) {
                        selectCoupon = model;
                        break;
                    }
                }
                updateCouponMoney();
            }
        }
    };

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/order/pay";
        String orderIds = model == null ? this.orderIds : model.getString("id");
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, dataListener);
        req.addParam("order_ids", orderIds);
        req.addParam("coupon_type", "-1");
        req.addHeader("ticket", ticketLoader.getTicket().getValue());
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private void updateCouponMoney() {
        if (model != null) {
            double mny = model.getDouble("mny");
            totalAmountTextView.setText(String.format("总金额: ¥%s", PriceFormat.format(mny)));
            double couponMny = 0;
            if (selectCoupon != null) {
                //TODO
                TLog.e("xx", "238------selectCoupon="+selectCoupon);
                if(selectCoupon.getInt("coupon_type") == 0){ // 固定金额优惠券
                    couponMny = selectCoupon.getDouble("money");
                }else if(selectCoupon.getInt("coupon_type") == 1){  //百分比折扣券
                    couponMny = ( mny * (100-selectCoupon.getInt("coupon_rate")) ) / 100;
                    if(couponMny >= selectCoupon.getDouble("max_money") ){
                        couponMny = selectCoupon.getDouble("max_money");
                    }
                }
            }
            float totalMny = PriceFormat.formatPrice((float) (mny - couponMny));
            couponBtnTextView.setText(String.format("-¥%s" ,PriceFormat.format(couponMny)));
            totalMnyTextView.setText(String.format("¥%s", PriceFormat.format(totalMny)));
        }
    }

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Payment pay = new Payment();
            pay.setOrderId(orderIds);
            if (weixinPayButton.isChecked()) {
                WeixinPlatform weixinPay = PayService.getInstance().getWeixinPlatform();
                if (!weixinPay.isValid(getActivity())) {
                    Toast.makeText(ContextProvider.getContext(), "未安装微信或者 ,微信版本过低!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectCoupon != null) {
                    pay.setCouponIds(selectCoupon.getLong("id") + "");
                }
                weixinPay.pay(pay, getActivity());
            } else {
                AlipayConfig config = new AlipayConfig();
                AlipayPlatform alipayPlatform = PayService.getInstance().getAlipayPlatform();
                alipayPlatform.setConfig(config);
                if (selectCoupon != null) {
                    pay.setCouponIds(selectCoupon.getLong("id") + "");
                }
                alipayPlatform.pay(pay, getActivity());
            }
        }
    };

    private ModelStatusListener<PayResult, Payment> payResultListener = new ModelStatusListener<PayResult, Payment>() {
        @Override
        public void onModelEvent(PayResult result, Payment model) {
            if (model != null && model.getOrderId() != null && model.getOrderId().equals(orderIds)) {
                switch (result.getEvent()) {
                    case OnPreparing:
                        showLoading();//TODO
                        break;
                    case OnPrepared:
                        hideLoading();
                        break;
                    case OnPayCanceled:
                        hideLoading();
                        Toast.makeText(ContextProvider.getContext(), "用户取消!", Toast.LENGTH_SHORT).show();
                        break;
                    case OnPayFailed:
                        hideLoading();
                        Toast.makeText(ContextProvider.getContext(), "支付失败!:" + result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        break;
                    case OnPaySuccess:
                        if (getActivity() != null) {
                            if(isGoComment){
                                OrderStatusTool tool = new OrderStatusTool(getActivity());
                                tool.setOrderId(orderIds);//TODO
                                tool.setModel(SelectPaymentViewController.this.model);
                                tool.setBaseViewController(SelectPaymentViewController.this);
                                tool.getOrderStatus();
                                OrderManager.getInstance().notifyEvent(ModelEvent.OnModelUpdate,null);
//                                BuyerApplication.goCommentView(getActivity());
                            }else{

                                Intent intent = new Intent(getActivity(), ContainerActivity.class);
                                intent.putExtra(ContainerActivity.KEY_CONTENT, PaySuccessViewController.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }

                        }
                        break;
                }
            }
        }
    };

    @Override
    public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a,requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
//                orderStatusTextView.setText(result);
            }
        } else if (requestCode == REQUEST_CODE_COUPON) {
            if (resultCode == Activity.RESULT_OK) {
                Model selectCoupon = null;
                try {
                    JSONObject object = new JSONObject(data.getStringExtra(KEY_SELECT_COUPON));
                    selectCoupon = new Model();
                    JsonObjectMapper.map(selectCoupon,object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (selectCoupon != null) {
                    Log.e("xx", "115--------------" + selectCoupon);
                    this.selectCoupon = selectCoupon;
                    updateCouponMoney();
                }
            }
        }
    }

    @Override
    public boolean onBackKeyDown(Activity a) {
        if(getActionSheet(true) != null && getActionSheet(true).isShowing()){
            getActionSheet(true).dismiss();
            return true;
        }
        dialog.setActionSheet(getActionSheet(true));
        dialog.setTitle("是否确认放弃付款？");
        dialog.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextIntent != null){
                    Log.e("xx","nextIntent:" + nextIntent.getExtras());
                    getActivity().startActivity(nextIntent);
                }
                getActivity().finish();
            }
        });
        dialog.show();
        return true;
    }
}
