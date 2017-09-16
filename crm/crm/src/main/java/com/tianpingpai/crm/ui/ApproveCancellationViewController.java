package com.tianpingpai.crm.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.crm.R;
import com.tianpingpai.http.HttpRequest;
import com.tianpingpai.http.volley.VolleyDispatcher;
import com.tianpingpai.manager.UserManager;
import com.tianpingpai.model.OrderLog;
import com.tianpingpai.model.OrderModel;
import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheetDialog;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.CommonErrorHandler;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.ui.Statistics;
import com.tianpingpai.user.UserModel;

@SuppressWarnings("unused")
@Statistics(page = "拒绝")
@ActionBar(title = "审批")
@Layout(id = R.layout.fragment_approve_cancellation)
public class ApproveCancellationViewController extends BaseViewController{

    public static final String KEY_ORDER_ID = "key.OrderId";

    private long orderId = -1;

    @Binding(id = R.id.order_id_text_view,format = "订单号:{{order_id}}")
    private TextView orderIdTextView;
    @Binding(id = R.id.store_name_text_view,format = "{{sale_name}}")
    private TextView storeNameTextView;
    @Binding(id = R.id.order_time_text_view,format = "下单时间:{{oper_dt}}")
    private TextView orderTimeTextView;
    @Binding(id = R.id.amount_text_view,format = "金额:￥{{mny|money}}")
    private TextView amountTextView;
    @Binding(id = R.id.buyer_name_text_view,format = "买家:{{b_display_name}}")
    private TextView buyerNameTextView;
    @Binding(id = R.id.seller_name_text_view,format = "卖家:{{s_display_name}}")
    private TextView sellerNameTextView;
    @Binding(id = R.id.buyer_phone_text_view,format = "买家电话:{{b_phone}}")
    private TextView buyerPhoneTextView;
    @Binding(id = R.id.seller_phone_text_view,format = "卖家电话:{{s_phone}}")
    private TextView sellerPhoneTextView;
    @Binding(id = R.id.cancel_reason_text_view)
    private TextView cancelReasonTextView;

    @Override
    protected void onConfigureView(View v) {
        super.onConfigureView(v);
        orderId = getActivity().getIntent().getLongExtra(KEY_ORDER_ID,-1);
        loadData();
    }

    private void loadData(){
        String url = ContextProvider.getBaseURL() + "/crm/order/examine";
        UserModel user = UserManager.getInstance().getCurrentUser();

        HttpRequest<ModelResult<OrderLog>> req = new HttpRequest<>(url,listener);
        req.addParam("order_id","" + orderId);
        req.setParser(new ModelParser<>(OrderLog.class));
        if(user != null){
            req.addParam("accessToken",user.getAccessToken());
        }
        VolleyDispatcher.getInstance().dispatch(req);
        showLoading();
    }

    private void fillData(OrderLog model){
        getBinder().bindData(model);
    }

    @OnClick(R.id.approve_button)
    private View.OnClickListener  approveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setTitle("确定要同意吗");
            dialog.setActivity(getActivity());
            dialog.setActionSheet(getActionSheet(true));
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    commit(true);
                }
            });
            dialog.show();
        }
    };

    @OnClick(R.id.disapprove_button)
    private View.OnClickListener disapproveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ActionSheetDialog dialog = new ActionSheetDialog();
            dialog.setTitle("确定要拒绝吗");
            dialog.setActionSheet(getActionSheet(true));
            dialog.setPositiveButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    commit(false);
                }
            });
            dialog.show();
        }
    };

    private HttpRequest.ResultListener<ModelResult<OrderLog>> listener = new HttpRequest.ResultListener<ModelResult<OrderLog>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<OrderLog>> request, ModelResult<OrderLog> data) {
            hideLoading();
            if(data.isSuccess()){
                showContent();
                fillData(data.getModel());
            }else {
                Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    private HttpRequest.ResultListener<ModelResult<Boolean>> commitListener = new HttpRequest.ResultListener<ModelResult<Boolean>>() {
        @Override
        public void onResult(HttpRequest<ModelResult<Boolean>> request, ModelResult<Boolean> data) {
            Log.e("xx", "approve:" + data.isSuccess());
            Toast.makeText(ContextProvider.getContext(),data.getDesc(),Toast.LENGTH_SHORT).show();
            if(data.isSuccess() && getActivity() != null){
                getActivity().finish();
            }
        }
    };

    private void commit(boolean approve){
        String url = ContextProvider.getBaseURL() + "/api/order/updateOrderStatus.json";
        UserModel user = UserManager.getInstance().getCurrentUser();
        HttpRequest<ModelResult<Boolean>> req = new HttpRequest<>(url,commitListener);
        req.setMethod(HttpRequest.POST);
        req.addParam("user_type","2");
        if(approve){
            req.addParam("status",String.valueOf(OrderModel.STATUS_CANCELED));
        }else{
            req.addParam("status",String.valueOf(OrderModel.STATUS_NO_PAYED));
        }
        req.addParam("order_id",String.valueOf(orderId));
        req.setParser(new ModelParser<>(Boolean.class));
        req.setErrorListener(new CommonErrorHandler(this));

        if(user != null){
            req.addParam("accessToken",user.getAccessToken());
            req.addParam("marketer_id","" + user.getId());
        }
        VolleyDispatcher.getInstance().dispatch(req);
    }
}
