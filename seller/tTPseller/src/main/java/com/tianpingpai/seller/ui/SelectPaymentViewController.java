package com.tianpingpai.seller.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.seller.R;
import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.core.ModelStatusListener;
import com.tianpingpai.http.HttpError;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.model.Model;
import com.tianpingpai.parser.GenericModelParser;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.pay.PayResult;
import com.tianpingpai.pay.PayService;
import com.tianpingpai.pay.Payment;
import com.tianpingpai.pay.alipay.AlipayConfig;
import com.tianpingpai.pay.alipay.AlipayPlatform;
import com.tianpingpai.pay.weixin.WeixinPayConfig;
import com.tianpingpai.pay.weixin.WeixinPlatform;
import com.tianpingpai.seller.adapter.SubOrdersAdapter;
import com.tianpingpai.seller.manager.UserManager;
import com.tianpingpai.seller.model.Coupon;
import com.tianpingpai.seller.model.UserModel;
import com.tianpingpai.seller.tools.TLog;
import com.tianpingpai.seller.tools.URLApi;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.ContainerActivity;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.JsonObjectMapper;
import com.tianpingpai.utils.PriceFormat;
import com.tianpingpai.utils.TicketLoader;
import com.tianpingpai.widget.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@ActionBar(title = "支付")
@Layout(id = R.layout.ui_select_payment)
public class SelectPaymentViewController extends BaseViewController {

    private int payTypeScene = 0; //        场景支付时 区分跳转 0待发货和 1已完成订单
    public static final String KEY_PAY_TYPE = "Key.payTypeScene";
    public static final String KEY_ORDER_ID = "Key.orderId";
    private static final int REQUEST_CODE_PAYMENT = 101;
    public static final String KEY_SELECT_COUPON = "Key.selectCoupon";
    public static final int REQUEST_CODE_COUPON = 818;
    public static final String KEY_NEXT_INTENT = "nextIntent";
    private int orderTypeGroup = 0;
    public static final String KEY_ORDER_TYPE_GROUP = "key.OrderTypeGroup"; // 区分团购订单
    private int groupID;
    public static final String KEY_GROUP_ID = "Key.GroupID";

    private Model model;
    private Model selectCoupon;
    private String orderIds;
    private TicketLoader ticketLoader = new TicketLoader();

    @Binding(id = R.id.coupon_btn_text_view)
    private TextView couponBtnTextView;
    @Binding(id = R.id.total_amount_text_view)
    private TextView totalAmountTextView;
    @Binding(id = R.id.validation_code_container)
    private View validationCodeContainer;
    @Binding(id = R.id.validation_code_edit_text)
    private EditText validationCodeEditText;
    @Binding(id = R.id.get_validation_code_button)
    private TextView getValidationCodeButton;
    @Binding(id = R.id.validation_hint_text_view)
    private TextView validationHintTextView;
    @Binding(id = R.id.validation_divider)
    private View validationDivider;

    @Binding(id = R.id.balance_text_view)
    private TextView balanceTextView;
    @Binding(id = R.id.use_balance_button)
    private ImageView userBalanceButton;
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

    private Handler handler = new Handler();
    private long counterStartTime = 0;

//    private ArrayList<String> platforms;
    private Intent nextIntent;
    private ButtonGroup buttonGroup = new ButtonGroup();

    private SubOrdersAdapter ordersAdapter = new SubOrdersAdapter();

    @Override
    public void onActivityCreated(Activity a) {
        super.onActivityCreated(a);
        payTypeScene = a.getIntent().getIntExtra(KEY_PAY_TYPE, 0);
        orderIds = a.getIntent().getStringExtra(KEY_ORDER_ID);
        nextIntent =  a.getIntent().getParcelableExtra(KEY_NEXT_INTENT);
        orderTypeGroup = getActivity().getIntent().getIntExtra(KEY_ORDER_TYPE_GROUP, 0);
        groupID = getActivity().getIntent().getIntExtra(KEY_GROUP_ID, 0);
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
                Toast.makeText(ContextProvider.getContext(),error.getErrorMsg(),Toast.LENGTH_SHORT).show();
            }
        });
        ticketLoader.load(orderTypeGroup, groupID);
        showLoading();
        //TODO 我们这个版本只支持支付宝支付。
        weixinPayContainer.setVisibility(View.GONE);
        weixinPayButton.setChecked(false);
        alipayContainer.setVisibility(View.VISIBLE);
        alipayButton.setChecked(true);
    }

    private void handlePaymentDataError(){
//        weixinPayContainer.setVisibility(View.VISIBLE);
//        weixinPayButton.setChecked(true);
        alipayContainer.setVisibility(View.VISIBLE);
        alipayButton.setChecked(true);
    }
    private SelectCouponViewController selectCouponViewController;

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
            if(selectCouponViewController == null){
                selectCouponViewController = new SelectCouponViewController();
                List<Model> couponList = model.getList("coupons", Model.class);
                selectCouponViewController.setActionSheet(getActionSheet(true));
                selectCouponViewController.setOnSelectListener(couponSelectListener);
                selectCouponViewController.setCoupons((ArrayList<Model>) couponList);
                selectCouponViewController.setSelectedCoupon(selectCoupon);
                getActionSheet(true).setViewController(selectCouponViewController);
                getActionSheet(true).setHeight(getView().getHeight());
            }
            getActionSheet(true).show();
//            Intent intent = new Intent(getActivity(), ContainerActivity.class);
//            intent.putExtra(ContainerActivity.KEY_CONTENT, SelectCouponViewControllerOld.class);
//            ArrayList<Coupon> coupons = new ArrayList<>();
//            List<Model> couponList = model.getList("coupons", Model.class);
//            for (Model m : couponList) {
//                Coupon c = new Coupon();
//                JsonObjectMapper.map(m, c);
//                coupons.add(c);
//            }
//            intent.putExtra(SelectCouponViewController.KEY_COUPONS, coupons);
//            intent.putExtra(SelectCouponViewController.KEY_SELECT_COUPON, selectCoupon);
//            getActivity().startActivityForResult(intent, REQUEST_CODE_COUPON);

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
                getBinder().bindData(model);
                List<Model> orders = m.getList("orders", Model.class);
                ordersAdapter.setModels(new ArrayList<>(orders));
                List<Model> coupons = m.getList("coupons", Model.class);
                if(coupons != null) {
                    for (Model model : coupons) {
                        if (model.getBoolean("valid")) {
//                            Coupon coupon = new Coupon();
//                            JsonObjectMapper.map(model, coupon);
                            selectCoupon = model;
                            break;
                        }
                    }
                }
                updateCouponMoney();
                String phone = UserManager.getInstance().getCurrentUser().getPhone();
                validationHintTextView.setText(String.format("为了您的账户安全，请输入手机%s收到的验证码",phone));
            }
        }
    };

    private void loadData() {
        String url = ContextProvider.getBaseURL() + "/api/salerorder/pay";
        String orderIds = model == null ? this.orderIds : model.getString("");
        HttpRequest<ModelResult<Model>> req = new HttpRequest<>(url, dataListener);
        req.addParam("order_ids", orderIds);
        req.addParam("coupon_type", "-1");
        req.addParam("ticket", ticketLoader.getTicket().getValue());
        req.addParam("type","1");//上游采购。
        if(orderTypeGroup != 0 ){
            req.addParam("order_type", "" + orderTypeGroup);
        }
        req.addHeader("ticket", ticketLoader.getTicket().getValue());
        req.setParser(new GenericModelParser());
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private double balance;
    private void updateCouponMoney() {
        if (model != null) {
            double mny = model.getDouble("mny");
            totalAmountTextView.setText(String.format("总金额: ¥%s", PriceFormat.format(mny)));
            double couponMny = 0;
            if (selectCoupon != null) {
//                couponMny = selectCoupon.getMoney();
                TLog.e("xx", "238------selectCoupon=" + selectCoupon);
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
            String coupon ;
            if(couponMny <= 0){
                List<Model> coupons = model.getList("coupons", Model.class);
                if(coupons != null && !coupons.isEmpty()){
                    coupon = "暂不使用优惠券";
                }else{
                    coupon = "暂无可用优惠券";
                }
            }else{
                coupon = String.format("-¥%s", PriceFormat.format(couponMny));
            }

            Number balance = model.getNumber("balance");

            Boolean checked = (Boolean) userBalanceButton.getTag();
            if(checked == null){
                checked = true;
            }
            if(checked && balance != null){
                if(balance.doubleValue() >= totalMny){
                    alipayButton.setChecked(false);
                    buttonGroup.setEnabled(false);
                    this.balance = totalMny;
                    totalMny = 0;//TODO
                }else {
                    this.balance = balance.doubleValue();
                    totalMny -= balance.doubleValue();
                    alipayButton.setChecked(true);
                    buttonGroup.setEnabled(true);
                }
            }else{
                this.balance = 0;
                alipayButton.setChecked(true);
                buttonGroup.setEnabled(true);
            }

            couponBtnTextView.setText(coupon);

            totalMnyTextView.setText(String.format("¥%s",PriceFormat.format(totalMny)));
            balanceTextView.setText(String.format("¥%s",PriceFormat.format(this.balance)));
        }
    }

    @OnClick(R.id.use_balance_button)
    private View.OnClickListener useBalanceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean checked = (Boolean) v.getTag();
            if(checked == null){
                checked = true;
            }
            if(checked){
                validationCodeContainer.setVisibility(View.GONE);
                userBalanceButton.setImageResource(R.drawable.ic_check_not);
                validationHintTextView.setVisibility(View.GONE);
                validationDivider.setVisibility(View.GONE);
            }else{
                validationCodeContainer.setVisibility(View.VISIBLE);
                userBalanceButton.setImageResource(R.drawable.ic_checked);
                validationHintTextView.setVisibility(View.VISIBLE);
                validationDivider.setVisibility(View.VISIBLE);
            }
            v.setTag(!checked);
            updateCouponMoney();
        }
    };

    private View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Boolean checked = (Boolean) userBalanceButton.getTag();
            String validationCode = validationCodeEditText.getText().toString();
            if(checked == null){
                checked = true;
            }
            if(checked){
                //TODO validate code ;
                if(validationCode.length() < 4){
                    Toast.makeText(ContextProvider.getContext(),"请输入正确的验证码",Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Payment pay = new Payment();
            pay.setOrderId(orderIds);
            if(checked){
                pay.setValidationCode(validationCode);
                pay.setBalance(balance);//TODO
            }
            if (weixinPayButton.isChecked()) {
                WeixinPlatform weixinPay = PayService.getInstance().getWeixinPlatform();
                WeixinPayConfig config = new WeixinPayConfig();
                config.setType("1");
                weixinPay.setConfig(config);
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
                config.setType("1");
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
            if (model.getOrderId().equals(orderIds)) {
                switch (result.getEvent()) {
                    case OnPreparing:
                        showLoading();//TODO
                        break;
                    case OnPrepared:
                        hideLoading();
                        break;
                    case OnPayCanceled:
                        hideLoading();
                        Toast.makeText(ContextProvider.getContext(), "用户取消!" , Toast.LENGTH_SHORT).show();
                        break;
                    case OnPayFailed:
                        hideLoading();
                        Toast.makeText(ContextProvider.getContext(), "支付失败!" + result.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        break;
                    case OnPaySuccess:
                        if (getActivity() != null) {
                            Intent intent = new Intent(getActivity(), ContainerActivity.class);
                            intent.putExtra(ContainerActivity.KEY_CONTENT, PaySuccessViewController.class);
                            intent.putExtra(PaySuccessViewController.KEY_ORDER_TYPE_GROUP, orderTypeGroup);
                            intent.putExtra(PaySuccessViewController.KEY_PAY_TYPE, payTypeScene);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                        break;
                }
            }
        }
    };

    @OnClick(R.id.get_validation_code_button)
    private View.OnClickListener getValidationButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserModel user = UserManager.getInstance().getCurrentUser();
            if(user == null){
                return;//TODO
            }
            HttpRequest<ModelResult<Boolean>> req = new HttpRequest<>(URLApi.VALIDATE_URL, getValidationListener);
            req.addParam("phone", user.getPhone());
            req.setParser(new ModelParser<>(Boolean.class));
            req.setErrorListener(new HttpRequest.ErrorListener() {
                @Override
                public void onError(HttpRequest<?> request, HttpError eror) {
                    getValidationCodeButton.setEnabled(true);
                }
            });
            VolleyDispatcher.getInstance().dispatch(req);
            getValidationCodeButton.setEnabled(false);
        }
    };

    private HttpRequest.ResultListener<ModelResult<Boolean>> getValidationListener = new HttpRequest.ResultListener<ModelResult<Boolean>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Boolean>> request, ModelResult<Boolean> data) {
            startCounter();
//            if(data.isSuccess() && data.getModel()){
////                Toast.makeText(ContextProvider.getContext(), "发送成功",Toast.LENGTH_SHORT).show();
//            }else{
//                getValidationCodeButton.setEnabled(true);
////                Toast.makeText(ContextProvider.getContext(), "发送失败！", Toast.LENGTH_SHORT).show();
//            }
        }
    };

    private void startCounter() {
        handler.removeCallbacks(counterRun);
        counterStartTime = System.currentTimeMillis();
        handler.post(counterRun);
        getValidationCodeButton.setEnabled(false);
    }

    @Override
    public void onActivityResult(Activity a,int requestCode, int resultCode, Intent data) {
        super.onActivityResult(a,requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
//                orderStatusTextView.setText(result);
            }
//        } else if (requestCode == REQUEST_CODE_COUPON) {
//            if (resultCode == Activity.RESULT_OK) {
//                Coupon selectCoupon = (Coupon) data.getSerializableExtra(KEY_SELECT_COUPON);
////                if (selectCoupon != null) {
//                    Log.e("xx", "115--------------" + selectCoupon);
//                    this.selectCoupon = selectCoupon;
//                    updateCouponMoney();
////                }
//            }
        }
    }


    @Override
    public boolean onBackKeyDown(Activity a) {
        if(super.onBackKeyDown(a)){
            return true;
        }
        ActionSheetDialog dialog = new ActionSheetDialog();
        dialog.setActionSheet(getActionSheet(true));
        dialog.setTitle("是否确认放弃付款？");
        dialog.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextIntent != null){
                    Log.e("xx","nextIntent:" + nextIntent);
                    Log.e("xx","nextIntent:" + nextIntent.getExtras());
                    getActivity().startActivity(nextIntent);
                }
                getActivity().finish();
            }
        });
        dialog.show();
        return true;
    }

    private Runnable counterRun = new Runnable() {
        @Override
        public void run() {
            int secondsPassed = (int) ((System.currentTimeMillis() - counterStartTime) / 1000);
            secondsPassed = 60 - secondsPassed;
            if (secondsPassed < 0) {//TODO define a constant
                handler.removeCallbacks(this);
                getValidationCodeButton.setEnabled(true);
                getValidationCodeButton.setText("重新获取");
            } else {
                handler.postDelayed(this, 1000);
                getValidationCodeButton.setText(String.format("重新获取(%d)", secondsPassed));
            }
        }
    };
}
